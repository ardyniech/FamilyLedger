package com.example.modules.dashboard.logic

import com.example.shared.models.FinancialGoal
import com.example.shared.models.Transaction
import java.util.Calendar

data class GoalProgressState(
    val goal: FinancialGoal,
    val totalAccumulated: Long,
    val progressFraction: Float,
    val percentage: Int,
    val remainingAmount: Long,
    val isCompleted: Boolean,
    val daysLeft: Long?,
    val deadlineStatusText: String,
    val monthlyRecommendation: Long?,
    val taggedTransactions: List<Transaction>
)

data class OverallGoalsSummary(
    val totalTarget: Long,
    val totalAccumulated: Long,
    val progressFraction: Float,
    val completedGoalsCount: Int,
    val activeGoalsCount: Int
)

object GoalProgressCalculator {
    fun calculate(goal: FinancialGoal, transactions: List<Transaction>): GoalProgressState {
        val tagged = transactions.filter { it.goalId == goal.id && !it.isDeleted }
        val taggedSum = tagged.sumOf { if (it.amount > 0) it.amount else -it.amount }
        val total = goal.currentAmount + taggedSum
        val fraction = if (goal.targetAmount > 0) (total.toFloat() / goal.targetAmount.toFloat()).coerceIn(0f, 1f) else 0f
        val percentage = (fraction * 100).toInt()
        val remaining = (goal.targetAmount - total).coerceAtLeast(0L)
        val isCompleted = total >= goal.targetAmount

        val now = System.currentTimeMillis()
        val (daysLeft, deadlineText, monthlyRec) = if (goal.targetTimestamp > 0L) {
            val diffMs = goal.targetTimestamp - now
            val days = (diffMs / (1000 * 60 * 60 * 24)).coerceAtLeast(0L)
            val text = when {
                isCompleted -> "Target Tercapai! 🎉"
                diffMs <= 0 -> "Melewati Deadline ⚠️"
                days == 0L -> "Jatuh tempo hari ini!"
                days < 30L -> "Sisa $days hari"
                else -> {
                    val months = (days / 30).coerceAtLeast(1L)
                    "Sisa ~$months bulan ($days hari)"
                }
            }
            val rec = if (!isCompleted && days > 0) {
                val months = (days / 30.0).coerceAtLeast(1.0)
                (remaining / months).toLong()
            } else null
            Triple(days, text, rec)
        } else if (goal.deadline.isNotBlank()) {
            Triple(null, "Target: ${goal.deadline}", null)
        } else {
            Triple(null, if (isCompleted) "Target Tercapai!" else "Fleksibel", null)
        }

        return GoalProgressState(
            goal = goal,
            totalAccumulated = total,
            progressFraction = fraction,
            percentage = percentage,
            remainingAmount = remaining,
            isCompleted = isCompleted,
            daysLeft = daysLeft,
            deadlineStatusText = deadlineText,
            monthlyRecommendation = monthlyRec,
            taggedTransactions = tagged
        )
    }

    fun calculateOverall(goals: List<FinancialGoal>, transactions: List<Transaction>): OverallGoalsSummary {
        val totalTarget = goals.sumOf { it.targetAmount }
        var totalAccumulated = 0L
        var completed = 0

        goals.forEach { goal ->
            val state = calculate(goal, transactions)
            totalAccumulated += state.totalAccumulated
            if (state.isCompleted) completed++
        }

        val fraction = if (totalTarget > 0) (totalAccumulated.toFloat() / totalTarget.toFloat()).coerceIn(0f, 1f) else 0f
        return OverallGoalsSummary(
            totalTarget = totalTarget,
            totalAccumulated = totalAccumulated,
            progressFraction = fraction,
            completedGoalsCount = completed,
            activeGoalsCount = goals.size - completed
        )
    }
}
