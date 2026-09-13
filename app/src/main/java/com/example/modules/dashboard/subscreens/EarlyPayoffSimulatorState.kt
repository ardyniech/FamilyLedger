package com.example.modules.dashboard.subscreens

import com.example.modules.dashboard.logic.EarlyPayoffInput
import com.example.modules.dashboard.logic.EarlyPayoffResult
import com.example.modules.dashboard.logic.PayoffStrategy
import com.example.shared.models.DebtRecord

data class EarlyPayoffSimulatorState(
    val selectedDebt: DebtRecord? = null,
    val principalInput: String = "350000000",
    val annualRateInput: String = "8.5",
    val remainingTenorInput: String = "120",
    val monthlyInstallmentInput: String = "3200000",
    val extraMonthlyInput: String = "1000000",
    val lumpSumInput: String = "0",
    val strategy: PayoffStrategy = PayoffStrategy.REDUCE_TENOR,
    val calculationResult: EarlyPayoffResult? = null
) {
    fun toInput(): EarlyPayoffInput {
        val principal = principalInput.toLongOrNull() ?: 0L
        val rate = annualRateInput.toDoubleOrNull() ?: 0.0
        val tenor = remainingTenorInput.toIntOrNull() ?: 1
        val monthly = monthlyInstallmentInput.toLongOrNull() ?: 0L
        val extraMonthly = extraMonthlyInput.toLongOrNull() ?: 0L
        val lumpSum = lumpSumInput.toLongOrNull() ?: 0L
        return EarlyPayoffInput(
            principalRemaining = principal,
            annualInterestRate = rate,
            remainingTenorMonths = tenor,
            currentMonthlyInstallment = monthly,
            extraPaymentPerMonth = extraMonthly,
            lumpSumExtraPayment = lumpSum,
            strategy = strategy
        )
    }
}
