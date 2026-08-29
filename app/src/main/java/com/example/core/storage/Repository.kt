package com.example.core.storage

import com.example.shared.models.*
import com.example.core.sync.SyncEngine
import kotlinx.coroutines.flow.Flow

class HouseholdRepository(
    val dao: HouseholdDao,
    val auditDao: LedgerAuditDao,
    val categoryGroupDao: CategoryGroupDao? = null
) {
    val syncEngine = SyncEngine(dao)
    val p2pSyncManager = com.example.core.sync.p2p.P2POfflineSyncManager(dao)
    val ledgerEngine = LedgerEngineService(auditDao)

    val members: Flow<List<Member>> = dao.getAllMembers()
    val wallets: Flow<List<WalletAccount>> = dao.getAllWallets()
    val categories: Flow<List<Category>> = dao.getAllCategories()
    val categoryGroups: Flow<List<CategoryGroup>> = categoryGroupDao?.getAllCategoryGroups() ?: kotlinx.coroutines.flow.flowOf(emptyList())
    val transactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val ledgerEvents: Flow<List<LedgerEventEntity>> = auditDao.getAllLedgerEvents()
    val internalTransfers: Flow<List<TransferEventEntity>> = auditDao.getAllTransfers()

    suspend fun addMember(member: Member) = dao.insertMember(member)
    suspend fun addWallet(wallet: WalletAccount) = dao.insertWallet(wallet)
    suspend fun addCategory(category: Category) = dao.insertCategory(category)
    suspend fun addCategoryGroup(group: CategoryGroup) = categoryGroupDao?.insertCategoryGroup(group)
    suspend fun insertCategoryGroups(groups: List<CategoryGroup>) = categoryGroupDao?.insertCategoryGroups(groups)
    suspend fun deleteCategoryGroup(id: String) = categoryGroupDao?.deleteCategoryGroup(id)
    suspend fun deleteCategoryGroup(group: CategoryGroup) = categoryGroupDao?.deleteCategoryGroup(group.id)
    
    suspend fun addTransaction(transaction: Transaction) {
        dao.addTransactionAndUpdateWallet(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransactionAndUpdateWallet(transaction)
    }

    suspend fun updateTransaction(oldTx: Transaction, newTx: Transaction) {
        dao.updateTransactionAndUpdateWallet(oldTx, newTx)
    }

    suspend fun clearAllData() = dao.clearAllData()
    suspend fun insertMembers(members: List<Member>) = dao.insertMembers(members)
    suspend fun insertWallets(wallets: List<WalletAccount>) = dao.insertWallets(wallets)
    suspend fun insertCategories(categories: List<Category>) = dao.insertCategories(categories)
    suspend fun insertTransactions(transactions: List<Transaction>) = dao.insertTransactions(transactions)
}
