package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.DashboardPeriod
import com.example.modules.dashboard.logic.PeriodSummary
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

import com.example.shared.atoms.springClickable

@Composable
fun PeriodOverviewCard(
    summary: PeriodSummary,
    onClick: () -> Unit
) {
    val currencyFmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val progress = if (summary.adjustedBudget > 0) {
        (summary.totalOutflow / summary.adjustedBudget).toFloat().coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .springClickable { onClick() },
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
    ) {
        Column(
            modifier = Modifier.padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ringkasan ${summary.period.displayName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DesignTokens.TextPrimary
                    )
                    Text(
                        text = "${summary.transactionCount} Transaksi Tercatat",
                        fontSize = 11.sp,
                        color = DesignTokens.TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Detail Laporan",
                    tint = DesignTokens.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Pengeluaran", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text(
                        text = currencyFmt.format(summary.totalOutflow),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFFFF5252)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Pemasukan", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text(
                        text = "+${currencyFmt.format(summary.totalInflow)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DesignTokens.EmeraldGlow
                    )
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (progress > 0.9f) Color(0xFFFF5252) else DesignTokens.AmberAccent,
                trackColor = DesignTokens.SurfaceGlass
            )
        }
    }
}
