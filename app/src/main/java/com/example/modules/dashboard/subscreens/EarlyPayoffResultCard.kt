package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.EarlyPayoffResult
import com.example.modules.dashboard.logic.PayoffStrategy
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun EarlyPayoffResultCard(
    result: EarlyPayoffResult,
    strategy: PayoffStrategy,
    onViewSchedule: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated),
        border = BorderStroke(1.5.dp, DesignTokens.EmeraldAccent.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Savings, contentDescription = "Savings", tint = DesignTokens.EmeraldAccent, modifier = Modifier.size(20.dp))
                    Text("Hasil Efisiensi Pelunasan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                }
                Surface(shape = RoundedCornerShape(10.dp), color = DesignTokens.EmeraldAccent.copy(alpha = 0.15f)) {
                    Text(
                        "Hemat ${(result.percentInterestSaved * 100).toInt()}% Bunga",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DesignTokens.EmeraldAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Surface(shape = RoundedCornerShape(12.dp), color = DesignTokens.SurfaceGlass) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Total Hemat Beban Bunga", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                        Text(MathUtils.formatRupiah(result.interestSaved), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = DesignTokens.EmeraldAccent)
                    }
                    if (strategy == PayoffStrategy.REDUCE_TENOR) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Waktu Lunas Lebih Cepat", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                            val yrs = result.monthsSaved / 12
                            val mos = result.monthsSaved % 12
                            val timeStr = if (yrs > 0) "$yrs thn $mos bln" else "$mos bln"
                            Text(timeStr, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.AmberAccent)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Cicilan Berkurang", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                            Text("${MathUtils.formatRupiah(result.monthlyPaymentSaved)}/bln", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tenor Baru: ${result.newTenorMonths} bln (dari ${result.originalTenorMonths} bln)", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text("Bunga Baru: ${MathUtils.formatRupiah(result.newTotalInterest)}", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                }
                LinearProgressIndicator(
                    progress = { if (result.originalTotalInterest > 0) (result.newTotalInterest.toFloat() / result.originalTotalInterest.toFloat()).coerceIn(0f, 1f) else 0f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = DesignTokens.AmberAccent,
                    trackColor = DesignTokens.EmeraldAccent
                )
            }

            Surface(shape = RoundedCornerShape(10.dp), color = DesignTokens.CobaltAccent.copy(alpha = 0.1f)) {
                Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = "Summary", tint = DesignTokens.CobaltAccent, modifier = Modifier.size(16.dp))
                    Text(result.estimatedPayoffSummary, fontSize = 11.sp, color = DesignTokens.TextPrimary, lineHeight = 15.sp)
                }
            }

            if (onViewSchedule != null) {
                OutlinedButton(
                    onClick = onViewSchedule,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Lihat Rincian Jadwal Pokok vs Bunga", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.CobaltAccent)
                }
            }
        }
    }
}
