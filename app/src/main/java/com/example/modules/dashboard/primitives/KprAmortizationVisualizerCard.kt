package com.example.modules.dashboard.primitives

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared.utils.MathUtils

@Composable
fun KprAmortizationVisualizerCard(
    fixedInstallment: Long,
    floatingInstallment: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Komposisi Beban Bulanan (Canvas Visualizer)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            val maxVal = maxOf(fixedInstallment, floatingInstallment, 1L).toFloat()
            val fixedRatio = (fixedInstallment.toFloat() / maxVal).coerceIn(0.1f, 1f)
            val floatingRatio = (floatingInstallment.toFloat() / maxVal).coerceIn(0.1f, 1f)

            Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                val barHeight = 24.dp.toPx()
                val corner = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                val maxWidth = size.width

                // Fixed bar
                val fixedBarWidth = maxWidth * fixedRatio
                drawRoundRect(
                    color = Color(0xFF10B981),
                    topLeft = Offset(0f, 6.dp.toPx()),
                    size = Size(fixedBarWidth, barHeight),
                    cornerRadius = corner
                )

                // Floating bar
                val floatingBarWidth = maxWidth * floatingRatio
                drawRoundRect(
                    color = Color(0xFFEF4444),
                    topLeft = Offset(0f, 44.dp.toPx()),
                    size = Size(floatingBarWidth, barHeight),
                    cornerRadius = corner
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF10B981), modifier = Modifier.size(10.dp)) {}
                    Text("Fixed: ${MathUtils.formatRupiah(fixedInstallment)}", style = MaterialTheme.typography.labelSmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEF4444), modifier = Modifier.size(10.dp)) {}
                    Text("Floating: ${MathUtils.formatRupiah(floatingInstallment)}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
