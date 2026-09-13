package com.example.modules.dashboard.logic

data class NafkahAllocationReport(
    val isWifeRole: Boolean,
    val husbandName: String,
    val wifeName: String,
    val totalReceivedFromHusband: Long,
    val totalHouseholdExpensesSpent: Long,
    val remainingBudget: Long,
    val spentPercentage: Float,
    val statusHeadline: String,
    val feedbackNote: String,
    val topHouseholdExpenses: List<HouseholdExpenseBreakdown>
)

data class HouseholdExpenseBreakdown(
    val categoryName: String,
    val amount: Long,
    val percentageOfBudget: Float
)
