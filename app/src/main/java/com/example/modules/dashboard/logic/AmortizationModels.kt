package com.example.modules.dashboard.logic

data class AmortizationScheduleRow(
    val monthIndex: Int,
    val startingBalance: Long,
    val paymentAmount: Long,
    val principalPaid: Long,
    val interestPaid: Long,
    val endingBalance: Long
)
