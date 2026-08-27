package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun MonthlyAdviceCard(progress: Float) {
    val statusText = when {
        progress < 0.4f -> "Sangat hemat! Pengeluaran masih di bawah 40% anggaran. Ruang tabungan keluarga sangat aman."
        progress < 0.8f -> "Konsumsi wajar dan terkendali. Tetap pertahankan kedisiplinan pencatatan harian bersama."
        else -> "Perhatian: Pengeluaran telah menyentuh lebih dari 80% pagu anggaran. Disarankan menunda pos belanja sekunder."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
    ) {
        Row(
            modifier = Modifier.padding(DesignTokens.PaddingLarge),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(text = "💡", fontSize = 24.sp)
            Column {
                Text("Evaluasi Harmoni Finansial", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(statusText, color = DesignTokens.TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
