package com.example.modules.dashboard

import com.example.modules.dashboard.logic.DueDateUrgency
import com.example.modules.dashboard.logic.LoanProgressCalculator
import com.example.shared.models.DebtRecord
import com.example.shared.models.LoanType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class LoanProgressCalculatorTest {

    @Test
    fun testSingleKprProgressCalculation() {
        val kpr = DebtRecord(
            personName = "KPR Rumah Utama",
            isHutang = true,
            amount = 500_000_000L,
            paidAmount = 150_000_000L,
            dueDate = 0L,
            loanType = LoanType.KPR_MORTGAGE,
            monthlyInstallment = 4_500_000L,
            dueDayOfMonth = 10,
            tenorRemainingMonths = 120,
            totalTenorMonths = 180
        )

        val progress = LoanProgressCalculator.calculateSingle(kpr)

        assertEquals(500_000_000L, progress.totalAmount)
        assertEquals(150_000_000L, progress.paidAmount)
        assertEquals(350_000_000L, progress.remainingBalance)
        assertEquals(30.0f, progress.paidPercentage, 0.01f)
        assertEquals(0.30f, progress.paidRatio, 0.01f)
        assertEquals(60, progress.tenorMonthsElapsed)
        assertEquals(33.33f, progress.tenorProgressPercentage, 0.05f)
        assertEquals(false, progress.isSettled)
    }

    @Test
    fun testDueDateUrgencyLevels() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 5) // Today is 5th
        }
        val nowTime = cal.timeInMillis

        // Case 1: Today (5th)
        val debtToday = DebtRecord(
            personName = "KPR BTN",
            isHutang = true,
            amount = 100_000_000L,
            dueDate = 0L,
            loanType = LoanType.KPR_MORTGAGE,
            dueDayOfMonth = 5
        )
        val resToday = LoanProgressCalculator.calculateSingle(debtToday, nowTime)
        assertEquals(0, resToday.dueDateInfo.daysUntilDue)
        assertEquals(DueDateUrgency.TODAY, resToday.dueDateInfo.urgency)

        // Case 2: Tomorrow H-1 (6th)
        val debtTomorrow = debtToday.copy(dueDayOfMonth = 6)
        val resTomorrow = LoanProgressCalculator.calculateSingle(debtTomorrow, nowTime)
        assertEquals(1, resTomorrow.dueDateInfo.daysUntilDue)
        assertEquals(DueDateUrgency.CRITICAL_UPCOMING, resTomorrow.dueDateInfo.urgency)

        // Case 3: H-3 (8th)
        val debtH3 = debtToday.copy(dueDayOfMonth = 8)
        val resH3 = LoanProgressCalculator.calculateSingle(debtH3, nowTime)
        assertEquals(3, resH3.dueDateInfo.daysUntilDue)
        assertEquals(DueDateUrgency.CRITICAL_UPCOMING, resH3.dueDateInfo.urgency)

        // Case 4: 5 days away (10th)
        val debtSoon = debtToday.copy(dueDayOfMonth = 10)
        val resSoon = LoanProgressCalculator.calculateSingle(debtSoon, nowTime)
        assertEquals(5, resSoon.dueDateInfo.daysUntilDue)
        assertEquals(DueDateUrgency.SOON_UPCOMING, resSoon.dueDateInfo.urgency)
    }

    @Test
    fun testPortfolioCalculation() {
        val debts = listOf(
            DebtRecord(
                personName = "KPR Mandiri",
                isHutang = true,
                amount = 600_000_000L,
                paidAmount = 200_000_000L,
                dueDate = 0L,
                loanType = LoanType.KPR_MORTGAGE,
                monthlyInstallment = 5_000_000L,
                dueDayOfMonth = 15
            ),
            DebtRecord(
                personName = "Kredit Mobil BCA",
                isHutang = true,
                amount = 200_000_000L,
                paidAmount = 100_000_000L,
                dueDate = 0L,
                loanType = LoanType.VEHICLE_LOAN,
                monthlyInstallment = 3_000_000L,
                dueDayOfMonth = 10
            )
        )

        val portfolio = LoanProgressCalculator.calculatePortfolio(debts)
        assertEquals(2, portfolio.activeLoansCount)
        assertEquals(800_000_000L, portfolio.totalPrincipal)
        assertEquals(300_000_000L, portfolio.totalPaid)
        assertEquals(500_000_000L, portfolio.totalRemaining)
        assertEquals(37.5f, portfolio.overallPaidPercentage, 0.01f)
        assertEquals(8_000_000L, portfolio.totalMonthlyLiability)
    }
}
