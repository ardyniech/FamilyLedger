package com.example.modules.dashboard.logic

import com.example.shared.models.Category
import com.example.shared.models.CategoryGroup
import com.example.shared.models.Transaction
import kotlin.math.abs

data class CategoryGroupItem(
    val category: Category,
    val totalExpense: Long,
    val totalIncome: Long,
    val transactionCount: Int
)

data class GroupSpendingSummary(
    val group: CategoryGroup,
    val totalExpense: Long,
    val totalIncome: Long,
    val percentOfTotalExpense: Double,
    val percentOfTotalIncome: Double,
    val categoryItems: List<CategoryGroupItem>
)

object CategoryGroupCalculator {
    fun calculate(
        transactions: List<Transaction>,
        categories: List<Category>,
        groups: List<CategoryGroup>
    ): List<GroupSpendingSummary> {
        val totalExpenseAll = transactions
            .filter { it.amount < 0 && !it.note.contains("transfer", ignoreCase = true) }
            .sumOf { abs(it.amount) }
        val totalIncomeAll = transactions
            .filter { it.amount > 0 && !it.note.contains("transfer", ignoreCase = true) }
            .sumOf { it.amount }

        val unassignedGroup = CategoryGroup(
            id = "unassigned",
            name = "Tanpa Grup",
            colorHex = "#9CA3AF",
            iconName = "📦",
            description = "Kategori belum dikelompokkan"
        )
        val allGroups = groups + listOf(unassignedGroup)

        return allGroups.mapNotNull { group ->
            val groupCategories = if (group.id == "unassigned") {
                categories.filter { it.groupId == null || it.groupId.isEmpty() || groups.none { g -> g.id == it.groupId } }
            } else {
                categories.filter { it.groupId == group.id }
            }

            val groupCatIds = groupCategories.map { it.id }.toSet()
            val groupTxs = transactions.filter { it.categoryId in groupCatIds }

            val expSum = groupTxs.filter { it.amount < 0 && !it.note.contains("transfer", ignoreCase = true) }.sumOf { abs(it.amount) }
            val incSum = groupTxs.filter { it.amount > 0 && !it.note.contains("transfer", ignoreCase = true) }.sumOf { it.amount }

            if (expSum == 0L && incSum == 0L && group.id == "unassigned") return@mapNotNull null

            val items = groupCategories.map { cat ->
                val catTxs = transactions.filter { it.categoryId == cat.id }
                val catExp = catTxs.filter { it.amount < 0 && !it.note.contains("transfer", ignoreCase = true) }.sumOf { abs(it.amount) }
                val catInc = catTxs.filter { it.amount > 0 && !it.note.contains("transfer", ignoreCase = true) }.sumOf { it.amount }
                CategoryGroupItem(cat, catExp, catInc, catTxs.size)
            }.filter { it.totalExpense > 0L || it.totalIncome > 0L }

            val expPct = if (totalExpenseAll > 0L) (expSum.toDouble() / totalExpenseAll.toDouble()) * 100.0 else 0.0
            val incPct = if (totalIncomeAll > 0L) (incSum.toDouble() / totalIncomeAll.toDouble()) * 100.0 else 0.0

            GroupSpendingSummary(group, expSum, incSum, expPct, incPct, items)
        }.sortedByDescending { it.totalExpense }
    }
}

