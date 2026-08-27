package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.sync.SyncState
import com.example.shared.theme.DesignTokens

@Composable
fun SyncStatusCard(
    syncState: SyncState,
    onClick: (() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            when (syncState) {
                                SyncState.SYNCING -> DesignTokens.AmberAccent
                                SyncState.ERROR -> Color.Red
                                else -> DesignTokens.EmeraldGlow
                            }
                        )
                )
                Column {
                    Text(
                        text = when (syncState) {
                            SyncState.SYNCING -> "Sedang Menyinkronkan..."
                            SyncState.ERROR -> "Mode Offline (Data Tersimpan Aman)"
                            else -> "Tersinkronisasi & Terhubung"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DesignTokens.TextPrimary
                    )
                    Text(
                        text = "Arsitektur Offline-First Aktif",
                        fontSize = 11.sp,
                        color = DesignTokens.TextSecondary
                    )
                }
            }

            Icon(
                Icons.Default.Sync, 
                contentDescription = "Sync", 
                tint = DesignTokens.CobaltAccent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
