package com.example.modules.dashboard.csv

import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount

object SmartCsvParser {
    fun parse(
        csvContent: String,
        existingWallets: List<WalletAccount>,
        existingCategories: List<Category>,
        existingTransactions: List<Transaction>
    ): CsvParseResult {
        if (csvContent.isBlank()) return CsvParseResult(',', false, emptyList(), 0L, 0L, 0, 0, "Data Kosong")

        val lines = csvContent.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) return CsvParseResult(',', false, emptyList(), 0L, 0L, 0, 0, "Data Kosong")

        val delimiter = CsvLineTokenizer.detectDelimiter(lines.first())
        val rawRows = lines.map { CsvLineTokenizer.tokenize(it, delimiter) }
        val hasHeader = CsvColumnDetector.isHeaderRow(rawRows.first())
        val dataRows = if (hasHeader) rawRows.drop(1) else rawRows

        val colMap = if (hasHeader) CsvColumnDetector.detectFromHeader(rawRows.first()) else CsvColumnDetector.detectHeuristically(dataRows)
        val walletMap = existingWallets.associate { it.id to it.name }
        val catMap = existingCategories.associate { it.id to it.name }
        val existingSignatures = existingTransactions.map { generateSignature(it.timestamp, it.amount, it.walletId, it.note) }.toSet()

        val parsedList = mutableListOf<ParsedTransaction>()
        var incomeSum = 0L
        var expenseSum = 0L
        var duplicateCount = 0

        for ((index, row) in dataRows.withIndex()) {
            val dateStr = row.getOrNull(colMap.dateCol)?.trim() ?: ""
            val typeStr = row.getOrNull(colMap.typeCol)?.trim() ?: ""
            val amountStr = row.getOrNull(colMap.amountCol)?.trim() ?: "0"
            val catStr = row.getOrNull(colMap.categoryCol)?.trim() ?: ""
            val accStr = row.getOrNull(colMap.accountCol)?.trim() ?: "Cash"
            val noteStr = row.getOrNull(colMap.noteCol)?.trim() ?: ""

            val parsedAmount = kotlin.math.abs(CsvPatternMatcher.parseAmount(amountStr))
            val detectedType = CsvPatternMatcher.detectType(typeStr, parsedAmount, "$catStr $noteStr")
            val timestamp = CsvDateParser.parseTimestamp(dateStr)
            val isTransfer = detectedType == "Transfer" || accStr.contains("->")

            val targetWallet = if (isTransfer && accStr.contains("->")) {
                val parts = accStr.split("->")
                if (parts.size >= 2) CsvPatternMatcher.matchWallet(parts[1], walletMap) else null
            } else null

            val sourceAcc = if (accStr.contains("->")) accStr.split("->")[0] else accStr
            val walletId = CsvPatternMatcher.matchWallet(sourceAcc, walletMap)
            val categoryId = CsvPatternMatcher.matchCategory(catStr, catMap)
            val memberId = if (walletId == "w_deina") "m2" else "m1"

            val netAmount = if (detectedType == "Income") parsedAmount else -parsedAmount
            if (detectedType == "Income") incomeSum += parsedAmount else expenseSum += parsedAmount

            val sig = generateSignature(timestamp, netAmount, walletId, noteStr)
            val isDuplicate = existingSignatures.contains(sig)
            if (isDuplicate) duplicateCount++

            parsedList.add(
                ParsedTransaction(
                    id = "csv_${System.currentTimeMillis()}_$index",
                    rawDate = dateStr,
                    timestamp = timestamp,
                    rawType = detectedType,
                    amount = netAmount,
                    rawCategory = catStr,
                    categoryId = categoryId,
                    rawAccount = accStr,
                    walletId = walletId,
                    memberId = memberId,
                    note = noteStr,
                    isTransfer = isTransfer,
                    targetWalletId = targetWallet,
                    targetMemberId = if (targetWallet == "w_deina") "m2" else "m1",
                    isDuplicate = isDuplicate
                )
            )
        }

        return CsvParseResult(
            delimiter = delimiter,
            hasHeader = hasHeader,
            records = parsedList,
            totalIncome = incomeSum,
            totalExpense = expenseSum,
            newCount = parsedList.size - duplicateCount,
            duplicateCount = duplicateCount,
            detectedFormatName = "Format ${if (delimiter == ',') "Comma (,)" else if (delimiter == ';') "Semicolon (;)" else "Tab (\\t)"}"
        )
    }

    private fun generateSignature(ts: Long, amount: Long, wId: String, note: String): String {
        return "${ts / 60000}_${amount}_${wId}_${note.trim().lowercase()}"
    }
}
