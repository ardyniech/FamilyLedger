package com.example.modules.dashboard.logic

import com.example.shared.models.*
import com.example.shared.utils.MathUtils

object NafkahAllocationCalculator {
    fun calculate(
        activeMember: Member?,
        members: List<Member>,
        wallets: List<WalletAccount>,
        transactions: List<Transaction>,
        categories: List<Category>
    ): NafkahAllocationReport {
        val isWife = activeMember?.role?.trim()?.equals("Istri", ignoreCase = true) == true
        val husband = members.find { it.role.trim().equals("Suami", ignoreCase = true) }
        val wife = members.find { it.role.trim().equals("Istri", ignoreCase = true) } ?: activeMember
        val wifeWalletIds = wallets.filter { it.memberId == wife?.id }.map { it.id }.toSet()

        val husbandWalletIds = wallets.filter { it.memberId == husband?.id }.map { it.id }.toSet()

        // 1. Transfers received by Wife from Husband (supports both incoming credit or outgoing debit representation)
        val creditTransfersToWife = transactions.filter { tx ->
            tx.walletId in wifeWalletIds &&
            tx.amount > 0 &&
            (tx.note.contains("Transfer", ignoreCase = true) || tx.note.contains("Nafkah", ignoreCase = true) || tx.note.contains("Belanja", ignoreCase = true))
        }.sumOf { it.amount }

        val debitTransfersFromHusband = transactions.filter { tx ->
            tx.walletId in husbandWalletIds &&
            tx.amount < 0 &&
            (tx.note.contains("Istri", ignoreCase = true) || tx.note.contains("Nafkah", ignoreCase = true) || tx.note.contains("Belanja", ignoreCase = true))
        }.sumOf { kotlin.math.abs(it.amount) }

        val totalReceived = if (creditTransfersToWife > 0) creditTransfersToWife else debitTransfersFromHusband

        // 2. Household expenses spent by wife or categorized under household groups
        val householdCatKeywords = listOf("dapur", "sayur", "sembako", "bumbu", "rumah", "anak", "susu", "mandi", "kebersihan", "obat", "laundry", "jajan")
        val householdCategoryIds = categories.filter { cat ->
            cat.type.equals("Expense", ignoreCase = true) &&
            householdCatKeywords.any { kw -> cat.name.contains(kw, ignoreCase = true) }
        }.map { it.id }.toSet()

        val householdTxs = transactions.filter { tx ->
            (tx.walletId in wifeWalletIds || tx.memberId == wife?.id) &&
            tx.amount < 0 &&
            (tx.categoryId in householdCategoryIds || householdCatKeywords.any { kw -> tx.note.contains(kw, ignoreCase = true) })
        }
        val totalSpent = householdTxs.sumOf { kotlin.math.abs(it.amount) }

        val remaining = totalReceived - totalSpent
        val ratio = if (totalReceived > 0) (totalSpent.toFloat() / totalReceived.toFloat()).coerceIn(0f, 1f) else if (totalSpent > 0) 1.0f else 0f

        val topExpenses = householdTxs
            .groupBy { it.categoryId }
            .map { (catId, txList) ->
                val catName = categories.find { it.id == catId }?.name ?: "Kebutuhan Dapur"
                val sum = txList.sumOf { kotlin.math.abs(it.amount) }
                HouseholdExpenseBreakdown(
                    categoryName = catName,
                    amount = sum,
                    percentageOfBudget = if (totalReceived > 0) (sum.toFloat() / totalReceived.toFloat()).coerceIn(0f, 1f) else 0f
                )
            }.sortedByDescending { it.amount }.take(4)

        val (headline, note) = when {
            totalReceived == 0L -> {
                "Menunggu Transfer Nafkah Suami" to "Belum ada catatan transfer belanja dari ${husband?.name ?: "Suami"} bulan ini. Ketuk tombol 'Minta Transfer' atau catat transfer masuk."
            }
            remaining >= 0L -> {
                "Anggaran Belanja Aman (${(ratio * 100).toInt()}% Terpakai)" to "Alhamdulillah! Uang belanja dari ${husband?.name ?: "Suami"} terkendali rapi. Sisa cadangan: ${MathUtils.formatRupiah(remaining)}."
            }
            else -> {
                "Defisit Belanja Rumah Tangga" to "Perhatian: Pengeluaran dapur melebihi transfer nafkah suami sebesar ${MathUtils.formatRupiah(kotlin.math.abs(remaining))}. Koordinasikan dengan pasangan."
            }
        }

        return NafkahAllocationReport(
            isWifeRole = isWife,
            husbandName = husband?.name ?: "Suami",
            wifeName = wife?.name ?: "Istri",
            totalReceivedFromHusband = totalReceived,
            totalHouseholdExpensesSpent = totalSpent,
            remainingBudget = remaining,
            spentPercentage = ratio,
            statusHeadline = headline,
            feedbackNote = note,
            topHouseholdExpenses = topExpenses
        )
    }
}
