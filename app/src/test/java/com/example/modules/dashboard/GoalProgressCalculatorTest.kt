package com.example.modules.dashboard

import com.example.modules.dashboard.logic.GoalProgressCalculator
import com.example.shared.models.FinancialGoal
import com.example.shared.models.Transaction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GoalProgressCalculatorTest {

    @Test
    fun testGoalProgressCalculationWithTaggedTransactions() {
        val goal = FinancialGoal(
            id = "goal_1",
            title = "Rumah Impian",
            targetAmount = 10000000L,
            currentAmount = 2000000L,
            category = "Rumah",
            iconEmoji = "🏡",
            deadline = "31 Des 2026",
            targetTimestamp = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000) // 90 days from now
        )

        val tx1 = Transaction(
            id = "tx_1",
            walletId = "w1",
            memberId = "m1",
            categoryId = "c1",
            amount = -1500000L,
            note = "Cicilan Rumah",
            goalId = "goal_1"
        )

        val tx2 = Transaction(
            id = "tx_2",
            walletId = "w1",
            memberId = "m1",
            categoryId = "c1",
            amount = -500000L,
            note = "Bonus Tabungan",
            goalId = "goal_1"
        )

        val unlinkedTx = Transaction(
            id = "tx_3",
            walletId = "w1",
            memberId = "m1",
            categoryId = "c1",
            amount = -300000L,
            note = "Beli Makan",
            goalId = null
        )

        val state = GoalProgressCalculator.calculate(goal, listOf(tx1, tx2, unlinkedTx))

        assertEquals(4000000L, state.totalAccumulated) // 2M initial + 1.5M + 0.5M
        assertEquals(40, state.percentage)
        assertEquals(6000000L, state.remainingAmount)
        assertEquals(2, state.taggedTransactions.size)
        assertFalse(state.isCompleted)
    }

    @Test
    fun testOverallGoalsSummary() {
        val g1 = FinancialGoal("g1", "Goal 1", 5000000L, 5000000L, "Tabungan", "🎯")
        val g2 = FinancialGoal("g2", "Goal 2", 10000000L, 2000000L, "Tabungan", "🎯")

        val summary = GoalProgressCalculator.calculateOverall(listOf(g1, g2), emptyList())

        assertEquals(15000000L, summary.totalTarget)
        assertEquals(7000000L, summary.totalAccumulated)
        assertEquals(1, summary.completedGoalsCount)
        assertEquals(1, summary.activeGoalsCount)
    }
}
