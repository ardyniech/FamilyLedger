package com.example.modules.dashboard.csv

import com.example.core.storage.HouseholdRepository
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import kotlinx.coroutines.flow.first
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

        val existingWallets = repository.wallets.first().associateBy { it.id }.toMutableMap()
        val existingCategories = repository.categories.first().associateBy { it.id }.toMutableMap()

        for (item in targets) {
            val walletId = ensureWalletExists(repository, existingWallets, item.walletId, item.rawAccount)
            val categoryId = if (!item.isTransfer) ensureCategoryExists(repository, existingCategories, item.categoryId, item.rawCategory, item.amount > 0) else item.categoryId

            if (item.isTransfer && item.targetWalletId != null) {
                val targetWalletId = ensureWalletExists(repository, existingWallets, item.targetWalletId, item.rawAccount)
                val transferAmount = kotlin.math.abs(item.amount)
                val outTx = Transaction(UUID.randomUUID().toString(), walletId, item.memberId, "c_tf_out", -transferAmount, if (item.note.isNotBlank()) "${item.note} (Transfer)" else "Transfer ke ${item.rawAccount}", timestamp = item.timestamp)
                val inTx = Transaction(UUID.randomUUID().toString(), targetWalletId, item.targetMemberId ?: "m1", "c_tf_in", transferAmount, if (item.note.isNotBlank()) "${item.note} (Transfer)" else "Transfer dari ${item.rawAccount}", timestamp = item.timestamp + 1)
                repository.addTransaction(outTx)
                repository.addTransaction(inTx)
                insertedCount += 2
            } else {
                val tx = Transaction(UUID.randomUUID().toString(), walletId, item.memberId, categoryId, item.amount, if (item.note.isNotBlank()) "${item.rawCategory}: ${item.note}".trimEnd(':', ' ') else item.rawCategory.ifBlank { "Transaksi CSV" }, timestamp = item.timestamp)
                repository.addTransaction(tx)
                insertedCount++

                if (item.amount > 0) totalInflow += item.amount else totalOutflow += kotlin.math.abs(item.amount)
            }
        }

        return ImportExecutionResult(insertedCount, parsedTransactions.size - targets.size, totalInflow, totalOutflow, true, "Berhasil mengimpor $insertedCount transaksi ke database!")
    }

    private suspend fun ensureWalletExists(repo: HouseholdRepository, wallets: MutableMap<String, WalletAccount>, walletId: String, rawName: String): String {
        if (wallets.containsKey(walletId)) return walletId
        val cleanName = rawName.replace("->", " ").trim().ifBlank { walletId }
        val memberId = if (walletId == "w_deina") "m2" else "m1"
        val walletType = if (cleanName.lowercase().contains("bca") || cleanName.lowercase().contains("bank")) "Bank" else if (cleanName.lowercase().contains("gopay") || cleanName.lowercase().contains("ovo") || cleanName.lowercase().contains("dana")) "E-Wallet" else "Cash"
        val newWallet = WalletAccount(walletId, memberId, walletType, cleanName, 0L)
        repo.addWallet(newWallet)
        wallets[walletId] = newWallet
        return walletId
    }

    private suspend fun ensureCategoryExists(repo: HouseholdRepository, categories: MutableMap<String, Category>, catId: String, rawName: String, isIncome: Boolean): String {
        if (categories.containsKey(catId)) return catId
        val cleanName = rawName.trim().ifBlank { if (isIncome) "Pemasukan CSV" else "Pengeluaran CSV" }
        val newCat = Category(catId, cleanName, if (isIncome) "Income" else "Expense", if (isIncome) "payments" else "shopping_bag")
        repo.addCategory(newCat)
        categories[catId] = newCat
        return catId
    }
}
