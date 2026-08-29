package com.example.shared.models

data class MonthlyCategoryPoint(
    val monthLabel: String,
    val timestamp: Long,
    val categoryAmounts: Map<String, Long>
)
