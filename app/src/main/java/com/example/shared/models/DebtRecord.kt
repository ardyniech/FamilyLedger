package com.example.shared.models

import java.util.UUID

data class DebtRecord(
    val id: String = UUID.randomUUID().toString(),
    val personName: String,
    val isHutang: Boolean, // true = Hutang (kita ngutang), false = Piutang (orang ngutang ke kita)
    val amount: Long,
    val paidAmount: Long = 0L,
    val dueDate: Long,
    val note: String = "",
    val isSettled: Boolean = false,
    val loanType: LoanType = LoanType.PERSONAL_DEBT,
    val monthlyInstallment: Long = 0L,
    val dueDayOfMonth: Int = 0,
    val tenorRemainingMonths: Int = 0,
    val totalTenorMonths: Int = 0,
    val institutionName: String = "",
    val autoDebitWalletId: String? = null,
    val annualInterestRate: Double = 0.0
) {
    val remainingAmount: Long get() = (amount - paidAmount).coerceAtLeast(0L)
    val isBankLoanOrInstallment: Boolean get() = loanType != LoanType.PERSONAL_DEBT
}

