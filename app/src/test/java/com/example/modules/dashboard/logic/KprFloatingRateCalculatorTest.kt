package com.example.modules.dashboard.logic

import org.junit.Assert.*
import org.junit.Test

class KprFloatingRateCalculatorTest {

    @Test
    fun testAnnuityCalculation_returnsPositiveMonthlyInstallment() {
        val installment = KprFloatingRateCalculator.calculateAnnuity(
            principal = 500_000_000L,
            annualRatePct = 5.5f,
            tenorMonths = 180
        )
        assertTrue("Monthly installment should be positive", installment > 0L)
        assertTrue("Installment around 4 million", installment in 3_900_000L..4_500_000L)
    }

    @Test
    fun testFloatingAnalysis_detectsSpikeAndSeverity() {
        val analysis = KprFloatingRateCalculator.analyze(
            loanName = "KPR Primary",
            remainingPrincipal = 500_000_000L,
            fixedRatePct = 5.5f,
            floatingRatePct = 11.5f,
            remainingTenorMonths = 180,
            monthsUntilFloating = 2
        )

        assertEquals(KprFloatingSeverity.SHOCK_IMMINENT, analysis.severityLevel)
        assertTrue(analysis.monthlyDifference > 0)
        assertTrue(analysis.percentageSpike > 20f)
        assertTrue(analysis.recommendationText.contains("Cicilan Anda diproyeksikan melonjak"))
    }

    @Test
    fun testFloatingAnalysis_upcomingStatusWhen8Months() {
        val analysis = KprFloatingRateCalculator.analyze(
            loanName = "KPR Primary",
            remainingPrincipal = 500_000_000L,
            fixedRatePct = 5.5f,
            floatingRatePct = 11.5f,
            remainingTenorMonths = 180,
            monthsUntilFloating = 8
        )

        assertEquals(KprFloatingSeverity.UPCOMING, analysis.severityLevel)
        assertTrue(analysis.recommendationText.contains("Sisa fixed tersisa 8 bulan"))
    }
}
