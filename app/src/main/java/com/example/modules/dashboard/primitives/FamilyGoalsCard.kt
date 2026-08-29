package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.FinancialGoal
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FamilyGoalsCard(goals: List<FinancialGoal>, onClick: () -> Unit) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val totalTarget = goals.sumOf { it.targetAmount }
    val totalCurrent = goals.sumOf { it.currentAmount }
    val overallProgress = if (totalTarget > 0) (totalCurrent / totalTarget).toFloat().coerceIn(0f, 1f) else 0f

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Rencana & Impian Bersama", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    Text("${goals.size} Target Tabungan Keluarga", style = MaterialTheme.typography.bodySmall, color = DesignTokens.TextSecondary)
                }
                Text("Buka Impian →", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent, fontSize = 13.sp)
            }

            if (goals.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                    Text("Belum ada target impian bersama. Yuk buat!", color = DesignTokens.TextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(DesignTokens.BorderLight)) {
                        Box(
                            modifier = Modifier.fillMaxHeight().fillMaxWidth(overallProgress)
                                .background(brush = Brush.linearGradient(listOf(DesignTokens.EmeraldGlow, DesignTokens.CobaltAccent)))
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Terkumpul: ${formatter.format(totalCurrent)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldGlow)
                        Text("${(overallProgress * 100).toInt()}% dari target", style = MaterialTheme.typography.bodySmall, color = DesignTokens.TextSecondary)
                    }
                }
            }
        }
    }
}

