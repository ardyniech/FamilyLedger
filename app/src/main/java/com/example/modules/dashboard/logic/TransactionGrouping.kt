package com.example.modules.dashboard.logic

import com.example.shared.models.Transaction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyTransactionGroup(
    val dateKey: String,
    val displayHeader: String,
    val dayTotalInflow: Long,
    val dayTotalOutflow: Long,
    val transactions: List<Transaction>
)

object TransactionGroupingHelper {
    private val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val fullDateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))

    fun groupByDay(transactions: List<Transaction>): List<DailyTransactionGroup> {
        if (transactions.isEmpty()) return emptyList()

        val todayKey = keyFormat.format(Date())
        val yesterdayKey = keyFormat.format(Date(System.currentTimeMillis() - 86400000L))

        val sorted = transactions.sortedByDescending { it.timestamp }
        val groupedMap = sorted.groupBy { keyFormat.format(Date(it.timestamp)) }

        return groupedMap.map { (dateKey, list) ->
            val dateObj = list.firstOrNull()?.let { Date(it.timestamp) } ?: Date()
            val headerText = when (dateKey) {
                todayKey -> "Hari Ini • ${fullDateFormat.format(dateObj)}"
                yesterdayKey -> "Kemarin • ${fullDateFormat.format(dateObj)}"
                else -> fullDateFormat.format(dateObj)
            }

            val inflow = list.filter { it.amount > 0 }.sumOf { it.amount }
            val outflow = list.filter { it.amount < 0 }.sumOf { -it.amount }

            DailyTransactionGroup(
                dateKey = dateKey,
                displayHeader = headerText,
                dayTotalInflow = inflow,
                dayTotalOutflow = outflow,
                transactions = list
            )
        }
    }
}
