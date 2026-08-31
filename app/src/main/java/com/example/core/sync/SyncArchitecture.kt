package com.example.core.sync

import com.example.core.storage.CategoryGroupDao
import com.example.core.storage.HouseholdDao
import com.example.core.storage.LedgerAuditDao
import com.example.core.sync.p2p.P2PImportResult
import com.example.core.sync.p2p.P2PSyncPackage
import kotlinx.coroutines.flow.first

interface SyncTransport {
    suspend fun sendPackage(syncPackage: P2PSyncPackage): Boolean
    suspend fun receivePackage(): P2PSyncPackage?
}

class SyncProtocol(
    private val dao: HouseholdDao,
    private val auditDao: LedgerAuditDao? = null,
    private val categoryGroupDao: CategoryGroupDao? = null
) {
    suspend fun prepareDeltaPackage(pairCode: String, senderName: String, senderRole: String): P2PSyncPackage {
        val pendingTxs = dao.getPendingTransactions()
        val pendingWallets = dao.getPendingWallets()
        val pendingCats = dao.getPendingCategories()
        val pendingMembers = dao.getPendingMembers()

        val txs = if (pendingTxs.isNotEmpty()) pendingTxs else dao.getAllTransactions().first()
        val wallets = if (pendingWallets.isNotEmpty()) pendingWallets else dao.getAllWallets().first()
        val cats = if (pendingCats.isNotEmpty()) pendingCats else dao.getAllCategories().first()
        val members = if (pendingMembers.isNotEmpty()) pendingMembers else dao.getAllMembers().first()

        return P2PSyncPackage(
            pairCode = pairCode,
            senderName = senderName,
            senderRole = senderRole,
            timestamp = System.currentTimeMillis(),
            transactions = txs,
            wallets = wallets,
            categories = cats,
            members = members
        )
    }

    suspend fun reconcileAndCommit(incomingPkg: P2PSyncPackage): P2PImportResult {
        var importedTxs = 0
        var importedWallets = 0
        var importedCats = 0

        val existingTxs = dao.getAllTransactions().first().associateBy { it.id }
        val existingWallets = dao.getAllWallets().first().associateBy { it.id }
        val existingCats = dao.getAllCategories().first().associateBy { it.id }
        val existingMembers = dao.getAllMembers().first().associateBy { it.id }

        for (m in incomingPkg.members) {
            val local = existingMembers[m.id]
            if (ConflictResolver.resolveMember(local, m) == ConflictResolution.ACCEPT_INCOMING) {
                dao.insertMember(m)
            }
        }

        for (c in incomingPkg.categories) {
            val local = existingCats[c.id]
            if (ConflictResolver.resolveCategory(local, c) == ConflictResolution.ACCEPT_INCOMING) {
                dao.insertCategory(c)
                importedCats++
            }
        }

        for (w in incomingPkg.wallets) {
            val local = existingWallets[w.id]
            if (ConflictResolver.resolveWallet(local, w) == ConflictResolution.ACCEPT_INCOMING) {
                dao.insertWallet(w)
                importedWallets++
            }
        }

        for (t in incomingPkg.transactions) {
            val local = existingTxs[t.id]
            if (ConflictResolver.resolveTransaction(local, t) == ConflictResolution.ACCEPT_INCOMING) {
                dao.insertTransaction(t)
                if (local == null) {
                    dao.updateWalletBalance(t.walletId, t.amount)
                }
                importedTxs++
            }
        }

        return P2PImportResult(
            success = true,
            importedTransactions = importedTxs,
            importedWallets = importedWallets,
            importedCategories = importedCats,
            message = "Sinkronisasi P2P berhasil via ConflictResolver LWW (Txs: $importedTxs, Wallets: $importedWallets)"
        )
    }
}
