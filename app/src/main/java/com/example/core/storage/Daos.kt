package com.example.core.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction as DbTransaction
import com.example.shared.models.Member
import com.example.shared.models.WalletAccount
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface HouseholdDao {
    @Query("SELECT * FROM members WHERE isDeleted = 0")
    fun getAllMembers(): Flow<List<Member>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<Member>)

    @Query("SELECT * FROM wallet_accounts WHERE isDeleted = 0")
    fun getAllWallets(): Flow<List<WalletAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallets(wallets: List<WalletAccount>)

    @Query("SELECT * FROM categories WHERE isDeleted = 0")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEvent(event: LedgerEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransferEvent(transfer: TransferEventEntity)

    @Query("UPDATE wallet_accounts SET balance = balance + :amount, syncStatus = 0, updatedAt = :updatedAt WHERE id = :walletId")
    suspend fun updateWalletBalance(walletId: String, amount: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE transactions SET isDeleted = 1, syncStatus = 0, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteTransaction(id: String, updatedAt: Long = System.currentTimeMillis())

    @DbTransaction
    suspend fun addTransactionWithAudit(transaction: Transaction, ledgerEvent: LedgerEventEntity) {
        insertTransaction(transaction)
        updateWalletBalance(transaction.walletId, transaction.amount)
        insertLedgerEvent(ledgerEvent)
    }

    @DbTransaction
    suspend fun deleteTransactionWithAudit(transaction: Transaction, ledgerEvent: LedgerEventEntity) {
        softDeleteTransaction(transaction.id)
        updateWalletBalance(transaction.walletId, -transaction.amount)
        insertLedgerEvent(ledgerEvent)
    }

    @DbTransaction
    suspend fun updateTransactionWithAudit(oldTx: Transaction, newTx: Transaction, ledgerEvent: LedgerEventEntity) {
        updateWalletBalance(oldTx.walletId, -oldTx.amount)
        updateWalletBalance(newTx.walletId, newTx.amount)
        insertTransaction(newTx)
        insertLedgerEvent(ledgerEvent)
    }

    @DbTransaction
    suspend fun executeAtomicTransferWithAudit(
        debitTx: Transaction,
        creditTx: Transaction,
        transferEvent: TransferEventEntity,
        debitLedgerEvent: LedgerEventEntity,
        creditLedgerEvent: LedgerEventEntity
    ) {
        insertTransaction(debitTx)
        insertTransaction(creditTx)
        updateWalletBalance(debitTx.walletId, debitTx.amount)
        updateWalletBalance(creditTx.walletId, creditTx.amount)
        insertTransferEvent(transferEvent)
        insertLedgerEvent(debitLedgerEvent)
        insertLedgerEvent(creditLedgerEvent)
    }

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: String): Transaction?

    @Query("SELECT * FROM wallet_accounts WHERE id = :id LIMIT 1")
    suspend fun getWalletById(id: String): WalletAccount?

    @Query("SELECT * FROM transactions WHERE syncStatus = 0")
    suspend fun getPendingTransactions(): List<Transaction>

    @Query("UPDATE transactions SET syncStatus = 1 WHERE id IN (:ids)")
    suspend fun markTransactionsSynced(ids: List<String>)

    @Query("SELECT * FROM categories WHERE syncStatus = 0")
    suspend fun getPendingCategories(): List<Category>

    @Query("UPDATE categories SET syncStatus = 1 WHERE id IN (:ids)")
    suspend fun markCategoriesSynced(ids: List<String>)

    @Query("SELECT * FROM wallet_accounts WHERE syncStatus = 0")
    suspend fun getPendingWallets(): List<WalletAccount>

    @Query("UPDATE wallet_accounts SET syncStatus = 1 WHERE id IN (:ids)")
    suspend fun markWalletsSynced(ids: List<String>)

    @Query("SELECT * FROM members WHERE syncStatus = 0")
    suspend fun getPendingMembers(): List<Member>

    @Query("UPDATE members SET syncStatus = 1 WHERE id IN (:ids)")
    suspend fun markMembersSynced(ids: List<String>)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM wallet_accounts")
    suspend fun clearWallets()

    @Query("DELETE FROM categories")
    suspend fun clearCategories()

    @Query("DELETE FROM members")
    suspend fun clearMembers()

    @DbTransaction
    suspend fun clearAllData() {
        clearTransactions()
        clearWallets()
        clearCategories()
        clearMembers()
    }
}


