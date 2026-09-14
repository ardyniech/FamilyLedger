package com.example.shared.models

data class RecurringBill(
    val id: String,
    val name: String,
    val amount: Long,
    val dueDate: String, // e.g. "Aug 28, 2026"
    val categoryId: String,
    val isPaid: Boolean = false,
    val autoPay: Boolean = false,
    val targetWalletId: String? = null,
    val frequency: String = "Monthly", // "One-Time", "Daily", "Weekly", "Monthly", "Yearly"
    val lastProcessedTime: Long = 0L,
    val dueDayOfMonth: Int = 0,
    val autoPopulateInLedger: Boolean = true
)
