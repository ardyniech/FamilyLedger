package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CategoryBudgetBreakdownCard(
    transactions: List<Transaction>,
    categories: List<Category>,
    monthlyBudget: Double,
    onCategoryClick: ((Category) -> Unit)? = null
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val expenseCategories = categories.filter { it.type == "Expense" }
    val categoryExpenses = expenseCategories.map { cat ->
        val spent = transactions.filter { it.categoryId == cat.id && it.amount < 0 }.sumOf { -it.amount }
        cat to spent
    }.filter { it.second > 0 }.sortedByDescending { it.second }

    if (categoryExpenses.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Distribusi Pengeluaran vs Anggaran", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DesignTokens.TextPrimary)

            categoryExpenses.take(4).forEach { (cat, spent) ->
                val ratio = if (monthlyBudget > 0) (spent / monthlyBudget).toFloat().coerceIn(0f, 1f) else 0f
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (onCategoryClick != null) Modifier.clickable { onCategoryClick(cat) } else Modifier),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(cat.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextPrimary)
                        Text("${formatter.format(spent)} (${(ratio * 100).toInt()}%)", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                    }
                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = DesignTokens.AmberAccent,
                        trackColor = DesignTokens.BorderLight
                    )
                }
            }
        }
    }
}
