package com.example.modules.dashboard.logic

data class CsvRecord(
    val time: String,
    val type: String,
    val amount: Double,
    val category: String,
    val account: String,
    val notes: String
)
