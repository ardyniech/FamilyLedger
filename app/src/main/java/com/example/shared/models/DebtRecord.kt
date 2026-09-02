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
    val isSettled: Boolean = false
) {
    val remainingAmount: Long get() = (amount - paidAmount).coerceAtLeast(0L)
}
