package com.example.modules.dashboard.primitives

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.MonthlyCategoryPoint
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CategorySpendingTrendChart(
    transactions: List<Transaction>,
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    val currencyFmt = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val monthFmt = remember { SimpleDateFormat("MMM yyyy", Locale("id", "ID")) }
    val expenseCategories = remember(categories) { categories.filter { it.type == "Expense" } }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var isBarChart by remember { mutableStateOf(false) }
    var activePointIndex by remember { mutableStateOf<Int?>(null) }

    val monthlyPoints = remember(transactions, expenseCategories) {
        (5 downTo 0).map { i ->
            val c = Calendar.getInstance().apply { add(Calendar.MONTH, -i); set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }
            val startMs = c.timeInMillis; c.add(Calendar.MONTH, 1); val endMs = c.timeInMillis
            val monthTxs = transactions.filter { it.timestamp in startMs until endMs && it.amount < 0 }
            val catMap = expenseCategories.associate { cat -> cat.id to monthTxs.filter { it.categoryId == cat.id }.sumOf { -it.amount } }
            MonthlyCategoryPoint(monthLabel = monthFmt.format(Date(startMs)), timestamp = startMs, categoryAmounts = catMap)
        }
    }
    LaunchedEffect(monthlyPoints) { if (monthlyPoints.isNotEmpty() && activePointIndex == null) activePointIndex = monthlyPoints.size - 1 }

    val tip = remember(monthlyPoints, expenseCategories) {
        if (monthlyPoints.size >= 2) {
            val last = monthlyPoints.last().categoryAmounts; val prev = monthlyPoints[monthlyPoints.size - 2].categoryAmounts
            val maxSpike = expenseCategories.mapNotNull { cat -> val d = (last[cat.id] ?: 0L) - (prev[cat.id] ?: 0L); if (d > 0L && (prev[cat.id] ?: 0L) > 0L) cat to d else null }.maxByOrNull { it.second }
            maxSpike?.let { (cat, diff) -> "💡 Tren: '${cat.name}' melonjak ${currencyFmt.format(diff)} dibanding bulan lalu. Evaluasi anggaran keluarga." } ?: "💡 Tren pengeluaran keluarga stabil. Tetap jaga batas anggaran bulanan!"
        } else "💡 Belum cukup data tren bulanan untuk optimasi."
    }

    Card(
        modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface), border = BorderStroke(1.dp, DesignTokens.BorderGlass), elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
    ) {
        Column(modifier = Modifier.padding(DesignTokens.PaddingMedium), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Grafik Tren Pengeluaran Kategori", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DesignTokens.TextPrimary)
                    Text("Visualisasi Interaktif (Tekan & Geser Grafik)", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { isBarChart = false }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.ShowChart, "Line", tint = if (!isBarChart) DesignTokens.CobaltAccent else DesignTokens.TextSecondary, modifier = Modifier.size(20.dp)) }
                    IconButton(onClick = { isBarChart = true }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.BarChart, "Bar", tint = if (isBarChart) DesignTokens.CobaltAccent else DesignTokens.TextSecondary, modifier = Modifier.size(20.dp)) }
                }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item { FilterChipItem("Semua Kategori", selectedCategoryId == null) { selectedCategoryId = null } }
                items(expenseCategories) { cat -> FilterChipItem(cat.name, selectedCategoryId == cat.id) { selectedCategoryId = cat.id } }
            }
            Box(modifier = Modifier.fillMaxWidth().height(170.dp).clip(RoundedCornerShape(12.dp)).background(DesignTokens.SurfaceGlass).padding(vertical = 8.dp)) {
                TrendCanvasChart(monthlyPoints = monthlyPoints, categories = expenseCategories, selectedCategoryId = selectedCategoryId, activePointIndex = activePointIndex, isBarChart = isBarChart, onActivePointChanged = { activePointIndex = it })
            }
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                monthlyPoints.forEachIndexed { idx, pt ->
                    Text(text = pt.monthLabel.take(3), fontSize = 10.sp, fontWeight = if (activePointIndex == idx) FontWeight.Bold else FontWeight.Normal, color = if (activePointIndex == idx) DesignTokens.CobaltAccent else DesignTokens.TextSecondary, modifier = Modifier.clickable { activePointIndex = idx })
                }
            }
            AnimatedVisibility(visible = activePointIndex != null && activePointIndex!! in monthlyPoints.indices, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                CategoryTrendTooltip(pt = monthlyPoints[activePointIndex!!], selectedCategoryId = selectedCategoryId, expenseCategories = expenseCategories, currencyFmt = currencyFmt)
            }
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(DesignTokens.AmberAccent.copy(alpha = 0.12f)).padding(10.dp)) {
                Text(text = tip, fontSize = 11.sp, color = DesignTokens.TextPrimary, lineHeight = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun FilterChipItem(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (selected) DesignTokens.CobaltAccent else DesignTokens.SurfaceGlass).clickable { onClick() }.padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text = text, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = if (selected) Color.White else DesignTokens.TextSecondary)
    }
}

