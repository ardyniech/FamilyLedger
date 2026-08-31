package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.FinancialGoal
import com.example.shared.theme.DesignTokens

@Composable
fun GoalSelectorRow(
    goals: List<FinancialGoal>,
    selectedGoalId: String?,
    onSelectGoal: (String?) -> Unit
) {
    if (goals.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Hubungkan ke Target Impian (Opsional):",
                color = DesignTokens.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val isNoneSelected = selectedGoalId == null
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isNoneSelected) DesignTokens.CobaltAccent else DesignTokens.Surface)
                    .clickable { onSelectGoal(null) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    "Tanpa Impian",
                    color = if (isNoneSelected) Color.White else DesignTokens.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isNoneSelected) FontWeight.Bold else FontWeight.Normal
                )
            }

            goals.forEach { goal ->
                val isSelected = selectedGoalId == goal.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) DesignTokens.AmberAccent else DesignTokens.Surface)
                        .clickable { onSelectGoal(goal.id) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(goal.iconEmoji, fontSize = 14.sp)
                        Text(
                            goal.title,
                            color = if (isSelected) Color.White else DesignTokens.TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
