package com.example.modules.dashboard.logic

import com.example.shared.models.DebtRecord
import java.util.Calendar

object LoanProgressCalculator {

    fun calculateSingle(debt: DebtRecord, nowTimestamp: Long = System.currentTimeMillis()): SingleLoanProgress {
        val total = debt.amount.coerceAtLeast(1L)
        val paid = debt.paidAmount.coerceIn(0L, total)
        val remaining = debt.remainingAmount
        val isSettled = debt.isSettled || remaining <= 0L
        val paidRatio = (paid.toFloat() / total.toFloat()).coerceIn(0f, 1f)
        val paidPct = paidRatio * 100f

        val totalTenor = debt.totalTenorMonths.coerceAtLeast(0)
        val remainingTenor = debt.tenorRemainingMonths.coerceIn(0, totalTenor)
        val elapsedTenor = (totalTenor - remainingTenor).coerceAtLeast(0)
        val tenorProgressPct = if (totalTenor > 0) {
            ((elapsedTenor.toFloat() / totalTenor.toFloat()) * 100f).coerceIn(0f, 100f)
        } else paidPct

        val dueDateInfo = calculateDueDateInfo(debt, isSettled, nowTimestamp)

        return SingleLoanProgress(
            debt = debt,
            totalAmount = total,
            paidAmount = paid,
            remainingBalance = remaining,
            paidPercentage = paidPct,
            paidRatio = paidRatio,
            tenorMonthsElapsed = elapsedTenor,
            tenorTotalMonths = totalTenor,
            tenorRemainingMonths = remainingTenor,
            tenorProgressPercentage = tenorProgressPct,
            dueDateInfo = dueDateInfo,
            isSettled = isSettled
        )
    }

    fun calculatePortfolio(debts: List<DebtRecord>, nowTimestamp: Long = System.currentTimeMillis()): PortfolioLoanProgress {
        val bankLoans = debts.filter { it.isBankLoanOrInstallment }
        val progresses = bankLoans.map { calculateSingle(it, nowTimestamp) }
        val activeLoans = progresses.filter { !it.isSettled }

        val totalPrincipal = bankLoans.sumOf { it.amount }
        val totalPaid = bankLoans.sumOf { it.paidAmount }
        val totalRemaining = activeLoans.sumOf { it.remainingBalance }
        val totalMonthly = activeLoans.sumOf { it.debt.monthlyInstallment }

        val overallRatio = if (totalPrincipal > 0L) {
            (totalPaid.toFloat() / totalPrincipal.toFloat()).coerceIn(0f, 1f)
        } else 0f

        val nextUpcoming = activeLoans.minByOrNull { it.dueDateInfo.daysUntilDue }

        return PortfolioLoanProgress(
            activeLoansCount = activeLoans.size,
            totalPrincipal = totalPrincipal,
            totalPaid = totalPaid,
            totalRemaining = totalRemaining,
            overallPaidPercentage = overallRatio * 100f,
            overallPaidRatio = overallRatio,
            totalMonthlyLiability = totalMonthly,
            nextUpcomingLoanProgress = nextUpcoming,
            loans = progresses
        )
    }

    private fun calculateDueDateInfo(debt: DebtRecord, isSettled: Boolean, nowTimestamp: Long): LoanDueDateInfo {
        if (isSettled) {
            return LoanDueDateInfo(0, Int.MAX_VALUE, DueDateUrgency.SETTLED, "LUNAS", "Pinjaman telah lunas")
        }

        val cal = Calendar.getInstance().apply { timeInMillis = nowTimestamp }
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val targetDay = if (debt.dueDayOfMonth in 1..31) {
            debt.dueDayOfMonth.coerceAtMost(maxDays)
        } else {
            val dueCal = Calendar.getInstance().apply { timeInMillis = debt.dueDate }
            dueCal.get(Calendar.DAY_OF_MONTH).coerceIn(1, maxDays)
        }

        val daysUntil = if (targetDay >= currentDay) {
            targetDay - currentDay
        } else {
            (maxDays - currentDay) + targetDay
        }

        val (urgency, badge, detail) = when {
            daysUntil == 0 -> Triple(DueDateUrgency.TODAY, "🚨 HARI INI", "Jatuh tempo cicilan HARI INI (Tgl $targetDay)")
            daysUntil == 1 -> Triple(DueDateUrgency.CRITICAL_UPCOMING, "⚠️ Besok (H-1)", "Jatuh tempo besok (Tgl $targetDay)")
            daysUntil in 2..3 -> Triple(DueDateUrgency.CRITICAL_UPCOMING, "⏳ H-$daysUntil", "Jatuh tempo $daysUntil hari lagi (Tgl $targetDay)")
            daysUntil in 4..7 -> Triple(DueDateUrgency.SOON_UPCOMING, "📅 $daysUntil Hari Lagi", "Jatuh tempo dlm $daysUntil hari (Tgl $targetDay)")
            else -> Triple(DueDateUrgency.NORMAL, "🗓️ Tgl $targetDay", "Jatuh tempo berikutnya Tgl $targetDay")
        }

        return LoanDueDateInfo(targetDay, daysUntil, urgency, badge, detail)
    }
}
