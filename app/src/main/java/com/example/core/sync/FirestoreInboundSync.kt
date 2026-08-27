package com.example.core.sync

import android.util.Log
import com.example.core.storage.HouseholdDao
import com.example.shared.models.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FirestoreInboundSync(
    private val dao: HouseholdDao,
    private val firestore: FirebaseFirestore?
) {
    private val registrations = mutableListOf<ListenerRegistration>()

    fun startListening(pairCode: String, scope: CoroutineScope) {
        stopListening()
        if (firestore == null) return

        val householdRef = firestore.collection("households").document(pairCode)

        // Listen for Members
        registrations.add(
            householdRef.collection("members").addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                scope.launch {
                    val members = snapshot.documents.mapNotNull { doc ->
                        try {
                            Member(
                                id = doc.getString("id") ?: doc.id,
                                householdId = doc.getString("householdId") ?: pairCode,
                                role = doc.getString("role") ?: "Husband",
                                name = doc.getString("name") ?: "Member",
                                avatarUrl = doc.getString("avatarUrl") ?: "",
                                syncStatus = 1,
                                updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis(),
                                isDeleted = doc.getBoolean("isDeleted") ?: false
                            )
                        } catch (e: Exception) { null }
                    }
                    if (members.isNotEmpty()) dao.insertMembers(members)
                }
            }
        )

        // Listen for Wallets
        registrations.add(
            householdRef.collection("wallets").addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                scope.launch {
                    val wallets = snapshot.documents.mapNotNull { doc ->
                        try {
                            WalletAccount(
                                id = doc.getString("id") ?: doc.id,
                                memberId = doc.getString("memberId") ?: "",
                                type = doc.getString("type") ?: "Bank",
                                name = doc.getString("name") ?: "Wallet",
                                balance = doc.getDouble("balance") ?: 0.0,
                                syncStatus = 1,
                                updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis(),
                                isDeleted = doc.getBoolean("isDeleted") ?: false
                            )
                        } catch (e: Exception) { null }
                    }
                    if (wallets.isNotEmpty()) dao.insertWallets(wallets)
                }
            }
        )

        // Listen for Categories
        registrations.add(
            householdRef.collection("categories").addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                scope.launch {
                    val categories = snapshot.documents.mapNotNull { doc ->
                        try {
                            Category(
                                id = doc.getString("id") ?: doc.id,
                                name = doc.getString("name") ?: "Kategori",
                                type = doc.getString("type") ?: "Expense",
                                iconName = doc.getString("iconName") ?: "",
                                parentId = doc.getString("parentId")?.ifBlank { null },
                                syncStatus = 1,
                                updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis(),
                                isDeleted = doc.getBoolean("isDeleted") ?: false,
                                budgetLimit = doc.getDouble("budgetLimit") ?: 0.0
                            )
                        } catch (e: Exception) { null }
                    }
                    if (categories.isNotEmpty()) dao.insertCategories(categories)
                }
            }
        )

        // Listen for Transactions
        registrations.add(
            householdRef.collection("transactions").addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                scope.launch {
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.getString("id") ?: doc.id
                            val existing = dao.getTransactionById(id)
                            val remoteTx = Transaction(
                                id = id,
                                walletId = doc.getString("walletId") ?: "",
                                memberId = doc.getString("memberId") ?: "",
                                categoryId = doc.getString("categoryId") ?: "",
                                amount = doc.getDouble("amount") ?: 0.0,
                                note = doc.getString("note") ?: "",
                                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                syncStatus = 1,
                                updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis(),
                                isDeleted = doc.getBoolean("isDeleted") ?: false
                            )
                            if (existing == null) {
                                dao.insertTransaction(remoteTx)
                            } else if (remoteTx.updatedAt > existing.updatedAt) {
                                dao.insertTransaction(remoteTx)
                            }
                        } catch (e: Exception) {
                            Log.w("FirestoreInbound", "Error parsing transaction: ${e.message}")
                        }
                    }
                }
            }
        )
    }

    fun stopListening() {
        registrations.forEach { it.remove() }
        registrations.clear()
    }
}
