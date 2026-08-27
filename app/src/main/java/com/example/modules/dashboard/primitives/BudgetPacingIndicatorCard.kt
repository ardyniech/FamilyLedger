package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun BudgetPacingIndicatorCard(
    monthlyBudget: Double,
    totalExpenses: Double,
    goalCount: Int
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val cal = Calendar.getInstance()
    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val daysRemaining = (maxDays - dayOfMonth + 1).coerceAtLeast(1)

    val remainingBudget = (monthlyBudget - totalExpenses).coerceAtLeast(0.0)
    val recommendedDaily = remainingBudget / daysRemaining
    val currentDailyAvg = if (dayOfMonth > 0) totalExpenses / dayOfMonth else totalExpenses

    val isOverBudget = totalExpenses > monthlyBudget
    val isNearLimit = !isOverBudget && (totalExpenses / monthlyBudget) > 0.85

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(if (isOverBudget) "🚨" else if (isNearLimit) "⚠️" else "⚡", fontSize = 16.sp)
                    Text("Laju Anggaran Harian", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DesignTokens.TextPrimary)
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(
                        if (isOverBudget) DesignTokens.RoseAccent.copy(alpha = 0.15f)
                        else if (isNearLimit) DesignTokens.AmberAccent.copy(alpha = 0.15f)
                        else DesignTokens.EmeraldAccent.copy(alpha = 0.15f)
                    ).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isOverBudget) "Over Limit" else if (isNearLimit) "Waspada" else "Terkendali",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverBudget) DesignTokens.RoseAccent else if (isNearLimit) DesignTokens.AmberAccent else DesignTokens.EmeraldAccent
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Jatah Harian Sisa ($daysRemaining hari)", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text(formatter.format(recommendedDaily), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Rata-rata Terpakai/Hari", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text(formatter.format(currentDailyAvg), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                }
            }

            Surface(shape = RoundedCornerShape(8.dp), color = DesignTokens.Surface, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🎯", fontSize = 14.sp)
                    Text(
                        text = if (isOverBudget) "Anggaran terlampaui, target $goalCount impian keluarga mungkin tertunda."
                        else "Sisa anggaran ${formatter.format(remainingBudget)} siap dialokasikan ke $goalCount target impian keluarga.",
                        fontSize = 11.sp,
                        color = DesignTokens.TextSecondary,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
