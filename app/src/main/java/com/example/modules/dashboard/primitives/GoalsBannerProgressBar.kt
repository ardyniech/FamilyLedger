package com.example.modules.dashboard.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.GoalProgressState
import com.example.modules.dashboard.logic.OverallGoalsSummary
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat

@Composable
fun GoalsBannerProgressBar(
    summary: OverallGoalsSummary,
    activeGoalTitle: String?,
    activeProgress: GoalProgressState?,
    currencyFmt: NumberFormat
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(activeGoalTitle ?: "Target Tabungan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextPrimary)
            activeProgress?.let {
                Text("${it.percentage}% (${it.deadlineStatusText})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldGlow)
            }
        }
        LinearProgressIndicator(
            progress = { summary.progressFraction },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = DesignTokens.EmeraldGlow,
            trackColor = DesignTokens.BorderLight
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Terkumpul: ${currencyFmt.format(summary.totalAccumulated)}", fontSize = 11.sp, color = DesignTokens.TextSecondary)
            Text("Target: ${currencyFmt.format(summary.totalTarget)}", fontSize = 11.sp, color = DesignTokens.TextSecondary)
        }
    }
}
