package com.example.modules.dashboard.csv.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun CsvHeaderActionBar(
    onBack: () -> Unit,
    onReset: () -> Unit,
    canReset: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = DesignTokens.TextPrimary)
            }
            Column {
                Text("Smart CSV Importer", color = DesignTokens.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Deteksi format multi-aplikasi cerdas", color = DesignTokens.TextSecondary, fontSize = 11.sp)
            }
        }

        if (canReset) {
            IconButton(onClick = onReset) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset Form", tint = DesignTokens.TextSecondary)
            }
        }
    }
}
