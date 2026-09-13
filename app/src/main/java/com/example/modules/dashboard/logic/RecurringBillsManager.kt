package com.example.modules.dashboard.logic

import android.content.Context
import com.example.shared.models.RecurringBill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class RecurringBillsManager(private val context: Context? = null) {
    private val defaultBills = listOf(
        RecurringBill("rb1", "Netflix Subscription", 186000L, "Aug 28, 2026", "c3", isPaid = false, autoPay = true, targetWalletId = "w_bca", frequency = "Monthly", dueDayOfMonth = 28, autoPopulateInLedger = true),
        RecurringBill("rb2", "Indihome Home Wifi", 385000L, "Sep 01, 2026", "c3", isPaid = false, autoPay = false, targetWalletId = null, frequency = "Monthly", dueDayOfMonth = 1, autoPopulateInLedger = true),
        RecurringBill("rb3", "PDAM Clean Water", 120000L, "Sep 05, 2026", "c3", isPaid = false, autoPay = false, targetWalletId = null, frequency = "Monthly", dueDayOfMonth = 5, autoPopulateInLedger = true),
        RecurringBill("rb4", "Sewa Rumah (Rent)", 2500000L, "Sep 10, 2026", "c3", isPaid = false, autoPay = true, targetWalletId = "w_cash", frequency = "Monthly", dueDayOfMonth = 10, autoPopulateInLedger = true)
    )

    private val _recurringBills = MutableStateFlow<List<RecurringBill>>(
        RecurringBillsStorage.loadBills(context) ?: defaultBills
    )
    val recurringBills: StateFlow<List<RecurringBill>> = _recurringBills.asStateFlow()

    fun markBillPaid(billId: String) {
        _recurringBills.value = _recurringBills.value.map { bill ->
            if (bill.id == billId) {
                if (bill.frequency == "One-Time") {
                    bill.copy(isPaid = true, lastProcessedTime = System.currentTimeMillis())
                } else {
                    val nextDate = getNextDueDate(bill.dueDate, bill.frequency)
                    bill.copy(
                        dueDate = nextDate,
                        isPaid = false,
                        lastProcessedTime = System.currentTimeMillis()
                    )
                }
            } else {
                bill
            }
        }
        RecurringBillsStorage.saveBills(context, _recurringBills.value)
    }

    fun deleteRecurringBill(billId: String) {
        _recurringBills.value = _recurringBills.value.filter { it.id != billId }
        RecurringBillsStorage.saveBills(context, _recurringBills.value)
    }

    fun addRecurringBill(
        name: String,
        amount: Long,
        dueDate: String,
        categoryId: String,
        autoPay: Boolean = false,
        targetWalletId: String? = null,
        frequency: String = "Monthly",
        dueDayOfMonth: Int = 0,
        autoPopulateInLedger: Boolean = true
    ) {
        val newBill = RecurringBill(
            id = UUID.randomUUID().toString(),
            name = name,
            amount = amount,
            dueDate = dueDate,
            categoryId = categoryId,
            autoPay = autoPay,
            targetWalletId = targetWalletId,
            frequency = frequency,
            isPaid = false,
            dueDayOfMonth = dueDayOfMonth,
            autoPopulateInLedger = autoPopulateInLedger
        )
        _recurringBills.value = _recurringBills.value + newBill
        RecurringBillsStorage.saveBills(context, _recurringBills.value)
    }

    private fun getNextDueDate(currentDueDateStr: String, frequency: String): String {
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val date = try { format.parse(currentDueDateStr) ?: Date() } catch (e: Exception) { Date() }
        val cal = Calendar.getInstance().apply { time = date }
        when (frequency) {
            "Daily" -> cal.add(Calendar.DAY_OF_YEAR, 1)
            "Weekly" -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            "Monthly" -> cal.add(Calendar.MONTH, 1)
            "Yearly" -> cal.add(Calendar.YEAR, 1)
            else -> return currentDueDateStr
        }
        return format.format(cal.time)
    }
}

