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

    fun getAuthenticatedUserId(): String? {
        return try {
            val authClass = Class.forName("com.google.firebase.auth.FirebaseAuth")
            val authInstance = authClass.getMethod("getInstance").invoke(null)
            val currentUser = authClass.getMethod("getCurrentUser").invoke(authInstance)
            currentUser?.javaClass?.getMethod("getUid")?.invoke(currentUser) as? String
        } catch (_: Throwable) { null }
    }

    fun startBackgroundSync(scope: CoroutineScope, initialPairCode: String = "") {
        currentPairCode = initialPairCode.trim().uppercase()
        syncJob?.cancel()
        syncJob = scope.launch(Dispatchers.IO) {
            val authUid = getAuthenticatedUserId()
            if (authUid == null && currentPairCode.isBlank()) {
                _syncState.value = SyncState.OFFLINE
            }
            refreshPendingStatus()
        }
    }

    fun updateHouseholdPairCode(scope: CoroutineScope, newPairCode: String) {
        currentPairCode = newPairCode.trim().uppercase()
        scope.launch(Dispatchers.IO) { refreshPendingStatus() }
    }

    fun setSyncing() { _syncState.value = SyncState.SYNCING }
    fun setOffline() { _syncState.value = SyncState.OFFLINE }
    fun setError() { _syncState.value = SyncState.ERROR }

    suspend fun refreshPendingStatus() = withContext(Dispatchers.IO) {
        val pendingTxs = dao.getPendingTransactions()
        val pendingWallets = dao.getPendingWallets()
        val pendingCats = dao.getPendingCategories()
        val pendingMembers = dao.getPendingMembers()
        val pendingLedgers = auditDao.getPendingLedgerEvents()
        val pendingGroups = categoryGroupDao?.getPendingCategoryGroups() ?: emptyList()

        val totalPending = pendingTxs.size + pendingWallets.size + pendingCats.size + pendingMembers.size + pendingLedgers.size + pendingGroups.size
        _pendingCount.value = totalPending
        _syncState.value = if (totalPending > 0) SyncState.LOCAL_PENDING else SyncState.IDLE
    }

    suspend fun markSynchronizedAfterHandshake() = withContext(Dispatchers.IO) {
        dao.getPendingTransactions().takeIf { it.isNotEmpty() }?.let { dao.markTransactionsSynced(it.map { tx -> tx.id }) }
        dao.getPendingWallets().takeIf { it.isNotEmpty() }?.let { dao.markWalletsSynced(it.map { w -> w.id }) }
        dao.getPendingCategories().takeIf { it.isNotEmpty() }?.let { dao.markCategoriesSynced(it.map { c -> c.id }) }
        dao.getPendingMembers().takeIf { it.isNotEmpty() }?.let { dao.markMembersSynced(it.map { m -> m.id }) }
        auditDao.getPendingLedgerEvents().takeIf { it.isNotEmpty() }?.let { auditDao.markLedgerEventsSynced(it.map { l -> l.eventId }) }
        categoryGroupDao?.getPendingCategoryGroups()?.takeIf { it.isNotEmpty() }?.let { categoryGroupDao.markCategoryGroupsSynced(it.map { g -> g.id }) }

        _pendingCount.value = 0
        _syncState.value = SyncState.SYNCED
    }

    fun forceSyncNow(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) { refreshPendingStatus() }
    }
}

