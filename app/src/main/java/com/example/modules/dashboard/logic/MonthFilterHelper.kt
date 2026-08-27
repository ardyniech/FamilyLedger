package com.example.modules.dashboard.logic

import com.example.shared.models.Transaction
import java.util.Calendar

data class MonthPeriodData(
    val monthOffset: Int,
    val startMillis: Long,
    val endMillis: Long,
    val transactions: List<Transaction>,
    val totalIncome: Double,
    val totalExpense: Double,
    val netBalance: Double
)

object MonthFilterHelper {
    fun getMonthRange(monthOffset: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, monthOffset)
        
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startMillis = cal.timeInMillis

        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, maxDay)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val endMillis = cal.timeInMillis

        return Pair(startMillis, endMillis)
    }

    fun filterForMonth(transactions: List<Transaction>, monthOffset: Int): MonthPeriodData {
        val (start, end) = getMonthRange(monthOffset)
        val filtered = transactions.filter { it.timestamp in start..end }
        val income = filtered.filter { it.amount > 0 }.sumOf { it.amount }
        val expense = filtered.filter { it.amount < 0 }.sumOf { -it.amount }

        return MonthPeriodData(
            monthOffset = monthOffset,
            startMillis = start,
            endMillis = end,
            transactions = filtered,
            totalIncome = income,
            totalExpense = expense,
            netBalance = income - expense
        )
    }
}
