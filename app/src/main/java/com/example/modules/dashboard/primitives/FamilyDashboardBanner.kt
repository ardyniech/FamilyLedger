package com.example.modules.dashboard.primitives

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun FamilyDashboardBanner(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.CobaltAccent.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.People, contentDescription = "Family", tint = DesignTokens.CobaltAccent, modifier = Modifier.size(24.dp))
                Column {
                    Text("Family Dashboard (Monitoring Anggota)", color = DesignTokens.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Lihat ringkasan aset & pengeluaran seluruh keluarga", color = DesignTokens.TextSecondary, fontSize = 11.sp)
                }
            }
            Text("Lihat >", color = DesignTokens.CobaltAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
