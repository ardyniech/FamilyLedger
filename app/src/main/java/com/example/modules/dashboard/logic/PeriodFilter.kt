package com.example.modules.dashboard.logic

import com.example.shared.models.Transaction
import java.util.Calendar

enum class DashboardPeriod(val displayName: String, val shortName: String) {
    DAILY("Harian", "Hari"),
    WEEKLY("Mingguan", "Minggu"),
    MONTHLY("Bulanan", "Bulan"),
    QUARTERLY("Kuartal (3 Bln)", "Kuartal"),
    SEMESTER("Semester (6 Bln)", "Semester"),
    YEARLY("Tahunan", "Tahun")
}

data class PeriodSummary(
    val period: DashboardPeriod,
    val totalInflow: Double,
    val totalOutflow: Double,
    val netBalance: Double,
    val adjustedBudget: Double,
    val transactionCount: Int
)

object PeriodFilterHelper {
    fun getPeriodDateRange(period: DashboardPeriod, nowMillis: Long = System.currentTimeMillis()): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val endCal = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }
        val startCal = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }

        when (period) {
            DashboardPeriod.DAILY -> {}
            DashboardPeriod.WEEKLY -> {
                startCal.set(Calendar.DAY_OF_WEEK, startCal.firstDayOfWeek)
            }
            DashboardPeriod.MONTHLY -> {
                startCal.set(Calendar.DAY_OF_MONTH, 1)
            }
            DashboardPeriod.QUARTERLY -> {
                val currentMonth = startCal.get(Calendar.MONTH)
                val quarterStartMonth = (currentMonth / 3) * 3
                startCal.set(Calendar.MONTH, quarterStartMonth)
                startCal.set(Calendar.DAY_OF_MONTH, 1)
            }
            DashboardPeriod.SEMESTER -> {
                val currentMonth = startCal.get(Calendar.MONTH)
                val semesterStartMonth = if (currentMonth < 6) 0 else 6
                startCal.set(Calendar.MONTH, semesterStartMonth)
                startCal.set(Calendar.DAY_OF_MONTH, 1)
            }
            DashboardPeriod.YEARLY -> {
                startCal.set(Calendar.MONTH, 0)
                startCal.set(Calendar.DAY_OF_MONTH, 1)
            }
        }
        return Pair(startCal.timeInMillis, endCal.timeInMillis)
    }

    fun filterTransactions(transactions: List<Transaction>, period: DashboardPeriod, refMillis: Long = System.currentTimeMillis()): List<Transaction> {
        val (start, end) = getPeriodDateRange(period, refMillis)
        return transactions.filter { it.timestamp in start..end }
    }

    fun calculateSummary(transactions: List<Transaction>, period: DashboardPeriod, baseMonthlyBudget: Double): PeriodSummary {
        val filtered = filterTransactions(transactions, period)
        val inflow = filtered.filter { it.amount > 0 }.sumOf { it.amount }
        val outflow = filtered.filter { it.amount < 0 }.sumOf { -it.amount }
        val multiplier = when (period) {
            DashboardPeriod.DAILY -> 1.0 / 30.0
            DashboardPeriod.WEEKLY -> 7.0 / 30.0
            DashboardPeriod.MONTHLY -> 1.0
            DashboardPeriod.QUARTERLY -> 3.0
            DashboardPeriod.SEMESTER -> 6.0
            DashboardPeriod.YEARLY -> 12.0
        }
        return PeriodSummary(
            period = period,
            totalInflow = inflow,
            totalOutflow = outflow,
            netBalance = inflow - outflow,
            adjustedBudget = baseMonthlyBudget * multiplier,
            transactionCount = filtered.size
        )
    }
}
