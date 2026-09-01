package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.PeriodSummary
import com.example.shared.atoms.springClickable
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SavingsRatioGaugeCard(
    summary: PeriodSummary,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currencyFmt = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val inflow = summary.totalInflow
    val outflow = summary.totalOutflow
    val netSavings = (inflow - outflow)

    val savingsRate = if (inflow > 0) {
        ((netSavings.toDouble() / inflow.toDouble()) * 100.0).toInt().coerceIn(-100, 100)
    } else 0

    val (grade, gradeColor, emoji) = when {
        savingsRate >= 30 -> Triple("Sangat Prima (30%+)", DesignTokens.EmeraldGlow, "🏆")
        savingsRate >= 15 -> Triple("Sehat & Positif", DesignTokens.CobaltAccent, "✨")
        savingsRate > 0 -> Triple("Cukup Baik", DesignTokens.AmberAccent, "⚖️")
        else -> Triple("Defisit / Overspend", DesignTokens.CrimsonAccent, "⚠️")
    }

    val progressFraction = (savingsRate.coerceAtLeast(0) / 50.0).coerceIn(0.0, 1.0).toFloat()

    Card(
        modifier = modifier.fillMaxWidth().springClickable { onClick?.invoke() },
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.PieChart, contentDescription = "Savings", tint = DesignTokens.CobaltAccent, modifier = Modifier.size(16.dp))
                    Text("RASIO TABUNGAN KELUARGA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextSecondary, letterSpacing = 0.5.sp)
                }
                Surface(shape = RoundedCornerShape(6.dp), color = gradeColor.copy(alpha = 0.15f)) {
                    Text("$emoji $grade", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = gradeColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column {
                    Text(text = "$savingsRate%", fontSize = 24.sp, fontWeight = FontWeight.Black, color = gradeColor)
                    Text(text = "Rasio dana terselamatkan", fontSize = 11.sp, color = DesignTokens.TextMuted)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = if (netSavings >= 0) "Surplus Tabungan" else "Defisit Kas", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text(text = currencyFmt.format(netSavings), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (netSavings >= 0) DesignTokens.EmeraldGlow else DesignTokens.CrimsonAccent)
                }
            }

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = gradeColor,
                trackColor = DesignTokens.BorderLight
            )
        }
    }
}
