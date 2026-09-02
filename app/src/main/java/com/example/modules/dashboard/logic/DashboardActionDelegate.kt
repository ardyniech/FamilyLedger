package com.example.modules.dashboard.logic

import com.example.core.storage.HouseholdRepository
import com.example.core.sync.TransferNotificationManager
import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

class DashboardActionDelegate(
    private val repository: HouseholdRepository,
    private val scope: CoroutineScope,
    private val transferNotificationManager: TransferNotificationManager
) {
    fun deleteCategory(cat: Category) = scope.launch {
        repository.addCategory(cat.copy(isDeleted = true, syncStatus = 0, updatedAt = System.currentTimeMillis()))
    }

    fun saveCategory(id: String?, name: String, type: String, parentId: String? = null, budgetLimit: Long = 0L) = scope.launch {
        repository.addCategory(Category(id ?: UUID.randomUUID().toString(), name, type, parentId = parentId, syncStatus = 0, updatedAt = System.currentTimeMillis(), budgetLimit = budgetLimit))
    }

    fun saveWalletAccount(id: String?, mId: String, type: String, name: String, bal: Long, monthlyTransferCap: Long = 0L) = scope.launch {
        repository.addWallet(WalletAccount(id ?: UUID.randomUUID().toString(), mId, type, name, bal, monthlyTransferCap = monthlyTransferCap))
    }

    suspend fun addTransaction(amt: Long, note: String, wId: String, cId: String, isIncome: Boolean, ts: Long, wallets: List<WalletAccount>, goalId: String? = null) {
        val wallet = wallets.find { it.id == wId } ?: wallets.firstOrNull()
        if (wallet != null) {
            val finalAmount = if (isIncome) amt else -amt
            repository.addTransaction(Transaction(UUID.randomUUID().toString(), wallet.id, wallet.memberId, cId, finalAmount, note, ts, goalId = goalId))
        } else {
            android.util.Log.e("DashboardActionDelegate", "Cannot add transaction: No wallets available in system.")
        }
    }

    fun deleteTransaction(tx: Transaction) = scope.launch { repository.deleteTransaction(tx) }

    fun updateTransaction(oldTx: Transaction, newTx: Transaction) = scope.launch { repository.updateTransaction(oldTx, newTx) }

    fun transferFunds(
        amount: Long,
        note: String,
        fWId: String,
        tWId: String,
        wallets: List<WalletAccount>,
        categories: List<Category>,
        members: List<Member>
    ) = scope.launch {
        val fW = wallets.find { it.id == fWId } ?: return@launch
        val tW = wallets.find { it.id == tWId } ?: return@launch
        DashboardTransferHelper.executeTransfer(amount, note, fW, tW, categories, members, repository, transferNotificationManager)
    }
}
