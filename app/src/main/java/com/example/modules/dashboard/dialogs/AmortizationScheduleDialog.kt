package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.modules.dashboard.logic.AmortizationScheduleRow
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AmortizationScheduleDialog(
    schedule: List<AmortizationScheduleRow>,
    onDismiss: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply { maximumFractionDigits = 0 }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(DesignTokens.CornerRadius)),
            colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Tabel Jadwal Amortisasi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                Text("Rincian Alokasi Pokok vs Bunga per Bulan", fontSize = 11.sp, color = DesignTokens.TextMuted)

                HorizontalDivider(color = DesignTokens.BorderLight)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DesignTokens.BackgroundBottom, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Bln", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextSecondary, modifier = Modifier.width(32.dp))
                    Text("Pokok", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldGlow, modifier = Modifier.weight(1f))
                    Text("Bunga", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.CrimsonAccent, modifier = Modifier.weight(1f))
                    Text("Sisa Pokok", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, modifier = Modifier.weight(1.2f))
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(schedule) { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${row.monthIndex}", fontSize = 11.sp, color = DesignTokens.TextMuted, modifier = Modifier.width(32.dp))
                            Text(formatter.format(row.principalPaid), fontSize = 11.sp, color = DesignTokens.EmeraldGlow, modifier = Modifier.weight(1f))
                            Text(formatter.format(row.interestPaid), fontSize = 11.sp, color = DesignTokens.CrimsonAccent, modifier = Modifier.weight(1f))
                            Text(formatter.format(row.endingBalance), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextPrimary, modifier = Modifier.weight(1.2f))
                        }
                    }
                }

                HorizontalDivider(color = DesignTokens.BorderLight)

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
                ) {
                    Text("TUTUP", fontWeight = FontWeight.Bold, color = DesignTokens.TextOnGradient)
                }
            }
        }
    }
}
