package com.example.modules.dashboard.csv.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.csv.CsvParseResult
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CsvPreviewCard(result: CsvParseResult) {
    val currencyFmt = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("📊 Hasil Analisis Cerdas", color = DesignTokens.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .background(DesignTokens.CobaltAccent.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(result.detectedFormatName, color = DesignTokens.CobaltAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Entri: ${result.records.size}", color = DesignTokens.TextSecondary, fontSize = 12.sp)
                    Text("Baru: ${result.newCount}", color = DesignTokens.EmeraldGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                if (result.duplicateCount > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Duplikat Terdeteksi", color = DesignTokens.AmberAccent, fontSize = 12.sp)
                        Text("${result.duplicateCount} entri", color = DesignTokens.AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DesignTokens.Surface, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Pemasukan", color = DesignTokens.TextMuted, fontSize = 10.sp)
                    Text(currencyFmt.format(result.totalIncome), color = DesignTokens.EmeraldGlow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Pengeluaran", color = DesignTokens.TextMuted, fontSize = 10.sp)
                    Text(currencyFmt.format(result.totalExpense), color = Color(0xFFFF5252), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
