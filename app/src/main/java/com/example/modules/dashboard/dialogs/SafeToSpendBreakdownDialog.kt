package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.shared.models.SafeToSpendReport
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SafeToSpendBreakdownDialog(
    report: SafeToSpendReport,
    onDismiss: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply { maximumFractionDigits = 0 }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(DesignTokens.CornerRadius)),
            colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(DesignTokens.CobaltAccent.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = DesignTokens.CobaltAccent, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text("Kalkulasi Safe-to-Spend", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                        Text("Transparansi Cadangan Belanja Harian", fontSize = 11.sp, color = DesignTokens.TextMuted)
                    }
                }

                HorizontalDivider(color = DesignTokens.BorderLight)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BreakdownRow("Sisa Saldo Bebas Bulan Ini", formatter.format(report.monthlySafeToSpendRemaining), DesignTokens.TextPrimary, isBold = true)
                    BreakdownRow("Cadangan Tagihan Berjalan", "- ${formatter.format(report.committedBillsSum)}", DesignTokens.CrimsonAccent)
                    BreakdownRow("Cadangan Cicilan/KPR Bank", "- ${formatter.format(report.committedLoansSum)}", DesignTokens.AmberAccent)
                    BreakdownRow("Reservasi Target Tabungan", "- ${formatter.format(report.goalsReservationSum)}", DesignTokens.CobaltAccent)
                    BreakdownRow("Jumlah Hari Tersisa", "${report.daysRemainingInMonth} Hari", DesignTokens.TextSecondary)
                }

                HorizontalDivider(color = DesignTokens.BorderLight)

                Surface(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                    color = DesignTokens.EmeraldGlow.copy(alpha = 0.1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Saran Pengelolaan Hari Ini:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldGlow)
                        Text(report.actionAdvice, fontSize = 11.sp, color = DesignTokens.TextPrimary)
                    }
                }

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

@Composable
private fun BreakdownRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 12.sp, color = DesignTokens.TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium, color = valueColor)
    }
}
