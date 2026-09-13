package com.example.modules.dashboard

import com.example.modules.dashboard.logic.AmortizationScheduleHelper
import org.junit.Assert.*
import org.junit.Test

class AmortizationScheduleTest {

    @Test
    fun testGenerateSchedule_ReducesPrincipalToZero() {
        val principal = 10_000_000L
        val annualRate = 12.0
        val monthlyPayment = 1_000_000L

        val schedule = AmortizationScheduleHelper.generateSchedule(
            principal = principal,
            annualRate = annualRate,
            monthlyPayment = monthlyPayment,
            maxMonths = 36
        )

        assertTrue(schedule.isNotEmpty())
        assertEquals(1, schedule.first().monthIndex)
        assertEquals(principal, schedule.first().startingBalance)
        assertTrue(schedule.last().endingBalance <= 1L)
        assertTrue(schedule.all { it.principalPaid >= 0L && it.interestPaid >= 0L })
    }

    @Test
    fun testGenerateSchedule_ZeroInputs() {
        val schedule = AmortizationScheduleHelper.generateSchedule(
            principal = 0L,
            annualRate = 10.0,
            monthlyPayment = 500_000L
        )

        assertTrue(schedule.isEmpty())
    }
}
