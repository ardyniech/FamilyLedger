package com.example.modules.dashboard.logic

data class CsvRecord(
    val time: String,
    val type: String,
    val amount: Long,
    val category: String,
    val account: String,
    val notes: String
) {
    constructor(time: String, type: String, amount: Double, category: String, account: String, notes: String) :
        this(time, type, amount.toLong(), category, account, notes)
}
