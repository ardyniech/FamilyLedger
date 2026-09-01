package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.atoms.springClickable
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FinancialRunwayCard(
    totalBalance: Long,
    wallets: List<WalletAccount>,
    transactions: List<Transaction>,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currencyFmt = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    
    val monthlyBurnRate = remember(transactions) {
        val expenseTx = transactions.filter { it.amount < 0 }
        if (expenseTx.isEmpty()) 5_000_000L
        else {
            val totalExpense = expenseTx.sumOf { -it.amount }
            (totalExpense / 3L).coerceAtLeast(1_000_000L)
        }
    }

    val runwayMonths = if (monthlyBurnRate > 0) (totalBalance.toDouble() / monthlyBurnRate).coerceAtLeast(0.0) else 0.0
    val runwayFraction = (runwayMonths / 6.0).coerceIn(0.0, 1.0).toFloat()

    val (statusLabel, statusColor, badgeEmoji) = when {
        runwayMonths >= 6.0 -> Triple("Keluarga Tangguh", DesignTokens.EmeraldGlow, "🛡️")
        runwayMonths >= 3.0 -> Triple("Dana Aman Standar", DesignTokens.CobaltAccent, "⚡")
        runwayMonths >= 1.0 -> Triple("Perlu Ditambah", DesignTokens.AmberAccent, "⚠️")
        else -> Triple("Kritis / Zona Bahaya", DesignTokens.CrimsonAccent, "🚨")
    }

    Card(
        modifier = modifier.fillMaxWidth().springClickable { onClick?.invoke() },
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.Shield, contentDescription = "Runway", tint = statusColor, modifier = Modifier.size(16.dp))
                    Text("FINANCIAL RUNWAY DANA DARURAT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextSecondary, letterSpacing = 0.5.sp)
                }
                Surface(shape = RoundedCornerShape(6.dp), color = statusColor.copy(alpha = 0.15f)) {
                    Text("$badgeEmoji $statusLabel", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column {
                    Text(text = String.format(Locale.US, "%.1f Bulan", runwayMonths), fontSize = 22.sp, fontWeight = FontWeight.Black, color = DesignTokens.TextPrimary)
                    Text(text = "Ketahanan hidup jika tanpa pemasukan", fontSize = 11.sp, color = DesignTokens.TextMuted)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Est. Pengeluaran/Bln", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text(text = currencyFmt.format(monthlyBurnRate), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextPrimary)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { runwayFraction },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = statusColor,
                    trackColor = DesignTokens.BorderLight
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("0 Bln", fontSize = 9.sp, color = DesignTokens.TextMuted)
                    Text("Target Ideal: 6 Bulan", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = DesignTokens.TextSecondary)
                    Text("6+ Bln", fontSize = 9.sp, color = DesignTokens.TextMuted)
                }
            }
        }
    }
}
