package com.example.core.sync

import android.util.Log
import com.example.core.storage.HouseholdDao
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SyncEngine(private val dao: HouseholdDao) {
    
    private val _syncState = MutableStateFlow(SyncState.IDLE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private var firestoreInstance: FirebaseFirestore? = null
    private var outboundSync: FirestoreOutboundSync? = null
    private var inboundSync: FirestoreInboundSync? = null
    private var currentPairCode: String = "FAM-8821"
    private var syncJob: Job? = null

    init {
        try {
            val firestore = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            firestore.firestoreSettings = settings
            firestoreInstance = firestore
            outboundSync = FirestoreOutboundSync(dao, firestore)
            inboundSync = FirestoreInboundSync(dao, firestore)
        } catch (e: Exception) {
            Log.w("SyncEngine", "Firestore initialization unavailable, operating in local-only mode: ${e.message}")
            outboundSync = FirestoreOutboundSync(dao, null)
            inboundSync = FirestoreInboundSync(dao, null)
        }
    }

    fun startBackgroundSync(scope: CoroutineScope, initialPairCode: String = "FAM-8821") {
        currentPairCode = initialPairCode
        inboundSync?.startListening(currentPairCode, scope)

        syncJob?.cancel()
        syncJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    val synced = outboundSync?.syncPendingToCloud(currentPairCode) ?: false
                    if (synced) {
                        _syncState.value = SyncState.SYNCED
                        delay(1500)
                    }
                    _syncState.value = SyncState.IDLE
                } catch (e: Exception) {
                    _syncState.value = SyncState.OFFLINE
                    Log.w("SyncEngine", "Sync offline or waiting for connectivity: ${e.message}")
                }
                delay(3000)
            }
        }
    }

    fun updateHouseholdPairCode(scope: CoroutineScope, newPairCode: String) {
        currentPairCode = newPairCode.trim().uppercase()
        inboundSync?.startListening(currentPairCode, scope)
        scope.launch(Dispatchers.IO) {
            try {
                _syncState.value = SyncState.SYNCING
                outboundSync?.syncPendingToCloud(currentPairCode)
                _syncState.value = SyncState.SYNCED
            } catch (e: Exception) {
                _syncState.value = SyncState.OFFLINE
            }
        }
    }

    fun forceSyncNow(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                _syncState.value = SyncState.SYNCING
                outboundSync?.syncPendingToCloud(currentPairCode)
                _syncState.value = SyncState.SYNCED
            } catch (e: Exception) {
                _syncState.value = SyncState.ERROR
            }
        }
    }
}
