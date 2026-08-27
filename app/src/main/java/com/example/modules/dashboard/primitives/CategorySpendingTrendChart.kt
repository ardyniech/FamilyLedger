package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

data class MonthlyCategoryPoint(
    val monthLabel: String,
    val timestamp: Long,
    val categoryAmounts: Map<String, Double>
)

@Composable
fun CategorySpendingTrendChart(
    transactions: List<Transaction>,
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    val currencyFmt = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val monthFmt = remember { SimpleDateFormat("MMM yyyy", Locale("id", "ID")) }

    val expenseCategories = remember(categories) { categories.filter { it.type == "Expense" } }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) } // null = All top categories

    val monthlyPoints = remember(transactions, expenseCategories) {
        val cal = Calendar.getInstance()
        val points = mutableListOf<MonthlyCategoryPoint>()

        for (i in 5 downTo 0) {
            val c = Calendar.getInstance().apply {
                add(Calendar.MONTH, -i)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val startMs = c.timeInMillis
            c.add(Calendar.MONTH, 1)
            val endMs = c.timeInMillis

            val monthTxs = transactions.filter { t ->
                t.timestamp in startMs until endMs && t.amount < 0
            }

            val categoryMap = expenseCategories.associate { cat ->
                val sum = monthTxs.filter { it.categoryId == cat.id }.sumOf { -it.amount }
                cat.id to sum
            }

            points.add(
                MonthlyCategoryPoint(
                    monthLabel = monthFmt.format(Date(startMs)),
                    timestamp = startMs,
                    categoryAmounts = categoryMap
                )
            )
        }
        points
    }

    // Identify highest growing category for optimization tip
    val optimizationTip = remember(monthlyPoints, expenseCategories) {
        if (monthlyPoints.size >= 2) {
            val lastMonth = monthlyPoints.last().categoryAmounts
            val prevMonth = monthlyPoints[monthlyPoints.size - 2].categoryAmounts

            var maxSpikeCat: Category? = null
            var maxSpikeDiff = 0.0

            expenseCategories.forEach { cat ->
                val curr = lastMonth[cat.id] ?: 0.0
                val prev = prevMonth[cat.id] ?: 0.0
                val diff = curr - prev
                if (diff > maxSpikeDiff && prev > 0) {
                    maxSpikeDiff = diff
                    maxSpikeCat = cat
                }
            }

            maxSpikeCat?.let { cat ->
                "💡 Tren Pengeluaran: Kategori '${cat.name}' melonjak ${currencyFmt.format(maxSpikeDiff)} dibanding bulan lalu. Evaluasi untuk optimasi anggaran keluarga."
            } ?: "💡 Tren pengeluaran keluarga stabil. Tetap jaga batas anggaran bulanan!"
        } else {
            "💡 Belum cukup data tren bulanan untuk optimasi."
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
    ) {
        Column(
            modifier = Modifier.padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Grafik Tren Pengeluaran Kategori",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DesignTokens.TextPrimary
                    )
                    Text(
                        text = "Visualisasi 6 Bulan Terakhir",
                        fontSize = 11.sp,
                        color = DesignTokens.TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = DesignTokens.CobaltAccent
                )
            }

            // Category Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FilterChipItem(
                        text = "Semua Kategori",
                        selected = selectedCategoryId == null,
                        onClick = { selectedCategoryId = null }
                    )
                }
                items(expenseCategories) { cat ->
                    FilterChipItem(
                        text = cat.name,
                        selected = selectedCategoryId == cat.id,
                        onClick = { selectedCategoryId = cat.id }
                    )
                }
            }

            // Canvas Trend Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DesignTokens.SurfaceGlass)
                    .padding(8.dp)
            ) {
                TrendCanvasChart(
                    monthlyPoints = monthlyPoints,
                    categories = expenseCategories,
                    selectedCategoryId = selectedCategoryId
                )
            }

            // Optimization Insight Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DesignTokens.AmberAccent.copy(alpha = 0.12f))
                    .padding(10.dp)
            ) {
                Text(
                    text = optimizationTip,
                    fontSize = 11.sp,
                    color = DesignTokens.TextPrimary,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun FilterChipItem(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) DesignTokens.CobaltAccent else DesignTokens.SurfaceGlass)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else DesignTokens.TextSecondary
        )
    }
}

@Composable
private fun TrendCanvasChart(
    monthlyPoints: List<MonthlyCategoryPoint>,
    categories: List<Category>,
    selectedCategoryId: String?
) {
    val colors = remember {
        listOf(
            DesignTokens.CobaltAccent,
            DesignTokens.AmberAccent,
            DesignTokens.RoseAccent,
            DesignTokens.EmeraldGlow,
            Color(0xFF8B5CF6)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (monthlyPoints.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val paddingLeft = 16f
        val paddingRight = 16f
        val paddingTop = 16f
        val paddingBottom = 24f

        val usableWidth = width - paddingLeft - paddingRight
        val usableHeight = height - paddingTop - paddingBottom

        // Compute max value for scaling
        val maxVal = monthlyPoints.maxOfOrNull { pt ->
            if (selectedCategoryId != null) {
                pt.categoryAmounts[selectedCategoryId] ?: 0.0
            } else {
                pt.categoryAmounts.values.sum()
            }
        }?.takeIf { it > 0 } ?: 100000.0

        val stepX = if (monthlyPoints.size > 1) usableWidth / (monthlyPoints.size - 1) else usableWidth

        // Render line graph for selected category or overall trend
        val filterCats = if (selectedCategoryId != null) {
            categories.filter { it.id == selectedCategoryId }
        } else {
            categories.take(3)
        }

        filterCats.forEachIndexed { idx, cat ->
            val strokeColor = colors[idx % colors.size]
            val path = Path()

            monthlyPoints.forEachIndexed { i, pt ->
                val valAmount = pt.categoryAmounts[cat.id] ?: 0.0
                val x = paddingLeft + (i * stepX)
                val y = paddingTop + usableHeight - ((valAmount / maxVal).toFloat() * usableHeight)

                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)

                drawCircle(color = strokeColor, radius = 4.dp.toPx(), center = Offset(x, y))
            }

            drawPath(
                path = path,
                color = strokeColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}
