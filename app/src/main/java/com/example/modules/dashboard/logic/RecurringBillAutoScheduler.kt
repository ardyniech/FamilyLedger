package com.example.modules.dashboard.logic

import com.example.modules.dashboard.csv.CsvDateParser
import com.example.shared.models.RecurringBill
import com.example.shared.models.WalletAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
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
                    val eligibleBills = bList.filter {
                        !it.isPaid && (it.autoPay || it.autoPopulateInLedger) && !processingBillIds.contains(it.id)
                    }
                    eligibleBills.forEach { bill ->
                        val isRecent = bill.lastProcessedTime > 0 && (now - bill.lastProcessedTime) < 60_000L
                        val dueTs = resolveDueTs(bill, now)
                        if (!isRecent && dueTs <= now) {
                            val targetWallet = bill.targetWalletId?.let { tid -> wList.find { it.id == tid } } ?: wList.firstOrNull()
                            if (targetWallet != null && processingBillIds.add(bill.id)) {
                                payRecurringBill(bill.id, targetWallet.id, wList, recurringBillsFlow.value)
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
                    val desc = if (bill.autoPopulateInLedger) "Tagihan Rutin: ${bill.name}" else "Paid: ${bill.name}"
                    actionDelegate.addTransaction(bill.amount, desc, walletId, bill.categoryId, false, System.currentTimeMillis(), wallets)
                    billsManager.markBillPaid(billId)
                }
            }
        } finally {
            delay(3000L)
            processingBillIds.remove(billId)
        }
    }

    private fun resolveDueTs(bill: RecurringBill, now: Long): Long {
        if (bill.dueDayOfMonth in 1..31) {
            val cal = Calendar.getInstance().apply { timeInMillis = now }
            cal.set(Calendar.DAY_OF_MONTH, bill.dueDayOfMonth.coerceAtMost(cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            return cal.timeInMillis
        }
        return CsvDateParser.parseTimestamp(bill.dueDate)
    }
}

