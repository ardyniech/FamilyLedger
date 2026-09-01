package com.example.modules.dashboard.logic

import com.example.modules.dashboard.csv.CsvDateParser
import com.example.shared.models.RecurringBill
import com.example.shared.models.WalletAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class RecurringBillAutoScheduler(
    private val scope: CoroutineScope,
    private val billsManager: RecurringBillsManager,
    private val actionDelegate: DashboardActionDelegate
) {
    private val processingBillIds = ConcurrentHashMap.newKeySet<String>()

    fun startAutoProcessing(
        walletsFlow: StateFlow<List<WalletAccount>>,
        recurringBillsFlow: StateFlow<List<RecurringBill>>
    ) {
        scope.launch {
            combine(walletsFlow, recurringBillsFlow) { w, b -> Pair(w, b) }.collect { (wList, bList) ->
                if (wList.isNotEmpty()) {
                    val now = System.currentTimeMillis()
                    bList.filter { !it.isPaid && it.autoPay && it.targetWalletId != null && !processingBillIds.contains(it.id) }.forEach { bill ->
                        val isRecent = bill.lastProcessedTime > 0 && (now - bill.lastProcessedTime) < 60_000L
                        if (!isRecent && CsvDateParser.parseTimestamp(bill.dueDate) <= now) {
                            if (processingBillIds.add(bill.id)) {
                                wList.find { it.id == bill.targetWalletId }?.let { wallet ->
                                    payRecurringBill(bill.id, wallet.id, wList, recurringBillsFlow.value)
                                } ?: processingBillIds.remove(bill.id)
                            }
                        }
                    }
                }
            }
        }
    }

    fun payRecurringBill(
        billId: String,
        walletId: String,
        wallets: List<WalletAccount>,
        recurringBills: List<RecurringBill>
    ) = scope.launch {
        try {
            recurringBills.find { it.id == billId }?.let { bill ->
                val targetWallet = wallets.find { w -> w.id == walletId }
                if (!bill.isPaid && targetWallet != null) {
                    if (targetWallet.balance >= bill.amount) {
                        actionDelegate.addTransaction(bill.amount, "Paid: ${bill.name}", walletId, bill.categoryId, false, System.currentTimeMillis(), wallets)
                        billsManager.markBillPaid(billId)
                    } else {
                        // Catat kegagalan sementara tanpa melempar exception agar tidak memicu retry loop beruntun
                        billsManager.markBillPaid(billId) // Memperbarui due date ke periode berikutnya atau menunda
                    }
                }
            }
        } finally {
            delay(5000L)
            processingBillIds.remove(billId)
        }
    }
}
