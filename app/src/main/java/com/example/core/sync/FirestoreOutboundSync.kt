package com.example.core.sync

import com.example.core.storage.HouseholdDao

class FirestoreOutboundSync(
    private val dao: HouseholdDao,
    private val firestore: Any?
) {
    suspend fun syncPendingToCloud(pairCode: String): Boolean {
        // Firebase Firestore has been removed, operating in local-only mode.
        return false
    }
}
