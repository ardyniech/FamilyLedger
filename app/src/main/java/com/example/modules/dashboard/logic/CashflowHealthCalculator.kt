package com.example.modules.dashboard.logic

import com.example.shared.models.Transaction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

data class MonthlyCashflowPoint(
    val monthLabel: String,
    val totalIncome: Long,
    val totalExpense: Long,
    val netCashflow: Long
)

data class CashflowHealthSummary(
    val currentPeriodIncome: Long,
    val currentPeriodExpense: Long,
    val netCashflow: Long,
    val isSurplus: Boolean,
    val savingsRate: Double, // % of income retained
    val last3MonthsTrend: List<MonthlyCashflowPoint>,
    val comparisonMessage: String
)

object CashflowHealthCalculator {
    fun calculate(
        filteredTransactions: List<Transaction>,
        allTransactions: List<Transaction>
    ): CashflowHealthSummary {
        val income = filteredTransactions
            .filter { it.amount > 0 && !it.note.contains("transfer", ignoreCase = true) }
            .sumOf { it.amount }
        val expense = filteredTransactions
            .filter { it.amount < 0 && !it.note.contains("transfer", ignoreCase = true) }
            .sumOf { abs(it.amount) }
        val net = income - expense
        val isSurplus = net >= 0L
        val savingsRate = if (income > 0L) ((net.toDouble() / income.toDouble()) * 100.0).coerceIn(-100.0, 100.0) else 0.0

        // Compute 3-Month Trend
        val trendPoints = mutableListOf<MonthlyCashflowPoint>()
        val cal = Calendar.getInstance()
        val monthFmt = SimpleDateFormat("MMM", Locale("id", "ID"))

        for (i in 2 downTo 0) {
            val targetCal = Calendar.getInstance().apply {
                timeInMillis = cal.timeInMillis
                add(Calendar.MONTH, -i)
            }
            val m = targetCal.get(Calendar.MONTH)
            val y = targetCal.get(Calendar.YEAR)
            val label = monthFmt.format(targetCal.time)

            val mTx = allTransactions.filter { tx ->
                val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
                txCal.get(Calendar.MONTH) == m && txCal.get(Calendar.YEAR) == y
            }
            val mInc = mTx.filter { it.amount > 0 && !it.note.contains("transfer", ignoreCase = true) }.sumOf { it.amount }
            val mExp = mTx.filter { it.amount < 0 && !it.note.contains("transfer", ignoreCase = true) }.sumOf { abs(it.amount) }
            val mNet = mInc - mExp
            trendPoints.add(MonthlyCashflowPoint(label, mInc, mExp, mNet))
        }

        val lastMonthNet = if (trendPoints.size >= 2) trendPoints[trendPoints.size - 2].netCashflow else 0L
        val currentMonthNet = trendPoints.lastOrNull()?.netCashflow ?: net
        val diff = currentMonthNet - lastMonthNet
        val comparisonMsg = when {
            diff > 0L -> "Cashflow naik Rp ${String.format(Locale("id", "ID"), "%,d", diff)} dibanding bulan lalu 📈"
            diff < 0L -> "Cashflow turun Rp ${String.format(Locale("id", "ID"), "%,d", abs(diff))} dibanding bulan lalu 📉"
            else -> "Cashflow stabil sama seperti bulan lalu ⚖️"
        }

        return CashflowHealthSummary(
            currentPeriodIncome = income,
            currentPeriodExpense = expense,
            netCashflow = net,
            isSurplus = isSurplus,
            savingsRate = savingsRate,
            last3MonthsTrend = trendPoints,
            comparisonMessage = comparisonMsg
        )
    }
}

