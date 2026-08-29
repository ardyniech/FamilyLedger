package com.example.core.storage

import com.example.shared.models.*
import com.example.core.sync.SyncEngine
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class HouseholdRepository(
    val dao: HouseholdDao,
    val auditDao: LedgerAuditDao,
    val categoryGroupDao: CategoryGroupDao,
    val database: androidx.room.RoomDatabase? = null
) {
    val syncEngine = SyncEngine(dao, auditDao, categoryGroupDao)
    val p2pSyncManager = com.example.core.sync.p2p.P2POfflineSyncManager(dao, auditDao)
    val ledgerEngine = LedgerEngineService(auditDao, database)

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
        ledgerEngine.withHouseholdLock(householdId) {
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
        }
        syncEngine.refreshPendingStatus()
    }

    suspend fun deleteTransaction(transaction: Transaction, householdId: String = "FAM-DEFAULT", actorId: String = "", reason: String = "User Voided") {
        ledgerEngine.withHouseholdLock(householdId) {
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
        }
        syncEngine.refreshPendingStatus()
    }

    suspend fun updateTransaction(oldTx: Transaction, newTx: Transaction, householdId: String = "FAM-DEFAULT", actorId: String = "") {
        val eventType = if (newTx.amount >= 0) "INCOME" else "EXPENSE"
        ledgerEngine.withHouseholdLock(householdId) {
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
        }
        syncEngine.refreshPendingStatus()
    }

    suspend fun executeTransfer(
        debitTx: Transaction,
        creditTx: Transaction,
        transferEvent: TransferEventEntity,
        householdId: String = "FAM-DEFAULT"
    ) {
        FinancialInvariants.validateTransfer(debitTx, creditTx)
        ledgerEngine.withHouseholdLock(householdId) {
            val events = ledgerEngine.createEvents(
                householdId = householdId,
                specs = listOf(
                    EventSpec(
                        entityId = debitTx.id,
                        actorId = debitTx.memberId,
                        deviceId = "DEVICE_LOCAL",
                        eventType = "TRANSFER_INTERNAL",
                        amount = debitTx.amount,
                        reason = "Transfer to ${creditTx.walletId}"
                    ),
                    EventSpec(
                        entityId = creditTx.id,
                        actorId = creditTx.memberId,
                        deviceId = "DEVICE_LOCAL",
                        eventType = "TRANSFER_INTERNAL",
                        amount = creditTx.amount,
                        reason = "Transfer from ${debitTx.walletId}"
                    )
                )
            )
            dao.executeAtomicTransferWithAudit(debitTx, creditTx, transferEvent, events[0], events[1])
        }
        syncEngine.refreshPendingStatus()
    }

    suspend fun clearAllData() = dao.clearAllData()
    suspend fun resetAllDataForTestingOrImport() = dao.clearAllData()
    suspend fun insertMembers(members: List<Member>) = dao.insertMembers(members)
    suspend fun insertWallets(wallets: List<WalletAccount>) = dao.insertWallets(wallets)
    suspend fun insertCategories(categories: List<Category>) = dao.insertCategories(categories)
    suspend fun insertTransactions(transactions: List<Transaction>) = dao.insertTransactions(transactions)
}

object FinancialInvariants {
    class InvariantViolationException(message: String) : IllegalArgumentException(message)

    fun validateTransfer(debitTx: Transaction, creditTx: Transaction) {
        if (debitTx.walletId == creditTx.walletId) {
            throw InvariantViolationException("Transfer invariant failed: Debit wallet (${debitTx.walletId}) and credit wallet (${creditTx.walletId}) must be different")
        }
        if (debitTx.amount >= 0L) {
            throw InvariantViolationException("Transfer invariant failed: Debit transaction amount must be negative, got ${debitTx.amount}")
        }
        if (creditTx.amount <= 0L) {
            throw InvariantViolationException("Transfer invariant failed: Credit transaction amount must be positive, got ${creditTx.amount}")
        }
        if (debitTx.amount != -creditTx.amount) {
            throw InvariantViolationException("Transfer invariant failed: Debit amount (${debitTx.amount}) must equal negative credit amount (-${creditTx.amount})")
        }
    }
}

