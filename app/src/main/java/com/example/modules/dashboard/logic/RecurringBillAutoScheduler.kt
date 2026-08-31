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
                        if (CsvDateParser.parseTimestamp(bill.dueDate) <= now) {
                            wList.find { it.id == bill.targetWalletId }?.let { payRecurringBill(bill.id, it.id, wList, recurringBillsFlow.value) }
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
        if (!processingBillIds.add(billId)) return@launch
        try {
            recurringBills.find { it.id == billId }?.let {
                if (!it.isPaid && wallets.any { w -> w.id == walletId }) {
                    actionDelegate.addTransaction(it.amount, "Paid: ${it.name}", walletId, it.categoryId, false, System.currentTimeMillis(), wallets)
                    billsManager.markBillPaid(billId)
                }
            }
        } finally {
            delay(1000L)
            processingBillIds.remove(billId)
        }
    }
}
