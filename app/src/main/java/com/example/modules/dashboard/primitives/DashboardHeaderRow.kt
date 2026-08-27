package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.sync.SyncState
import com.example.shared.models.Member
import com.example.shared.theme.DesignTokens

@Composable
fun DashboardHeaderRow(
    activeMember: Member?,
    syncState: SyncState,
    onSyncBadgeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Family Ledgers",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = DesignTokens.TextPrimary
            )
            Text(
                "Keluarga Harmonis 🕊️",
                fontSize = 12.sp,
                color = DesignTokens.TextSecondary
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DesignTokens.SurfaceGlass,
                border = BorderStroke(1.dp, DesignTokens.BorderGlass),
                modifier = Modifier.clickable { onSyncBadgeClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (syncState) {
                                    SyncState.SYNCING -> DesignTokens.AmberAccent
                                    SyncState.ERROR -> Color.Red
                                    else -> DesignTokens.EmeraldGlow
                                }
                            )
                    )
                    Text(
                        text = when (syncState) {
                            SyncState.SYNCING -> "Sinkron..."
                            SyncState.ERROR -> "Offline"
                            else -> "Tersinkron"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DesignTokens.TextPrimary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = (if (activeMember?.role == "Husband") DesignTokens.CobaltAccent else DesignTokens.AmberAccent).copy(alpha = 0.2f),
                border = BorderStroke(
                    1.dp, 
                    if (activeMember?.role == "Husband") DesignTokens.CobaltAccent else DesignTokens.AmberAccent
                ),
                modifier = Modifier.clickable { onProfileClick() }
            ) {
                Text(
                    text = "${if (activeMember?.role == "Husband") "👨" else "👩"} ${activeMember?.name ?: "User"}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DesignTokens.TextPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
