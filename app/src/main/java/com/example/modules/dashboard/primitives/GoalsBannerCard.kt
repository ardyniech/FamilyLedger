package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.shared.atoms.springClickable
import com.example.shared.models.FinancialGoal
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GoalsBannerCard(
    goals: List<FinancialGoal>,
    transactions: List<Transaction> = emptyList(),
    onClick: () -> Unit
) {
    val currencyFmt = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val summary = remember(goals, transactions) { GoalProgressCalculator.calculateOverall(goals, transactions) }
    val activeGoal = remember(goals) { goals.firstOrNull() }
    val activeProgress = remember(activeGoal, transactions) { activeGoal?.let { GoalProgressCalculator.calculate(it, transactions) } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .springClickable { onClick() },
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DesignTokens.AmberAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(activeGoal?.iconEmoji ?: "🎯", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            "Target Impian & Tabungan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DesignTokens.TextPrimary
                        )
                        Text(
                            "${goals.size} Target • ${summary.completedGoalsCount} Tercapai",
                            fontSize = 11.sp,
                            color = DesignTokens.TextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DesignTokens.CobaltAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        "Kelola →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DesignTokens.CobaltAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (goals.isNotEmpty()) {
                GoalsBannerProgressBar(
                    summary = summary,
                    activeGoalTitle = activeGoal?.title,
                    activeProgress = activeProgress,
                    currencyFmt = currencyFmt
                )
            }
        }
    }
}
