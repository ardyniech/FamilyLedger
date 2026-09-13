package com.example.modules.dashboard.logic

import android.content.Context
import android.widget.Toast
import com.example.core.storage.HouseholdRepository
import com.example.modules.dashboard.TransactionState
import com.example.modules.dashboard.TransferState
import com.example.shared.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DashboardTransactionOperations(
    private val repository: HouseholdRepository,
    private val actionDelegate: DashboardActionDelegate,
    private val debtManager: DebtManager,
    private val undoManager: UndoTransactionManager,
    private val recurringAutoScheduler: RecurringBillAutoScheduler,
    private val scope: CoroutineScope,
    private val context: Context,
    private val transactionState: MutableStateFlow<TransactionState>,
    private val transferState: MutableStateFlow<TransferState>
) {
    fun addTransaction(
        amt: Long, note: String, wId: String, cId: String,
        isIncome: Boolean = false, ts: Long = System.currentTimeMillis(),
        goalId: String? = null, wallets: List<WalletAccount>
    ) = scope.launch {
        transactionState.value = TransactionState.Loading
        try {
            val wallet = wallets.find { it.id == wId }
            val balanceWillBeNegative = wallet != null && !isIncome && (wallet.balance - amt < 0L)
            actionDelegate.addTransaction(amt, note, wId, cId, isIncome, ts, wallets, goalId)
            transactionState.value = TransactionState.Success
            if (balanceWillBeNegative) {
                Toast.makeText(context, "Transaksi dicatat! Peringatan: Saldo ${wallet?.name} menjadi negatif.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            transactionState.value = TransactionState.Error(e.message ?: "Gagal menyimpan transaksi")
            Toast.makeText(context, "Gagal menyimpan transaksi: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun deleteTransaction(tx: Transaction) {
        undoManager.setDeletedTransaction(tx)
        actionDelegate.deleteTransaction(tx)
    }

    fun undoDeleteTransaction(lastDeletedTx: Transaction?) {
        lastDeletedTx?.let { tx ->
            scope.launch {
                repository.addTransaction(tx)
                undoManager.clearLastDeleted()
            }
        }
    }

    fun payDebt(debtId: String, amount: Long, wallets: List<WalletAccount>, categories: List<Category>) {
        val debt = debtManager.debts.value.find { it.id == debtId }
        debtManager.payDebt(debtId, amount)
        val defaultWallet = wallets.firstOrNull()
        val defaultCategory = categories.firstOrNull { it.name.contains("Cicilan", ignoreCase = true) || it.type == "Expense" } ?: categories.firstOrNull()
        if (defaultWallet != null && defaultCategory != null && debt != null) {
            addTransaction(
                amt = amount,
                note = "Pembayaran ${debt.personName}",
                wId = defaultWallet.id,
                cId = defaultCategory.id,
                isIncome = false,
                ts = System.currentTimeMillis(),
                goalId = null,
                wallets = wallets
            )
        }
    }

    fun transferFunds(
        amount: Long, note: String, fWId: String, tWId: String,
        wallets: List<WalletAccount>, categories: List<Category>, members: List<Member>
    ) = scope.launch {
        transferState.value = TransferState.Loading
        val fromWallet = wallets.find { it.id == fWId }
        val toWallet = wallets.find { it.id == tWId }
        if (fromWallet == null || toWallet == null) {
            transferState.value = TransferState.Error("Wallet tidak ditemukan")
            Toast.makeText(context, "Wallet tidak ditemukan", Toast.LENGTH_SHORT).show()
            return@launch
        }
        if (fWId == tWId) {
            transferState.value = TransferState.Error("Tidak bisa transfer ke wallet yang sama")
            Toast.makeText(context, "Tidak bisa transfer ke wallet yang sama", Toast.LENGTH_SHORT).show()
            return@launch
        }
        if (fromWallet.balance < amount) {
            transferState.value = TransferState.Error("Saldo tidak mencukupi")
            Toast.makeText(context, "Saldo ${fromWallet.name} tidak mencukupi", Toast.LENGTH_SHORT).show()
            return@launch
        }
        try {
            actionDelegate.transferFunds(amount, note, fWId, tWId, wallets, categories, members)
            transferState.value = TransferState.Success
            Toast.makeText(context, "Transfer berhasil!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            transferState.value = TransferState.Error(e.message ?: "Transfer gagal")
            Toast.makeText(context, "Transfer gagal: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun payRecurringBill(billId: String, walletId: String, wallets: List<WalletAccount>, bills: List<RecurringBill>) =
        recurringAutoScheduler.payRecurringBill(billId, walletId, wallets, bills)
}
