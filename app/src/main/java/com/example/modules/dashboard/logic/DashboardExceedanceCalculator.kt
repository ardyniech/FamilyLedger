package com.example.modules.dashboard.logic

import com.example.shared.models.Category
import com.example.shared.models.CategoryExceedance
import com.example.shared.models.Transaction
import java.util.Calendar

object DashboardExceedanceCalculator {
    fun calculate(transactions: List<Transaction>, categories: List<Category>): List<CategoryExceedance> {
        val cal = Calendar.getInstance()
        val curYear = cal.get(Calendar.YEAR)
        val curMonth = cal.get(Calendar.MONTH)
        val monthTxs = transactions.filter { t ->
            val tCal = Calendar.getInstance().apply { timeInMillis = t.timestamp }
            tCal.get(Calendar.YEAR) == curYear && tCal.get(Calendar.MONTH) == curMonth && t.amount < 0 && !t.isDeleted
        }
        return categories.filter { it.type == "Expense" && !it.isDeleted && it.budgetLimit > 0.0 }.mapNotNull { cat ->
            val spent = monthTxs.filter { it.categoryId == cat.id }.sumOf { -it.amount }
            if (spent > cat.budgetLimit) CategoryExceedance(cat, cat.budgetLimit, spent) else null
        }
    }
}
