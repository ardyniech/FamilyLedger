package com.example.modules.dashboard.primitives

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.modules.dashboard.logic.GroupSpendingSummary

@Composable
fun CategoryGroupPieChart(
    summaries: List<GroupSpendingSummary>,
    isExpenseMode: Boolean,
    modifier: Modifier = Modifier
) {
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(summaries, isExpenseMode) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(750))
    }

    val totalAmount = if (isExpenseMode) summaries.sumOf { it.totalExpense } else summaries.sumOf { it.totalIncome }

    Box(modifier = modifier.size(160.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val strokeWidth = 24.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
            val arcSize = Size(diameter, diameter)

            if (totalAmount <= 0.0) {
                drawArc(
                    color = Color.Gray.copy(alpha = 0.3f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )
                return@Canvas
            }

            var startAngle = -90f
            summaries.forEach { summary ->
                val amount = if (isExpenseMode) summary.totalExpense else summary.totalIncome
                if (amount > 0) {
                    val sweep = ((amount / totalAmount) * 360f * animationProgress.value).toFloat()
                    val color = try {
                        Color(android.graphics.Color.parseColor(summary.group.colorHex))
                    } catch (e: Exception) {
                        Color(0xFF3B82F6)
                    }

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                    startAngle += sweep
                }
            }
        }
    }
}
