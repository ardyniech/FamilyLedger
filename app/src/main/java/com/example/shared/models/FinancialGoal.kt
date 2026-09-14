package com.example.shared.models

data class FinancialGoal(
    val id: String,
    val title: String,
    val targetAmount: Long,
    val currentAmount: Long = 0L,
    val category: String = "Tabungan", // "Rumah", "Pendidikan", "Darurat", "Liburan", "Investasi"
    val iconEmoji: String = "🎯",
    val deadline: String = "", // e.g. "31 Des 2026"
    val targetTimestamp: Long = 0L,
    val colorHex: String = "#3B82F6"
) {
    val isCompleted: Boolean
        get() = currentAmount >= targetAmount
}
