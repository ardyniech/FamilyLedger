package com.example.modules.dashboard.csv.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun CsvImportControlBar(
    importableCount: Int,
    totalRecords: Int,
    clearDatabaseFirst: Boolean,
    onExecuteImport: () -> Unit
) {
    Button(
        onClick = onExecuteImport,
        enabled = importableCount > 0 || clearDatabaseFirst,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.EmeraldGlow)
    ) {
        Text(
            if (clearDatabaseFirst) "🚨 BERSIHKAN & IMPOR $totalRecords TRANSAKSI" else "📥 IMPOR $importableCount TRANSAKSI SEKARANG",
            color = DesignTokens.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
        )
    }
}
