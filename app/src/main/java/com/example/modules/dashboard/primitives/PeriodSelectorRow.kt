package com.example.modules.dashboard.primitives

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.DashboardPeriod
import com.example.shared.theme.DesignTokens

@Composable
fun PeriodSelectorRow(
    selectedPeriod: DashboardPeriod,
    onPeriodSelected: (DashboardPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DashboardPeriod.values().forEach { period ->
            val isSelected = period == selectedPeriod
            val bgCol by animateColorAsState(
                targetValue = if (isSelected) DesignTokens.CobaltAccent else DesignTokens.Surface,
                label = "period_bg"
            )
            val textCol by animateColorAsState(
                targetValue = if (isSelected) Color.White else DesignTokens.TextSecondary,
                label = "period_text"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgCol)
                    .clickable { onPeriodSelected(period) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = period.displayName,
                    color = textCol,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
