package com.example.modules.dashboard.primitives

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun CategoryGroupBanner(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🏷️", fontSize = 22.sp)
                Column {
                    Text("Grup Kategori & Tag", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 14.sp)
                    Text("Lihat pie chart & analitik berdasarkan grup pos anggaran", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Open Groups", tint = DesignTokens.TextSecondary)
        }
    }
}
