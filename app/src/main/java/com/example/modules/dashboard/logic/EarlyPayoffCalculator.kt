package com.example.modules.dashboard.logic

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToLong

object EarlyPayoffCalculator {

    fun calculate(input: EarlyPayoffInput): EarlyPayoffResult {
        val p0 = max(0L, input.principalRemaining - input.lumpSumExtraPayment)
        val n0 = max(1, input.remainingTenorMonths)
        val rate = max(0.0, input.annualInterestRate)
        val monthlyRate = rate / 12.0 / 100.0

        if (input.principalRemaining <= 0L || p0 <= 0L) {
            return zeroResult(input)
        }

        val baseInstallment = if (input.currentMonthlyInstallment > 0L) {
            input.currentMonthlyInstallment
        } else {
            calculateMonthlyInstallment(input.principalRemaining, monthlyRate, n0)
        }

        val origTotalInterest = calculateTotalInterestLinear(input.principalRemaining, monthlyRate, baseInstallment, n0)

        return when (input.strategy) {
            PayoffStrategy.REDUCE_TENOR -> {
                val totalMonthly = baseInstallment + input.extraPaymentPerMonth
                val (newMonths, newInterest) = simulateAmortization(p0, monthlyRate, totalMonthly, n0)
                val monthsSaved = max(0, n0 - newMonths)
                val interestSaved = max(0L, origTotalInterest - newInterest)
                val pct = if (origTotalInterest > 0) (interestSaved.toFloat() / origTotalInterest.toFloat()) else 0f
                EarlyPayoffResult(
                    originalTotalInterest = origTotalInterest,
                    newTotalInterest = newInterest,
                    interestSaved = interestSaved,
                    originalTenorMonths = n0,
                    newTenorMonths = newMonths,
                    monthsSaved = monthsSaved,
                    originalMonthlyPayment = baseInstallment,
                    newMonthlyPayment = totalMonthly,
                    monthlyPaymentSaved = 0L,
                    percentInterestSaved = pct.coerceIn(0f, 1f),
                    estimatedPayoffSummary = "Lunas $monthsSaved bulan lebih cepat, hemat bunga Rp${formatCompact(interestSaved)}"
                )
            }
            PayoffStrategy.REDUCE_INSTALLMENT -> {
                val newInstallment = calculateMonthlyInstallment(p0, monthlyRate, n0)
                val newInterest = calculateTotalInterestLinear(p0, monthlyRate, newInstallment, n0)
                val interestSaved = max(0L, origTotalInterest - newInterest)
                val installmentSaved = max(0L, baseInstallment - newInstallment)
                val pct = if (origTotalInterest > 0) (interestSaved.toFloat() / origTotalInterest.toFloat()) else 0f
                EarlyPayoffResult(
                    originalTotalInterest = origTotalInterest,
                    newTotalInterest = newInterest,
                    interestSaved = interestSaved,
                    originalTenorMonths = n0,
                    newTenorMonths = n0,
                    monthsSaved = 0,
                    originalMonthlyPayment = baseInstallment,
                    newMonthlyPayment = newInstallment,
                    monthlyPaymentSaved = installmentSaved,
                    percentInterestSaved = pct.coerceIn(0f, 1f),
                    estimatedPayoffSummary = "Cicilan turun Rp${formatCompact(installmentSaved)}/bln, hemat total Rp${formatCompact(interestSaved)}"
                )
            }
        }
    }

    private fun calculateMonthlyInstallment(principal: Long, r: Double, months: Int): Long {
        if (r <= 0.0) return principal / max(1, months)
        val factor = (1.0 + r).pow(months.toDouble())
        val pmt = principal * (r * factor) / (factor - 1.0)
        return pmt.roundToLong().coerceAtLeast(1L)
    }

    private fun simulateAmortization(principal: Long, r: Double, monthlyPayment: Long, maxMonths: Int): Pair<Int, Long> {
        var balance = principal.toDouble()
        var totalInterest = 0.0
        var monthCount = 0

        while (balance > 1.0 && monthCount < maxMonths * 2) {
            monthCount++
            val interestMonth = balance * r
            totalInterest += interestMonth
            val principalPaid = monthlyPayment - interestMonth
            if (principalPaid <= 0.0) {
                // Payment does not cover interest; fallback
                return Pair(maxMonths, totalInterest.roundToLong())
            }
            balance -= principalPaid
            if (balance <= 0.0) break
        }
        return Pair(monthCount, totalInterest.roundToLong())
    }

    private fun calculateTotalInterestLinear(principal: Long, r: Double, payment: Long, months: Int): Long {
        if (r <= 0.0) return 0L
        val totalPaid = payment * months
        return max(0L, totalPaid - principal)
    }

    private fun zeroResult(input: EarlyPayoffInput) = EarlyPayoffResult(
        0L, 0L, 0L, input.remainingTenorMonths, 0, input.remainingTenorMonths,
        input.currentMonthlyInstallment, 0L, input.currentMonthlyInstallment, 1.0f, "Sudah Lunas"
    )

    private fun formatCompact(amount: Long): String {
        return when {
            amount >= 1_000_000_000L -> "%.1fM".format(amount / 1_000_000_000.0)
            amount >= 1_000_000L -> "%.1f Jt".format(amount / 1_000_000.0)
            amount >= 1_000L -> "${amount / 1_000} Rb"
            else -> amount.toString()
        }
    }
}
