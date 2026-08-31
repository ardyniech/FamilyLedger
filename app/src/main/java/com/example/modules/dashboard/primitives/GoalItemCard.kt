package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.GoalProgressCalculator
import com.example.shared.models.FinancialGoal
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GoalItemCard(
    goal: FinancialGoal,
    transactions: List<Transaction> = emptyList(),
    onClick: () -> Unit
) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val progress = remember(goal, transactions) { GoalProgressCalculator.calculate(goal, transactions) }

    Card(
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(goal.iconEmoji, fontSize = 24.sp)
                    Column {
                        Text(goal.title, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 14.sp)
                        Text(progress.deadlineStatusText, fontSize = 11.sp, color = DesignTokens.CobaltAccent)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (progress.isCompleted) DesignTokens.EmeraldGlow.copy(alpha = 0.2f) else DesignTokens.AmberAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        "${progress.percentage}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (progress.isCompleted) DesignTokens.EmeraldGlow else DesignTokens.AmberAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { progress.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (progress.isCompleted) DesignTokens.EmeraldGlow else DesignTokens.AmberAccent,
                    trackColor = DesignTokens.BorderLight,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${formatter.format(progress.totalAccumulated)} / ${formatter.format(goal.targetAmount)}",
                        fontSize = 11.sp,
                        color = DesignTokens.TextSecondary
                    )
                    Text(
                        if (progress.taggedTransactions.isNotEmpty()) "${progress.taggedTransactions.size} transaksi" else "Detail →",
                        fontSize = 11.sp,
                        color = DesignTokens.TextSecondary
                    )
                }
            }
        }
    }
}
