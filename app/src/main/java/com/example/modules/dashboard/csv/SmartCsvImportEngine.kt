package com.example.modules.dashboard.csv

import com.example.core.storage.HouseholdRepository
import com.example.shared.models.Transaction
import java.util.UUID

object SmartCsvImportEngine {
    suspend fun executeImport(
        repository: HouseholdRepository,
        parsedTransactions: List<ParsedTransaction>,
        skipDuplicates: Boolean = true
    ): ImportExecutionResult {
        val targets = if (skipDuplicates) parsedTransactions.filter { !it.isDuplicate } else parsedTransactions
        if (targets.isEmpty()) {
            return ImportExecutionResult(0, parsedTransactions.size, 0L, 0L, true, "Tidak ada transaksi baru untuk diimpor.")
        }

        var insertedCount = 0
        var totalInflow = 0L
        var totalOutflow = 0L

        for (item in targets) {
            if (item.isTransfer && item.targetWalletId != null) {
                val transferAmount = kotlin.math.abs(item.amount)
                val outTx = Transaction(
                    id = UUID.randomUUID().toString(),
                    walletId = item.walletId,
                    memberId = item.memberId,
                    categoryId = "c_tf_out",
                    amount = -transferAmount,
                    note = if (item.note.isNotBlank()) "${item.note} (Transfer)" else "Transfer ke ${item.rawAccount}",
                    timestamp = item.timestamp
                )
                val inTx = Transaction(
                    id = UUID.randomUUID().toString(),
                    walletId = item.targetWalletId,
                    memberId = item.targetMemberId ?: "m1",
                    categoryId = "c_tf_in",
                    amount = transferAmount,
                    note = if (item.note.isNotBlank()) "${item.note} (Transfer)" else "Transfer dari ${item.rawAccount}",
                    timestamp = item.timestamp + 1
                )
                repository.addTransaction(outTx)
                repository.addTransaction(inTx)
                insertedCount += 2
            } else {
                val tx = Transaction(
                    id = UUID.randomUUID().toString(),
                    walletId = item.walletId,
                    memberId = item.memberId,
                    categoryId = item.categoryId,
                    amount = item.amount,
                    note = if (item.note.isNotBlank()) "${item.rawCategory}: ${item.note}" else item.rawCategory.ifBlank { "Transaksi CSV" },
                    timestamp = item.timestamp
                )
                repository.addTransaction(tx)
                insertedCount++

                if (item.amount > 0) totalInflow += item.amount else totalOutflow += kotlin.math.abs(item.amount)
            }
        }

        return ImportExecutionResult(
            insertedCount = insertedCount,
            skippedDuplicates = parsedTransactions.size - targets.size,
            totalInflow = totalInflow,
            totalOutflow = totalOutflow,
            isSuccess = true,
            message = "Berhasil mengimpor $insertedCount transaksi!"
        )
    }
}
