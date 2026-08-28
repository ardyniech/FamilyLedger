package com.example.core.sync

import android.util.Log
import com.example.core.storage.HouseholdDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SyncEngine(private val dao: HouseholdDao) {
    
    private val _syncState = MutableStateFlow(SyncState.IDLE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private var currentPairCode: String = "FAM-8821"
    private var syncJob: Job? = null

    init {
    }

    fun startBackgroundSync(scope: CoroutineScope, initialPairCode: String = "FAM-8821") {
        currentPairCode = initialPairCode

        syncJob?.cancel()
        syncJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    _syncState.value = SyncState.IDLE
                } catch (e: Exception) {
                    _syncState.value = SyncState.OFFLINE
                }
                delay(3000)
            }
        }
    }

    fun updateHouseholdPairCode(scope: CoroutineScope, newPairCode: String) {
        currentPairCode = newPairCode.trim().uppercase()
        scope.launch(Dispatchers.IO) {
            _syncState.value = SyncState.SYNCED
        }
    }

    fun forceSyncNow(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            _syncState.value = SyncState.SYNCED
        }
    }
}
