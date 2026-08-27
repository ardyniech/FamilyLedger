package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun FamilySyncHeader(
    currentMemberName: String,
    currentRole: String,
    isPaired: Boolean,
    onSwitchProfile: () -> Unit,
    onPairClick: () -> Unit
) {
    val roleColor = if (currentRole == "Husband") DesignTokens.CobaltAccent else DesignTokens.AmberAccent
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignTokens.PaddingMedium, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(roleColor.copy(alpha = 0.15f))
                        .clickable { onSwitchProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentMemberName.take(1),
                        color = roleColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = currentMemberName,
                            fontWeight = FontWeight.Bold,
                            color = DesignTokens.TextPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "($currentRole)",
                            style = MaterialTheme.typography.bodySmall,
                            color = roleColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = if (isPaired) "🔗 Terhubung ke Pasangan" else "⚡ Mode Mandiri (Offline)",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPaired) DesignTokens.EmeraldGlow else DesignTokens.TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isPaired) DesignTokens.EmeraldGlow.copy(alpha = 0.12f) else DesignTokens.CobaltAccent.copy(alpha = 0.12f))
                    .clickable { onPairClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isPaired) "Sinkron 🔄" else "Hubungkan 📲",
                    color = if (isPaired) DesignTokens.EmeraldGlow else DesignTokens.CobaltAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
