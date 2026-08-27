package com.example.modules.dashboard.logic

import com.example.shared.models.RecurringBill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class RecurringBillsManager {
    private val _recurringBills = MutableStateFlow<List<RecurringBill>>(
        listOf(
            RecurringBill("rb1", "Netflix Subscription", 186000.0, "Aug 28, 2026", "c3", isPaid = false, autoPay = true, targetWalletId = "w1", frequency = "Monthly"),
            RecurringBill("rb2", "Indihome Home Wifi", 385000.0, "Sep 01, 2026", "c3", isPaid = false, autoPay = false, targetWalletId = null, frequency = "Monthly"),
            RecurringBill("rb3", "PDAM Clean Water", 120000.0, "Sep 05, 2026", "c3", isPaid = false, autoPay = false, targetWalletId = null, frequency = "Monthly"),
            RecurringBill("rb4", "Home Rent Payment", 2500000.0, "Sep 10, 2026", "c3", isPaid = false, autoPay = true, targetWalletId = "w2", frequency = "Monthly")
        )
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
    }

    fun deleteRecurringBill(billId: String) {
        _recurringBills.value = _recurringBills.value.filter { it.id != billId }
    }

    fun addRecurringBill(
        name: String,
        amount: Double,
        dueDate: String,
        categoryId: String,
        autoPay: Boolean = false,
        targetWalletId: String? = null,
        frequency: String = "Monthly"
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
            isPaid = false
        )
        _recurringBills.value = _recurringBills.value + newBill
    }

    private fun getNextDueDate(currentDueDateStr: String, frequency: String): String {
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val date = try {
            format.parse(currentDueDateStr) ?: Date()
        } catch (e: Exception) {
            Date()
        }
        val cal = Calendar.getInstance()
        cal.time = date
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
