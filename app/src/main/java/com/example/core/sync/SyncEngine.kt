package com.example.core.sync

import com.example.core.storage.CategoryGroupDao
import com.example.core.storage.HouseholdDao
import com.example.core.storage.LedgerAuditDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SyncEngine(
    private val dao: HouseholdDao,
    private val auditDao: LedgerAuditDao,
    private val categoryGroupDao: CategoryGroupDao? = null
) {
    private val _syncState = MutableStateFlow(SyncState.IDLE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _pendingCount = MutableStateFlow(0)
    val pendingCount: StateFlow<Int> = _pendingCount.asStateFlow()

    private var currentPairCode: String = ""
    private var syncJob: Job? = null

    fun startBackgroundSync(scope: CoroutineScope, initialPairCode: String = "") {
        currentPairCode = initialPairCode
        syncJob?.cancel()
        syncJob = scope.launch(Dispatchers.IO) {
            refreshPendingStatus()
        }
    }

    fun updateHouseholdPairCode(scope: CoroutineScope, newPairCode: String) {
        currentPairCode = newPairCode.trim().uppercase()
        scope.launch(Dispatchers.IO) {
            refreshPendingStatus()
        }
    }

    fun setSyncing() {
        _syncState.value = SyncState.SYNCING
    }

    fun setOffline() {
        _syncState.value = SyncState.OFFLINE
    }

    fun setError() {
        _syncState.value = SyncState.ERROR
    }

    suspend fun refreshPendingStatus() = withContext(Dispatchers.IO) {
        val pendingTxs = dao.getPendingTransactions()
        val pendingWallets = dao.getPendingWallets()
        val pendingCats = dao.getPendingCategories()
        val pendingMembers = dao.getPendingMembers()
        val pendingLedgers = auditDao.getPendingLedgerEvents()
        val pendingGroups = categoryGroupDao?.getPendingCategoryGroups() ?: emptyList()

        val totalPending = pendingTxs.size + pendingWallets.size + pendingCats.size + pendingMembers.size + pendingLedgers.size + pendingGroups.size
        _pendingCount.value = totalPending

        if (totalPending > 0) {
            _syncState.value = SyncState.LOCAL_PENDING
        } else {
            _syncState.value = SyncState.IDLE
        }
    }

    suspend fun markSynchronizedAfterHandshake() = withContext(Dispatchers.IO) {
        val pendingTxs = dao.getPendingTransactions()
        if (pendingTxs.isNotEmpty()) {
            pendingTxs.map { it.id }.chunked(500).forEach { dao.markTransactionsSynced(it) }
        }

        val pendingWallets = dao.getPendingWallets()
        if (pendingWallets.isNotEmpty()) {
            pendingWallets.map { it.id }.chunked(500).forEach { dao.markWalletsSynced(it) }
        }

        val pendingCats = dao.getPendingCategories()
        if (pendingCats.isNotEmpty()) {
            pendingCats.map { it.id }.chunked(500).forEach { dao.markCategoriesSynced(it) }
        }

        val pendingMembers = dao.getPendingMembers()
        if (pendingMembers.isNotEmpty()) {
            pendingMembers.map { it.id }.chunked(500).forEach { dao.markMembersSynced(it) }
        }

        val pendingLedgers = auditDao.getPendingLedgerEvents()
        if (pendingLedgers.isNotEmpty()) {
            pendingLedgers.map { it.eventId }.chunked(500).forEach { auditDao.markLedgerEventsSynced(it) }
        }

        val pendingGroups = categoryGroupDao?.getPendingCategoryGroups() ?: emptyList()
        if (pendingGroups.isNotEmpty()) {
            pendingGroups.map { it.id }.chunked(500).forEach { categoryGroupDao?.markCategoryGroupsSynced(it) }
        }

        _pendingCount.value = 0
        _syncState.value = SyncState.SYNCED
    }

    fun forceSyncNow(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            refreshPendingStatus()
        }
    }
}

