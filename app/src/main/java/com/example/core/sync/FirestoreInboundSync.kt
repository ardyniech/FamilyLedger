package com.example.core.sync

import com.example.core.storage.HouseholdDao
import kotlinx.coroutines.CoroutineScope

class FirestoreInboundSync(
    private val dao: HouseholdDao,
    private val firestore: Any?
) {
    fun startListening(pairCode: String, scope: CoroutineScope) {
        // Firebase Firestore has been removed, operating in local-only mode.
    }

    fun stopListening() {
        // Firebase Firestore has been removed, operating in local-only mode.
    }
}
