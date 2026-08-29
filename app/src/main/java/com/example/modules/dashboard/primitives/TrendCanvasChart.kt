package com.example.modules.dashboard.primitives

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.shared.models.Category
import com.example.shared.models.MonthlyCategoryPoint
import com.example.shared.theme.DesignTokens
import kotlin.math.roundToInt

@Composable
fun TrendCanvasChart(
    monthlyPoints: List<MonthlyCategoryPoint>,
    categories: List<Category>,
    selectedCategoryId: String?,
    activePointIndex: Int?,
    isBarChart: Boolean,
    onActivePointChanged: (Int?) -> Unit
) {
    val colors = remember {
        listOf(DesignTokens.CobaltAccent, DesignTokens.AmberAccent, DesignTokens.RoseAccent, DesignTokens.EmeraldGlow, Color(0xFF8B5CF6))
    }
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val pLeft = 32f; val pRight = 32f; val pTop = 16f; val pBottom = 16f
        val usableW = (width - pLeft - pRight).coerceAtLeast(1f)
        val usableH = (height - pTop - pBottom).coerceAtLeast(1f)
        val stepX = if (monthlyPoints.size > 1) usableW / (monthlyPoints.size - 1) else usableW

        Canvas(
            modifier = Modifier.fillMaxSize()
                .pointerInput(monthlyPoints) { detectTapGestures { offset -> onActivePointChanged(((offset.x - pLeft) / stepX).roundToInt().coerceIn(0, monthlyPoints.size - 1)) } }
                .pointerInput(monthlyPoints) { detectDragGestures { change, _ -> onActivePointChanged(((change.position.x - pLeft) / stepX).roundToInt().coerceIn(0, monthlyPoints.size - 1)); change.consume() } }
        ) {
            if (monthlyPoints.isEmpty()) return@Canvas
            val maxVal = monthlyPoints.maxOfOrNull { pt -> if (selectedCategoryId != null) pt.categoryAmounts[selectedCategoryId] ?: 0L else pt.categoryAmounts.values.sum() }?.takeIf { it > 0L }?.toDouble() ?: 100000.0
            for (i in 0..3) {
                val y = pTop + usableH - (i.toFloat() / 3 * usableH)
                drawLine(color = Color.LightGray.copy(alpha = 0.2f), start = Offset(pLeft, y), end = Offset(width - pRight, y), strokeWidth = 1f)
            }
            if (activePointIndex != null && activePointIndex in monthlyPoints.indices) {
                val x = pLeft + (activePointIndex * stepX)
                drawLine(color = DesignTokens.CobaltAccent.copy(alpha = 0.4f), start = Offset(x, pTop), end = Offset(x, pTop + usableH), strokeWidth = 1.5.dp.toPx())
            }
            val filterCats = if (selectedCategoryId != null) categories.filter { it.id == selectedCategoryId } else categories.take(3)
            if (isBarChart) {
                val barW = (stepX * 0.4f).coerceIn(12f, 50f)
                monthlyPoints.forEachIndexed { i, pt ->
                    val xCenter = pLeft + (i * stepX)
                    if (selectedCategoryId != null) {
                        val v = (pt.categoryAmounts[selectedCategoryId] ?: 0L).toDouble()
                        val bH = ((v / maxVal).toFloat() * usableH)
                        val alpha = if (activePointIndex == null || activePointIndex == i) 1.0f else 0.4f
                        drawRect(color = colors[0].copy(alpha = alpha), topLeft = Offset(xCenter - barW / 2, pTop + usableH - bH), size = Size(barW, bH))
                    } else {
                        var curY = 0f
                        filterCats.forEachIndexed { idx, cat ->
                            val v = (pt.categoryAmounts[cat.id] ?: 0L).toDouble()
                            val bH = ((v / maxVal).toFloat() * usableH)
                            val alpha = if (activePointIndex == null || activePointIndex == i) 1f else 0.4f
                            drawRect(color = colors[idx % colors.size].copy(alpha = alpha), topLeft = Offset(xCenter - barW / 2, pTop + usableH - curY - bH), size = Size(barW, bH))
                            curY += bH
                        }
                    }
                }
            } else {
                filterCats.forEachIndexed { idx, cat ->
                    val strokeColor = colors[idx % colors.size]
                    val path = Path()
                    monthlyPoints.forEachIndexed { i, pt ->
                        val v = (pt.categoryAmounts[cat.id] ?: 0L).toDouble()
                        val x = pLeft + (i * stepX)
                        val y = pTop + usableH - ((v / maxVal).toFloat() * usableH)
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        val alpha = if (activePointIndex == null || activePointIndex == i) 1f else 0.3f
                        drawCircle(color = strokeColor.copy(alpha = alpha), radius = if (activePointIndex == i) 5.dp.toPx() else 3.5.dp.toPx(), center = Offset(x, y))
                    }
                    drawPath(path = path, color = strokeColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                }
            }
        }
    }
}
