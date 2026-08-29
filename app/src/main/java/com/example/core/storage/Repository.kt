package com.example.core.storage

import com.example.shared.models.*
import com.example.core.sync.SyncEngine
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class HouseholdRepository(
    val dao: HouseholdDao,
    val auditDao: LedgerAuditDao,
    val categoryGroupDao: CategoryGroupDao
) {
    val syncEngine = SyncEngine(dao, auditDao, categoryGroupDao)
    val p2pSyncManager = com.example.core.sync.p2p.P2POfflineSyncManager(dao)
    val ledgerEngine = LedgerEngineService(auditDao)

    val members: Flow<List<Member>> = dao.getAllMembers()
    val wallets: Flow<List<WalletAccount>> = dao.getAllWallets()
    val categories: Flow<List<Category>> = dao.getAllCategories()
    val categoryGroups: Flow<List<CategoryGroup>> = categoryGroupDao.getAllCategoryGroups()
    val transactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val ledgerEvents: Flow<List<LedgerEventEntity>> = auditDao.getAllLedgerEvents()
    val internalTransfers: Flow<List<TransferEventEntity>> = auditDao.getAllTransfers()

    suspend fun addMember(member: Member) {
        dao.insertMember(member)
        syncEngine.refreshPendingStatus()
    }
    suspend fun addWallet(wallet: WalletAccount) {
        dao.insertWallet(wallet)
        syncEngine.refreshPendingStatus()
    }
    suspend fun addCategory(category: Category) {
        dao.insertCategory(category)
        syncEngine.refreshPendingStatus()
    }
    suspend fun addCategoryGroup(group: CategoryGroup) {
        categoryGroupDao.insertCategoryGroup(group)
        syncEngine.refreshPendingStatus()
    }
    suspend fun insertCategoryGroups(groups: List<CategoryGroup>) {
        categoryGroupDao.insertCategoryGroups(groups)
        syncEngine.refreshPendingStatus()
    }
    suspend fun deleteCategoryGroup(id: String) {
        categoryGroupDao.deleteCategoryGroup(id)
        syncEngine.refreshPendingStatus()
    }
    suspend fun deleteCategoryGroup(group: CategoryGroup) {
        categoryGroupDao.deleteCategoryGroup(group.id)
        syncEngine.refreshPendingStatus()
    }
    
    suspend fun addTransaction(transaction: Transaction, householdId: String = "FAM-DEFAULT", actorId: String = "") {
        val eventType = if (transaction.amount >= 0) "INCOME" else "EXPENSE"
        val ledgerEvent = ledgerEngine.createEvent(
            householdId = householdId,
            entityId = transaction.id,
            actorId = if (actorId.isNotEmpty()) actorId else transaction.memberId,
            deviceId = "DEVICE_LOCAL",
            eventType = eventType,
            amount = transaction.amount,
            reason = transaction.note
        )
        dao.addTransactionWithAudit(transaction, ledgerEvent)
        syncEngine.refreshPendingStatus()
    }

    suspend fun deleteTransaction(transaction: Transaction, householdId: String = "FAM-DEFAULT", actorId: String = "", reason: String = "User Voided") {
        val ledgerEvent = ledgerEngine.createEvent(
            householdId = householdId,
            entityId = transaction.id,
            actorId = if (actorId.isNotEmpty()) actorId else transaction.memberId,
            deviceId = "DEVICE_LOCAL",
            eventType = "REVERSAL",
            amount = -transaction.amount,
            reason = reason,
            referenceEntityId = transaction.id
        )
        dao.deleteTransactionWithAudit(transaction, ledgerEvent)
        syncEngine.refreshPendingStatus()
    }

    suspend fun updateTransaction(oldTx: Transaction, newTx: Transaction, householdId: String = "FAM-DEFAULT", actorId: String = "") {
        val eventType = if (newTx.amount >= 0) "INCOME" else "EXPENSE"
        val ledgerEvent = ledgerEngine.createEvent(
            householdId = householdId,
            entityId = newTx.id,
            actorId = if (actorId.isNotEmpty()) actorId else newTx.memberId,
            deviceId = "DEVICE_LOCAL",
            eventType = eventType,
            amount = newTx.amount,
            reason = "Update: ${newTx.note}",
            referenceEntityId = oldTx.id
        )
        dao.updateTransactionWithAudit(oldTx, newTx, ledgerEvent)
        syncEngine.refreshPendingStatus()
    }

    suspend fun executeTransfer(
        debitTx: Transaction,
        creditTx: Transaction,
        transferEvent: TransferEventEntity,
        householdId: String = "FAM-DEFAULT"
    ) {
        val debitLedger = ledgerEngine.createEvent(
            householdId = householdId,
            entityId = debitTx.id,
            actorId = debitTx.memberId,
            deviceId = "DEVICE_LOCAL",
            eventType = "TRANSFER_INTERNAL",
            amount = debitTx.amount,
            reason = "Transfer to ${creditTx.walletId}"
        )
        val creditLedger = ledgerEngine.createEvent(
            householdId = householdId,
            entityId = creditTx.id,
            actorId = creditTx.memberId,
            deviceId = "DEVICE_LOCAL",
            eventType = "TRANSFER_INTERNAL",
            amount = creditTx.amount,
            reason = "Transfer from ${debitTx.walletId}"
        )
        dao.executeAtomicTransferWithAudit(debitTx, creditTx, transferEvent, debitLedger, creditLedger)
        syncEngine.refreshPendingStatus()
    }

    suspend fun clearAllData() = dao.clearAllData()
    suspend fun insertMembers(members: List<Member>) = dao.insertMembers(members)
    suspend fun insertWallets(wallets: List<WalletAccount>) = dao.insertWallets(wallets)
    suspend fun insertCategories(categories: List<Category>) = dao.insertCategories(categories)
    suspend fun insertTransactions(transactions: List<Transaction>) = dao.insertTransactions(transactions)
}

