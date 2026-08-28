package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

import com.example.shared.atoms.springClickable

@Composable
fun TransparencyHealthCard(
    totalIncome: Double,
    totalExpense: Double,
    transactionCount: Int,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val netSavings = totalIncome - totalExpense
    val savingsRate = if (totalIncome > 0) ((netSavings / totalIncome) * 100).toInt().coerceIn(0, 100) else 0

    val score = when {
        transactionCount == 0 -> 100
        totalIncome >= totalExpense && savingsRate >= 20 -> 98
        totalIncome >= totalExpense -> 88
        else -> 65
    }

    val statusText = when {
        score >= 90 -> "Transparansi Maksimal"
        score >= 80 -> "Keuangan Sehat"
        else -> "Perlu Perhatian"
    }

    Card(
        modifier = modifier.fillMaxWidth().then(if (onClick != null) Modifier.springClickable { onClick() } else Modifier),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
    ) {
        Column(
            modifier = Modifier.padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DesignTokens.EmeraldGlow.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = DesignTokens.EmeraldGlow, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text("Indeks Transparansi Keluarga", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DesignTokens.TextPrimary)
                        Text(statusText, fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DesignTokens.EmeraldGlow.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("$score/100", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = DesignTokens.EmeraldGlow)
                }
            }

            HorizontalDivider(color = DesignTokens.BorderGlass, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Tingkat Tabungan", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text("$savingsRate%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DesignTokens.TextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Jumlah Catatan", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text("$transactionCount Transaksi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DesignTokens.TextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Net Disimpan", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text(
                        text = fmt.format(netSavings.coerceAtLeast(0.0)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (netSavings >= 0) DesignTokens.EmeraldGlow else Color(0xFFFF5252)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DesignTokens.CobaltAccent, modifier = Modifier.size(14.dp))
                Text("Semua mutasi tersinkronisasi antar pasangan", fontSize = 10.sp, color = DesignTokens.TextSecondary)
            }
        }
    }
}
