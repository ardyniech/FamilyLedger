package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TopCategoryAnomalyCard(
    transactions: List<Transaction>,
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    val currencyFmt = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    
    val anomalyData = remember(transactions, categories) {
        val expenses = transactions.filter { it.amount < 0 }
        if (expenses.isEmpty()) return@remember null
        
        val totalExpense = expenses.sumOf { kotlin.math.abs(it.amount) }
        if (totalExpense == 0L) return@remember null
        
        val grouped = expenses.groupBy { it.categoryId }.mapValues { (_, txs) -> txs.sumOf { kotlin.math.abs(it.amount) } }
        val topEntry = grouped.maxByOrNull { it.value } ?: return@remember null
        
        val category = categories.find { it.id == topEntry.key }
        val percentage = (topEntry.value.toDouble() / totalExpense.toDouble() * 100).toInt()
        
        Triple(category, topEntry.value, percentage)
    } ?: return
    
    val (category, amount, percentage) = anomalyData
    val catName = category?.name ?: "Lainnya"
    
    val isWarning = percentage > 35
    val icon = if (isWarning) Icons.Filled.WarningAmber else Icons.Filled.Insights
    val color = if (isWarning) DesignTokens.CrimsonAccent else DesignTokens.CobaltAccent
    val title = if (isWarning) "ANOMALI PENGELUARAN TINGGI" else "FOKUS PENGELUARAN UTAMA"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = "Anomaly", tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color, letterSpacing = 0.5.sp)
                Text("$catName menyita $percentage% total pengeluaran", fontSize = 13.sp, fontWeight = FontWeight.Black, color = DesignTokens.TextPrimary, lineHeight = 16.sp)
                Text("Total: ${currencyFmt.format(amount)} pada periode ini.", fontSize = 11.sp, color = DesignTokens.TextSecondary, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}
