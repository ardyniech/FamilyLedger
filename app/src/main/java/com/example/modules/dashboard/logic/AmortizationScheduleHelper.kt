package com.example.modules.dashboard.logic

import kotlin.math.max
import kotlin.math.roundToLong

object AmortizationScheduleHelper {

    fun generateSchedule(
        principal: Long,
        annualRate: Double,
        monthlyPayment: Long,
        maxMonths: Int = 240
    ): List<AmortizationScheduleRow> {
        val rows = mutableListOf<AmortizationScheduleRow>()
        if (principal <= 0L || monthlyPayment <= 0L) return rows

        val monthlyRate = max(0.0, annualRate) / 12.0 / 100.0
        var balance = principal.toDouble()
        var month = 1

        while (balance > 1.0 && month <= maxMonths) {
            val startBal = balance.roundToLong()
            val interestMonth = balance * monthlyRate
            val principalMonth = monthlyPayment - interestMonth

            if (principalMonth <= 0.0) {
                // Payment doesn't cover interest
                rows.add(
                    AmortizationScheduleRow(
                        monthIndex = month,
                        startingBalance = startBal,
                        paymentAmount = monthlyPayment,
                        principalPaid = 0L,
                        interestPaid = monthlyPayment,
                        endingBalance = startBal
                    )
                )
                break
            }

            val actualPrincipal = if (principalMonth > balance) balance else principalMonth
            val actualPayment = (actualPrincipal + interestMonth).roundToLong()
            balance -= actualPrincipal
            val endBal = max(0.0, balance).roundToLong()

            rows.add(
                AmortizationScheduleRow(
                    monthIndex = month,
                    startingBalance = startBal,
                    paymentAmount = actualPayment,
                    principalPaid = actualPrincipal.roundToLong(),
                    interestPaid = interestMonth.roundToLong(),
                    endingBalance = endBal
                )
            )

            if (balance <= 1.0) break
            month++
        }

        return rows
    }
}
