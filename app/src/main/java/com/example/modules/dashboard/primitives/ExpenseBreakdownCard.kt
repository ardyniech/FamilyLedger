package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.atoms.springClickable
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExpenseBreakdownCard(
    transactions: List<Transaction>,
    categories: List<Category>,
    onClick: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val expenseTransactions = transactions.filter { t -> t.amount < 0 && categories.find { it.id == t.categoryId }?.type == "Expense" }
    val totalExpenses = expenseTransactions.sumOf { -it.amount }
    val categoryTotals = expenseTransactions.groupBy { it.categoryId }.mapValues { entry -> entry.value.sumOf { -it.amount } }
    val palette = listOf(DesignTokens.CobaltAccent, DesignTokens.AmberAccent, DesignTokens.EmeraldGlow, Color(0xFF8B5CF6), Color(0xFFEC4899))

    Card(
        modifier = Modifier.fillMaxWidth().springClickable { onClick() },
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(DesignTokens.PaddingMedium), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Expense Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    Text("Distribution by Category", style = MaterialTheme.typography.bodySmall, color = DesignTokens.TextSecondary)
                }
                Text("Details →", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent, fontSize = 14.sp)
            }

            if (totalExpenses == 0.0) {
                Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    Text("No expenses recorded yet", color = DesignTokens.TextSecondary)
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth().height(18.dp).clip(RoundedCornerShape(9.dp)).background(DesignTokens.BorderLight)) {
                    categoryTotals.entries.sortedByDescending { it.value }.forEachIndexed { index, entry ->
                        val ratio = (entry.value / totalExpenses).toFloat()
                        if (ratio > 0.01f) {
                            Box(modifier = Modifier.fillMaxHeight().weight(ratio.coerceAtLeast(0.01f)).background(palette[index % palette.size]))
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    categoryTotals.entries.sortedByDescending { it.value }.take(4).forEachIndexed { index, entry ->
                        val category = categories.find { it.id == entry.key }
                        val ratio = (entry.value / totalExpenses).toFloat()
                        val color = palette[index % palette.size]
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(color))
                                Text(category?.name ?: "Other", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = DesignTokens.TextPrimary)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(formatter.format(entry.value), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                                Text("${(ratio * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
                            }
                        }
                    }
                }
            }
        }
    }
}
