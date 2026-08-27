package com.example.core.sync

import android.util.Log
import com.example.core.storage.HouseholdDao
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreOutboundSync(
    private val dao: HouseholdDao,
    private val firestore: FirebaseFirestore?
) {
    suspend fun syncPendingToCloud(pairCode: String): Boolean {
        if (firestore == null) return false
        val householdRef = firestore.collection("households").document(pairCode)

        val pendingTxs = dao.getPendingTransactions()
        val pendingCats = dao.getPendingCategories()
        val pendingWallets = dao.getPendingWallets()
        val pendingMembers = dao.getPendingMembers()

        if (pendingTxs.isEmpty() && pendingCats.isEmpty() && pendingWallets.isEmpty() && pendingMembers.isEmpty()) {
            return false
        }

        try {
            val batch = firestore.batch()

            pendingMembers.forEach { m ->
                val data = mapOf(
                    "id" to m.id,
                    "householdId" to m.householdId,
                    "role" to m.role,
                    "name" to m.name,
                    "avatarUrl" to m.avatarUrl,
                    "updatedAt" to m.updatedAt,
                    "isDeleted" to m.isDeleted
                )
                batch.set(householdRef.collection("members").document(m.id), data, SetOptions.merge())
            }

            pendingWallets.forEach { w ->
                val data = mapOf(
                    "id" to w.id,
                    "memberId" to w.memberId,
                    "type" to w.type,
                    "name" to w.name,
                    "balance" to w.balance,
                    "updatedAt" to w.updatedAt,
                    "isDeleted" to w.isDeleted
                )
                batch.set(householdRef.collection("wallets").document(w.id), data, SetOptions.merge())
            }

            pendingCats.forEach { c ->
                val data = mapOf(
                    "id" to c.id,
                    "name" to c.name,
                    "type" to c.type,
                    "iconName" to c.iconName,
                    "parentId" to (c.parentId ?: ""),
                    "updatedAt" to c.updatedAt,
                    "isDeleted" to c.isDeleted,
                    "budgetLimit" to c.budgetLimit
                )
                batch.set(householdRef.collection("categories").document(c.id), data, SetOptions.merge())
            }

            pendingTxs.forEach { t ->
                val data = mapOf(
                    "id" to t.id,
                    "walletId" to t.walletId,
                    "memberId" to t.memberId,
                    "categoryId" to t.categoryId,
                    "amount" to t.amount,
                    "note" to t.note,
                    "timestamp" to t.timestamp,
                    "updatedAt" to t.updatedAt,
                    "isDeleted" to t.isDeleted
                )
                batch.set(householdRef.collection("transactions").document(t.id), data, SetOptions.merge())
            }

            batch.commit().await()

            if (pendingMembers.isNotEmpty()) dao.markMembersSynced(pendingMembers.map { it.id })
            if (pendingWallets.isNotEmpty()) dao.markWalletsSynced(pendingWallets.map { it.id })
            if (pendingCats.isNotEmpty()) dao.markCategoriesSynced(pendingCats.map { it.id })
            if (pendingTxs.isNotEmpty()) dao.markTransactionsSynced(pendingTxs.map { it.id })

            Log.d("FirestoreOutbound", "Successfully synced ${pendingTxs.size} txs to Firestore for $pairCode")
            return true
        } catch (e: Exception) {
            Log.w("FirestoreOutbound", "Outbound sync failed: ${e.message}")
            throw e
        }
    }
}
