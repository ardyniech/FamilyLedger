package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.FabPosition
import com.example.shared.theme.DesignTokens

@Composable
fun FabPositionOptionCard(
    position: FabPosition,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DesignTokens.CobaltAccent.copy(alpha = 0.15f) else DesignTokens.Surface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) DesignTokens.CobaltAccent else DesignTokens.BorderGlass
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (position == FabPosition.RIGHT) Icons.Default.PanTool else Icons.Default.BackHand,
                    contentDescription = position.label,
                    tint = if (isSelected) DesignTokens.CobaltAccent else DesignTokens.TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = position.label,
                        color = DesignTokens.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = position.description,
                        color = DesignTokens.TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = DesignTokens.CobaltAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
