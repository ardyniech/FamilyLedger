package com.example.modules.dashboard.logic

enum class PayoffStrategy(val label: String, val description: String) {
    REDUCE_TENOR("Pangkas Tenor", "Cicilan tetap sama, waktu lunas jauh lebih cepat"),
    REDUCE_INSTALLMENT("Pangkas Angsuran", "Tenor tetap, cicilan bulanan menjadi lebih ringan")
}

data class EarlyPayoffInput(
    val principalRemaining: Long,
    val annualInterestRate: Double,
    val remainingTenorMonths: Int,
    val currentMonthlyInstallment: Long,
    val extraPaymentPerMonth: Long = 0L,
    val lumpSumExtraPayment: Long = 0L,
    val strategy: PayoffStrategy = PayoffStrategy.REDUCE_TENOR
)

data class EarlyPayoffResult(
    val originalTotalInterest: Long,
    val newTotalInterest: Long,
    val interestSaved: Long,
    val originalTenorMonths: Int,
    val newTenorMonths: Int,
    val monthsSaved: Int,
    val originalMonthlyPayment: Long,
    val newMonthlyPayment: Long,
    val monthlyPaymentSaved: Long,
    val percentInterestSaved: Float,
    val estimatedPayoffSummary: String
)
