package com.example.modules.dashboard.logic

import com.example.shared.models.Transaction
import java.util.UUID

object CsvTransactionBuilder {
    fun buildTransactions(): List<Transaction> {
        val allRecords = mutableListOf<CsvRecord>().apply {
            addAll(RawCsvRecords1.getRecords())
            addAll(RawCsvRecords2.getRecords())
            addAll(RawCsvRecords3.getRecords())
            addAll(RawCsvRecords4.getRecords())
            addAll(RawCsvRecords5.getRecords())
            addAll(RawCsvRecords6.getRecords())
            addAll(RawCsvRecords7.getRecords())
            addAll(RawCsvRecords8.getRecords())
            addAll(RawCsvRecords9.getRecords())
            addAll(RawCsvRecords10.getRecords())
            addAll(RawCsvRecords11.getRecords())
            addAll(RawCsvRecords12.getRecords())
        }

        val result = mutableListOf<Transaction>()
        var counter = 1

        for (rec in allRecords) {
            val ts = CsvDataConverter.parseTimestamp(rec.time)
            val notesSuffix = if (rec.notes.isNotBlank()) " (${rec.notes})" else ""

            when {
                rec.type.contains("Transfer") || rec.account.contains("->") -> {
                    val parts = rec.account.split("->")
                    if (parts.size == 2) {
                        val fromAcc = parts[0].trim()
                        val toAcc = parts[1].trim()
                        val fromW = CsvDataConverter.mapWalletId(fromAcc)
                        val toW = CsvDataConverter.mapWalletId(toAcc)
                        val fromM = CsvDataConverter.mapMemberId(fromW)
                        val toM = CsvDataConverter.mapMemberId(toW)

                        // Outflow from source wallet
                        result.add(
                            Transaction(
                                id = "tx_${counter++}",
                                walletId = fromW,
                                memberId = fromM,
                                categoryId = "c_tf_out",
                                amount = -rec.amount.toLong(),
                                note = "Transfer ke $toAcc$notesSuffix",
                                timestamp = ts
                            )
                        )
                        // Inflow to destination wallet
                        result.add(
                            Transaction(
                                id = "tx_${counter++}",
                                walletId = toW,
                                memberId = toM,
                                categoryId = "c_tf_in",
                                amount = rec.amount.toLong(),
                                note = "Transfer dari $fromAcc$notesSuffix",
                                timestamp = ts + 1
                            )
                        )
                    }
                }
                rec.type.contains("Income") -> {
                    val wId = CsvDataConverter.mapWalletId(rec.account)
                    val mId = CsvDataConverter.mapMemberId(wId)
                    val cId = CsvDataConverter.mapCategoryId(rec.category)
                    result.add(
                        Transaction(
                            id = "tx_${counter++}",
                            walletId = wId,
                            memberId = mId,
                            categoryId = cId,
                            amount = rec.amount.toLong(),
                            note = "${rec.category.replaceFirstChar { it.uppercase() }}$notesSuffix",
                            timestamp = ts
                        )
                    )
                }
                else -> { // Expense
                    val wId = CsvDataConverter.mapWalletId(rec.account)
                    val mId = CsvDataConverter.mapMemberId(wId)
                    val cId = CsvDataConverter.mapCategoryId(rec.category)
                    result.add(
                        Transaction(
                            id = "tx_${counter++}",
                            walletId = wId,
                            memberId = mId,
                            categoryId = cId,
                            amount = -rec.amount.toLong(),
                            note = "${rec.category.replaceFirstChar { it.uppercase() }}$notesSuffix",
                            timestamp = ts
                        )
                    )
                }
            }
        }
        return result
    }
}
