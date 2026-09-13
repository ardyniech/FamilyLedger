package com.example.modules.dashboard.logic

import com.example.shared.models.DebtRecord

enum class DueDateUrgency(val priority: Int) {
    TODAY(0),
    CRITICAL_UPCOMING(1),
    SOON_UPCOMING(2),
    NORMAL(3),
    SETTLED(4)
}

data class LoanDueDateInfo(
    val dayOfMonth: Int,
    val daysUntilDue: Int,
    val urgency: DueDateUrgency,
    val badgeLabel: String,
    val detailedText: String
)

data class SingleLoanProgress(
    val debt: DebtRecord,
    val totalAmount: Long,
    val paidAmount: Long,
    val remainingBalance: Long,
    val paidPercentage: Float,
    val paidRatio: Float,
    val tenorMonthsElapsed: Int,
    val tenorTotalMonths: Int,
    val tenorRemainingMonths: Int,
    val tenorProgressPercentage: Float,
    val dueDateInfo: LoanDueDateInfo,
    val isSettled: Boolean
)

data class PortfolioLoanProgress(
    val activeLoansCount: Int,
    val totalPrincipal: Long,
    val totalPaid: Long,
    val totalRemaining: Long,
    val overallPaidPercentage: Float,
    val overallPaidRatio: Float,
    val totalMonthlyLiability: Long,
    val nextUpcomingLoanProgress: SingleLoanProgress?,
    val loans: List<SingleLoanProgress>
)
