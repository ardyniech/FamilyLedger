package com.example.modules.dashboard.primitives

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ShowChart
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

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
    var isBarChart by remember { mutableStateOf(false) }
    var activePointIndex by remember { mutableStateOf<Int?>(null) }

    val monthlyPoints = remember(transactions, expenseCategories) {
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

    // Auto-select the latest month on initial loading
    LaunchedEffect(monthlyPoints) {
        if (monthlyPoints.isNotEmpty() && activePointIndex == null) {
            activePointIndex = monthlyPoints.size - 1
        }
    }

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
                        text = "Visualisasi Interaktif (Tekan & Geser Grafik)",
                        fontSize = 11.sp,
                        color = DesignTokens.TextSecondary
                    )
                }

                // Interactive Switcher Icon Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isBarChart = false },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Line Chart",
                            tint = if (!isBarChart) DesignTokens.CobaltAccent else DesignTokens.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { isBarChart = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Bar Chart",
                            tint = if (isBarChart) DesignTokens.CobaltAccent else DesignTokens.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
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

            // Canvas Trend Chart Area with Gesture Controls
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DesignTokens.SurfaceGlass)
                    .padding(vertical = 8.dp)
            ) {
                TrendCanvasChart(
                    monthlyPoints = monthlyPoints,
                    categories = expenseCategories,
                    selectedCategoryId = selectedCategoryId,
                    activePointIndex = activePointIndex,
                    isBarChart = isBarChart,
                    onActivePointChanged = { activePointIndex = it }
                )
            }

            // Interactive X-Axis Labels
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                monthlyPoints.forEachIndexed { idx, pt ->
                    Text(
                        text = pt.monthLabel.take(3),
                        fontSize = 10.sp,
                        fontWeight = if (activePointIndex == idx) FontWeight.Bold else FontWeight.Normal,
                        color = if (activePointIndex == idx) DesignTokens.CobaltAccent else DesignTokens.TextSecondary,
                        modifier = Modifier.clickable { activePointIndex = idx }
                    )
                }
            }

            // Recharts-Style Interactive Tooltip / Information Panel
            AnimatedVisibility(
                visible = activePointIndex != null && activePointIndex!! in monthlyPoints.indices,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val activeIdx = activePointIndex!!
                val pt = monthlyPoints[activeIdx]

                Card(
                    colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
                    border = BorderStroke(1.dp, DesignTokens.BorderGlass),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Rincian ${pt.monthLabel}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = DesignTokens.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val activeCats = if (selectedCategoryId != null) {
                            expenseCategories.filter { it.id == selectedCategoryId }
                        } else {
                            expenseCategories.take(4)
                        }

                        val colors = listOf(
                            DesignTokens.CobaltAccent,
                            DesignTokens.AmberAccent,
                            DesignTokens.RoseAccent,
                            DesignTokens.EmeraldGlow,
                            Color(0xFF8B5CF6)
                        )

                        activeCats.forEachIndexed { idx, cat ->
                            val amount = pt.categoryAmounts[cat.id] ?: 0.0
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(colors[idx % colors.size])
                                    )
                                    Text(
                                        text = cat.name,
                                        fontSize = 11.sp,
                                        color = DesignTokens.TextSecondary
                                    )
                                }
                                Text(
                                    text = currencyFmt.format(amount),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DesignTokens.TextPrimary
                                )
                            }
                        }

                        // Total Row
                        val totalMonthExpense = if (selectedCategoryId != null) {
                            pt.categoryAmounts[selectedCategoryId] ?: 0.0
                        } else {
                            pt.categoryAmounts.values.sum()
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = DesignTokens.BorderGlass)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Terpilih",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DesignTokens.TextPrimary
                            )
                            Text(
                                text = currencyFmt.format(totalMonthExpense),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DesignTokens.CobaltAccent
                            )
                        }
                    }
                }
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
    selectedCategoryId: String?,
    activePointIndex: Int?,
    isBarChart: Boolean,
    onActivePointChanged: (Int?) -> Unit
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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        val paddingLeft = 32f
        val paddingRight = 32f
        val paddingTop = 16f
        val paddingBottom = 16f

        val usableWidth = (width - paddingLeft - paddingRight).coerceAtLeast(1f)
        val usableHeight = (height - paddingTop - paddingBottom).coerceAtLeast(1f)
        val stepX = if (monthlyPoints.size > 1) usableWidth / (monthlyPoints.size - 1) else usableWidth

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(monthlyPoints) {
                    detectTapGestures { offset ->
                        val clickedX = offset.x - paddingLeft
                        val closestIndex = (clickedX / stepX).roundToInt().coerceIn(0, monthlyPoints.size - 1)
                        onActivePointChanged(closestIndex)
                    }
                }
                .pointerInput(monthlyPoints) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            val clickedX = change.position.x - paddingLeft
                            val closestIndex = (clickedX / stepX).roundToInt().coerceIn(0, monthlyPoints.size - 1)
                            onActivePointChanged(closestIndex)
                            change.consume()
                        }
                    )
                }
        ) {
            if (monthlyPoints.isEmpty()) return@Canvas

            // Compute max value for scaling
            val maxVal = monthlyPoints.maxOfOrNull { pt ->
                if (selectedCategoryId != null) {
                    pt.categoryAmounts[selectedCategoryId] ?: 0.0
                } else {
                    pt.categoryAmounts.values.sum()
                }
            }?.takeIf { it > 0 } ?: 100000.0

            // Draw Y-axis guide lines (dashed) and labels
            val gridLines = 3
            for (i in 0..gridLines) {
                val fraction = i.toFloat() / gridLines
                val y = paddingTop + usableHeight - (fraction * usableHeight)
                
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    start = Offset(paddingLeft, y),
                    end = Offset(width - paddingRight, y),
                    strokeWidth = 1f
                )
            }

            // Draw vertical guide line if a point is selected
            if (activePointIndex != null && activePointIndex in monthlyPoints.indices) {
                val x = paddingLeft + (activePointIndex * stepX)
                drawLine(
                    color = DesignTokens.CobaltAccent.copy(alpha = 0.4f),
                    start = Offset(x, paddingTop),
                    end = Offset(x, paddingTop + usableHeight),
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            // Draw data
            val filterCats = if (selectedCategoryId != null) {
                categories.filter { it.id == selectedCategoryId }
            } else {
                categories.take(3)
            }

            if (isBarChart) {
                // Render Stacked / Grouped Bar Chart
                val barWidth = (stepX * 0.4f).coerceIn(12f, 50f)
                monthlyPoints.forEachIndexed { i, pt ->
                    val xCenter = paddingLeft + (i * stepX)
                    
                    if (selectedCategoryId != null) {
                        val valAmount = pt.categoryAmounts[selectedCategoryId] ?: 0.0
                        val barHeight = ((valAmount / maxVal).toFloat() * usableHeight)
                        val color = colors[0]
                        val alpha = if (activePointIndex == null || activePointIndex == i) 1.5f else 0.4f
                        
                        drawRect(
                            color = color.copy(alpha = alpha.coerceIn(0f, 1f)),
                            topLeft = Offset(xCenter - barWidth / 2, paddingTop + usableHeight - barHeight),
                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                        )
                    } else {
                        var currentYOffset = 0f
                        filterCats.forEachIndexed { idx, cat ->
                            val valAmount = pt.categoryAmounts[cat.id] ?: 0.0
                            val barHeight = ((valAmount / maxVal).toFloat() * usableHeight)
                            val color = colors[idx % colors.size]
                            val alpha = if (activePointIndex == null || activePointIndex == i) 1f else 0.4f
                            
                            drawRect(
                                color = color.copy(alpha = alpha),
                                topLeft = Offset(xCenter - barWidth / 2, paddingTop + usableHeight - currentYOffset - barHeight),
                                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                            )
                            currentYOffset += barHeight
                        }
                    }
                }
            } else {
                // Render Line Chart
                filterCats.forEachIndexed { idx, cat ->
                    val strokeColor = colors[idx % colors.size]
                    val path = Path()

                    monthlyPoints.forEachIndexed { i, pt ->
                        val valAmount = pt.categoryAmounts[cat.id] ?: 0.0
                        val x = paddingLeft + (i * stepX)
                        val y = paddingTop + usableHeight - ((valAmount / maxVal).toFloat() * usableHeight)

                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)

                        val alpha = if (activePointIndex == null || activePointIndex == i) 1f else 0.3f
                        drawCircle(
                            color = strokeColor.copy(alpha = alpha),
                            radius = if (activePointIndex == i) 5.dp.toPx() else 3.5.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }

                    drawPath(
                        path = path,
                        color = strokeColor,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}
