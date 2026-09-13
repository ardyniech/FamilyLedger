package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.DueDateUrgency
import com.example.modules.dashboard.logic.LoanDueDateInfo
import com.example.shared.theme.DesignTokens

@Composable
fun DueDateUrgencyBadge(
    dueDateInfo: LoanDueDateInfo,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (dueDateInfo.urgency) {
        DueDateUrgency.TODAY -> Triple(DesignTokens.RoseAccent.copy(alpha = 0.18f), DesignTokens.RoseAccent, Icons.Default.Warning)
        DueDateUrgency.CRITICAL_UPCOMING -> Triple(DesignTokens.AmberAccent.copy(alpha = 0.20f), DesignTokens.AmberAccent, Icons.Default.Warning)
        DueDateUrgency.SOON_UPCOMING -> Triple(DesignTokens.CobaltAccent.copy(alpha = 0.15f), DesignTokens.CobaltAccent, Icons.Default.CalendarMonth)
        DueDateUrgency.NORMAL -> Triple(DesignTokens.SurfaceGlass, DesignTokens.TextSecondary, Icons.Default.CalendarMonth)
        DueDateUrgency.SETTLED -> Triple(DesignTokens.EmeraldGlow.copy(alpha = 0.15f), DesignTokens.EmeraldGlow, Icons.Default.CalendarMonth)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Due date urgency",
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = dueDateInfo.badgeLabel,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
