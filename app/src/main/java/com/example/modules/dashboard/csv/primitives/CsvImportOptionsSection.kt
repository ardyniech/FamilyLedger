package com.example.modules.dashboard.csv.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun CsvImportOptionsSection(
    recordCount: Int,
    clearDatabaseFirst: Boolean,
    onClearDatabaseFirstChange: (Boolean) -> Unit,
    skipDuplicates: Boolean,
    onSkipDuplicatesChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Hapus Database Sebelum Impor", color = DesignTokens.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Bersihkan semua transaksi/akun sebelum mengimpor", color = DesignTokens.AmberAccent, fontSize = 11.sp)
            }
            Switch(
                checked = clearDatabaseFirst,
                onCheckedChange = onClearDatabaseFirstChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = DesignTokens.AmberAccent,
                    checkedTrackColor = DesignTokens.AmberAccent.copy(alpha = 0.4f)
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Daftar Transaksi ($recordCount)", color = DesignTokens.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = skipDuplicates && !clearDatabaseFirst,
                    enabled = !clearDatabaseFirst,
                    onCheckedChange = onSkipDuplicatesChange,
                    colors = CheckboxDefaults.colors(checkedColor = DesignTokens.EmeraldGlow)
                )
                Text("Skip Duplikat", color = DesignTokens.TextSecondary, fontSize = 11.sp)
            }
        }
    }
}
