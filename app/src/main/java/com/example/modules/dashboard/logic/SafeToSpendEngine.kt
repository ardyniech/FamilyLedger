package com.example.modules.dashboard.logic

import com.example.shared.models.*
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.max

object SafeToSpendEngine {

    fun calculate(
        totalBalance: Long,
        monthlyBudget: Long,
        transactions: List<Transaction>,
        recurringBills: List<RecurringBill>,
        debts: List<DebtRecord>,
        goals: List<FinancialGoal>
    ): SafeToSpendReport {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val daysRemaining = max(1, maxDaysInMonth - currentDay + 1)

        val startOfDayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val startOfDayMillis = startOfDayCal.timeInMillis

        val spentToday = transactions
            .filter { it.amount < 0 && it.timestamp >= startOfDayMillis }
            .sumOf { abs(it.amount) }

        val committedBills = recurringBills
            .filter { !it.isPaid && (it.dueDayOfMonth >= currentDay || it.dueDayOfMonth == 0) }
            .sumOf { it.amount }

        val committedLoans = debts
            .filter { !it.isSettled && it.monthlyInstallment > 0L }
            .sumOf { it.monthlyInstallment }

        val goalsReservation = goals
            .filter { it.currentAmount < it.targetAmount }
            .sumOf { max(0L, (it.targetAmount - it.currentAmount) / 12) }

        val totalCommitted = committedBills + committedLoans + goalsReservation
        val availableDiscretionary = max(0L, totalBalance - totalCommitted)
        val dailySafe = if (daysRemaining > 0) availableDiscretionary / daysRemaining else 0L

        val remainingToday = max(0L, dailySafe - spentToday)
        val burnRatio = if (dailySafe > 0L) (spentToday.toFloat() / dailySafe.toFloat()).coerceAtLeast(0f) else 1.0f

        val status = when {
            spentToday <= dailySafe * 0.75f -> BurnStatus.SAFE
            spentToday <= dailySafe -> BurnStatus.MODERATE
            else -> BurnStatus.CRITICAL
        }

        val advice = when (status) {
            BurnStatus.SAFE -> "Bagus! Kamu masih memiliki cadangan harian yang sangat aman."
            BurnStatus.MODERATE -> "Waspada, pengeluaranmu hampir menyentuh batas harian."
            BurnStatus.CRITICAL -> "Batas harian terlampaui. Tunda pos belanja non-esensial hingga esok."
        }

        return SafeToSpendReport(
            dailySafeToSpend = dailySafe,
            spentToday = spentToday,
            remainingSafeToday = remainingToday,
            monthlySafeToSpendRemaining = availableDiscretionary,
            daysRemainingInMonth = daysRemaining,
            committedBillsSum = committedBills,
            committedLoansSum = committedLoans,
            goalsReservationSum = goalsReservation,
            burnStatus = status,
            dailyBurnRatio = burnRatio,
            actionAdvice = advice
        )
    }
}
