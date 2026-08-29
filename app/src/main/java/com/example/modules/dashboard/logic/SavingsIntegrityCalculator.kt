package com.example.modules.dashboard.logic

import com.example.shared.models.Category
import com.example.shared.models.Transaction
import kotlin.math.abs

data class SavingsCategoryDetail(
    val category: Category,
    val totalSaved: Long,
    val totalSpentDiverted: Long,
    val netBalance: Long,
    val integrityRate: Double,
    val isCompromised: Boolean
)

data class SavingsIntegrityReport(
    val totalSavingsAllocated: Long,
    val totalSavingsUsed: Long,
    val netSavingsRetained: Long,
    val overallIntegrityRate: Double,
    val compromisedCount: Int,
    val categoryDetails: List<SavingsCategoryDetail>
)

object SavingsIntegrityCalculator {
    fun calculate(
        transactions: List<Transaction>,
        categories: List<Category>
    ): SavingsIntegrityReport {
        val savingsCategories = categories.filter { 
            it.isSavings || it.name.contains("save", ignoreCase = true) || it.name.contains("tabung", ignoreCase = true) || it.name.contains("akad", ignoreCase = true)
        }

        val details = savingsCategories.map { cat ->
            val catTxs = transactions.filter { it.categoryId == cat.id }
            
            val positiveTxs = catTxs.filter { it.amount > 0 }.sumOf { it.amount }
            val negativeTxs = catTxs.filter { it.amount < 0 }.sumOf { abs(it.amount) }
            
            val totalSaved = if (positiveTxs > 0L) positiveTxs else negativeTxs
            val totalSpentDiverted = if (positiveTxs > 0L) negativeTxs else catTxs.filter { 
                it.amount < 0 && (it.note.contains("pakai", ignoreCase = true) || it.note.contains("tarik", ignoreCase = true) || it.note.contains("bocor", ignoreCase = true))
            }.sumOf { abs(it.amount) }

            val netBalance = totalSaved - totalSpentDiverted
            val rate = if (totalSaved > 0L) ((netBalance.toDouble() / totalSaved.toDouble()) * 100.0).coerceIn(0.0, 100.0) else 100.0
            val compromised = totalSpentDiverted > 0L

            SavingsCategoryDetail(
                category = cat,
                totalSaved = totalSaved,
                totalSpentDiverted = totalSpentDiverted,
                netBalance = netBalance,
                integrityRate = rate,
                isCompromised = compromised
            )
        }.sortedByDescending { it.totalSaved }

        val totalAllocated = details.sumOf { it.totalSaved }
        val totalUsed = details.sumOf { it.totalSpentDiverted }
        val netRetained = totalAllocated - totalUsed
        val overallRate = if (totalAllocated > 0L) ((netRetained.toDouble() / totalAllocated.toDouble()) * 100.0).coerceIn(0.0, 100.0) else 100.0
        val compromisedCount = details.count { it.isCompromised }

        return SavingsIntegrityReport(
            totalSavingsAllocated = totalAllocated,
            totalSavingsUsed = totalUsed,
            netSavingsRetained = netRetained,
            overallIntegrityRate = overallRate,
            compromisedCount = compromisedCount,
            categoryDetails = details
        )
    }
}

