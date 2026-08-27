package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.FinancialGoal
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GoalItemCard(
    goal: FinancialGoal,
    onDepositClick: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f

    Card(
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDepositClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        Text(goal.title, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                        Text(goal.category, fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    }
                }

                Button(
                    onClick = onDepositClick,
                    colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.SurfaceGlass),
                    border = BorderStroke(1.dp, DesignTokens.BorderGlass),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("+ Nabung", fontSize = 11.sp, color = DesignTokens.TextPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${formatter.format(goal.currentAmount)} / ${formatter.format(goal.targetAmount)}", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text("${(progress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.AmberAccent)
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = DesignTokens.AmberAccent,
                    trackColor = DesignTokens.BorderLight,
                )
            }
        }
    }
}
