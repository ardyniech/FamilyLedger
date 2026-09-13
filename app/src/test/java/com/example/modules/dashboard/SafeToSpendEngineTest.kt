package com.example.modules.dashboard

import com.example.modules.dashboard.logic.SafeToSpendEngine
import com.example.shared.models.*
import org.junit.Assert.*
import org.junit.Test

class SafeToSpendEngineTest {

    @Test
    fun testSafeToSpend_CalculatesProperlyWithCommittedBuffers() {
        val now = System.currentTimeMillis()
        val transactions = listOf(
            Transaction("t1", "w1", "m1", "c1", -50000L, "Makan siang", now)
        )
        val bills = listOf(
            RecurringBill("b1", "Listrik", 500000L, "2026-08-28", "c1", false, false, "w1", "Monthly", 0L, 28)
        )
        val debts = listOf(
            DebtRecord(
                id = "d1",
                personName = "Bank Mandiri KPR",
                isHutang = true,
                amount = 300000000L,
                paidAmount = 50000000L,
                dueDate = now + 86400000L * 30,
                monthlyInstallment = 3000000L,
                loanType = LoanType.KPR_MORTGAGE,
                annualInterestRate = 8.5,
                tenorRemainingMonths = 120
            )
        )
        val goals = listOf(
            FinancialGoal(
                id = "g1",
                title = "Dana Darurat",
                targetAmount = 12000000L,
                currentAmount = 0L,
                category = "Darurat",
                iconEmoji = "🎯",
                deadline = "31 Des 2026",
                targetTimestamp = now + 86400000L * 365
            )
        )

        val report = SafeToSpendEngine.calculate(
            totalBalance = 15000000L,
            monthlyBudget = 8000000L,
            transactions = transactions,
            recurringBills = bills,
            debts = debts,
            goals = goals
        )

        assertTrue(report.committedBillsSum >= 0L)
        assertEquals(3000000L, report.committedLoansSum)
        assertEquals(1000000L, report.goalsReservationSum)
        assertTrue(report.dailySafeToSpend > 0L)
        assertEquals(50000L, report.spentToday)
        assertTrue(report.remainingSafeToday >= 0L)
    }

    @Test
    fun testSafeToSpend_ZeroBalanceHandlesGracefully() {
        val report = SafeToSpendEngine.calculate(
            totalBalance = 0L,
            monthlyBudget = 0L,
            transactions = emptyList(),
            recurringBills = emptyList(),
            debts = emptyList(),
            goals = emptyList()
        )

        assertEquals(0L, report.dailySafeToSpend)
        assertEquals(0L, report.spentToday)
        assertEquals(0L, report.remainingSafeToday)
        assertNotNull(report.actionAdvice)
    }

    @Test
    fun testSafeToSpend_CriticalStatusWhenOverspent() {
        val now = System.currentTimeMillis()
        val transactions = listOf(
            Transaction("t1", "w1", "m1", "c1", -5000000L, "Belanja Besar", now)
        )

        val report = SafeToSpendEngine.calculate(
            totalBalance = 3000000L,
            monthlyBudget = 3000000L,
            transactions = transactions,
            recurringBills = emptyList(),
            debts = emptyList(),
            goals = emptyList()
        )

        assertEquals(BurnStatus.CRITICAL, report.burnStatus)
        assertEquals(0L, report.remainingSafeToday)
    }
}
