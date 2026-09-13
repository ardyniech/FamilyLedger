package com.example.modules.dashboard

import com.example.modules.dashboard.logic.EarlyPayoffCalculator
import com.example.modules.dashboard.logic.EarlyPayoffInput
import com.example.modules.dashboard.logic.PayoffStrategy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EarlyPayoffCalculatorTest {

    @Test
    fun testReduceTenorCalculation_savesTimeAndInterest() {
        val input = EarlyPayoffInput(
            principalRemaining = 240_000_000L,
            annualInterestRate = 8.0,
            remainingTenorMonths = 120,
            currentMonthlyInstallment = 2_911_860L,
            extraPaymentPerMonth = 1_000_000L,
            lumpSumExtraPayment = 0L,
            strategy = PayoffStrategy.REDUCE_TENOR
        )

        val result = EarlyPayoffCalculator.calculate(input)

        assertTrue("Months saved should be greater than 0", result.monthsSaved > 0)
        assertTrue("New tenor should be less than original", result.newTenorMonths < result.originalTenorMonths)
        assertTrue("Interest saved should be positive", result.interestSaved > 0)
        assertTrue("Percent interest saved should be between 0 and 1", result.percentInterestSaved in 0.01f..1.0f)
    }

    @Test
    fun testReduceInstallmentCalculation_reducesMonthlyPayment() {
        val input = EarlyPayoffInput(
            principalRemaining = 200_000_000L,
            annualInterestRate = 9.0,
            remainingTenorMonths = 60,
            currentMonthlyInstallment = 4_151_670L,
            extraPaymentPerMonth = 0L,
            lumpSumExtraPayment = 50_000_000L,
            strategy = PayoffStrategy.REDUCE_INSTALLMENT
        )

        val result = EarlyPayoffCalculator.calculate(input)

        assertEquals("Tenor remains unchanged", 60, result.newTenorMonths)
        assertTrue("Monthly installment saved should be > 0", result.monthlyPaymentSaved > 0)
        assertTrue("New monthly payment should be lower than original", result.newMonthlyPayment < result.originalMonthlyPayment)
        assertTrue("Interest saved should be > 0", result.interestSaved > 0)
    }

    @Test
    fun testBadInput_zeroPrincipal_handlesGracefully() {
        val input = EarlyPayoffInput(
            principalRemaining = 0L,
            annualInterestRate = 10.0,
            remainingTenorMonths = 12,
            currentMonthlyInstallment = 0L
        )

        val result = EarlyPayoffCalculator.calculate(input)

        assertEquals(0L, result.interestSaved)
        assertEquals(0L, result.newTotalInterest)
        assertEquals("Sudah Lunas", result.estimatedPayoffSummary)
    }

    @Test
    fun testBadInput_zeroRate_handlesGracefully() {
        val input = EarlyPayoffInput(
            principalRemaining = 12_000_000L,
            annualInterestRate = 0.0,
            remainingTenorMonths = 12,
            currentMonthlyInstallment = 1_000_000L,
            extraPaymentPerMonth = 1_000_000L,
            strategy = PayoffStrategy.REDUCE_TENOR
        )

        val result = EarlyPayoffCalculator.calculate(input)

        assertEquals(0L, result.originalTotalInterest)
        assertTrue("Should calculate fewer months with extra pay", result.newTenorMonths in 1..6)
    }
}
