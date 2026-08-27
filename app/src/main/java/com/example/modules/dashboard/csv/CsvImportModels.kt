package com.example.modules.dashboard.csv

data class ParsedTransaction(
    val id: String,
    val rawDate: String,
    val timestamp: Long,
    val rawType: String,
    val amount: Double,
    val rawCategory: String,
    val categoryId: String,
    val rawAccount: String,
    val walletId: String,
    val memberId: String,
    val note: String,
    val isTransfer: Boolean = false,
    val targetWalletId: String? = null,
    val targetMemberId: String? = null,
    val isDuplicate: Boolean = false
)

data class CsvParseResult(
    val delimiter: Char,
    val hasHeader: Boolean,
    val records: List<ParsedTransaction>,
    val totalIncome: Double,
    val totalExpense: Double,
    val newCount: Int,
    val duplicateCount: Int,
    val detectedFormatName: String,
    val errors: List<String> = emptyList()
)

data class ImportExecutionResult(
    val insertedCount: Int,
    val skippedDuplicates: Int,
    val totalInflow: Double,
    val totalOutflow: Double,
    val isSuccess: Boolean,
    val message: String
)
