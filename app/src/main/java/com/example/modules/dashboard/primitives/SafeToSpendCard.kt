package com.example.modules.dashboard.primitives

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.BurnStatus
import com.example.shared.models.SafeToSpendReport
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SafeToSpendCard(
    report: SafeToSpendReport,
    onClickDetails: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply { maximumFractionDigits = 0 }
    val progressAnim by animateFloatAsState(targetValue = report.dailyBurnRatio.coerceIn(0f, 1f), label = "burn_progress")

    val statusColor = when (report.burnStatus) {
        BurnStatus.SAFE -> DesignTokens.EmeraldGlow
        BurnStatus.MODERATE -> DesignTokens.AmberAccent
        BurnStatus.CRITICAL -> DesignTokens.CrimsonAccent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignTokens.CornerRadius))
            .border(1.dp, DesignTokens.BorderLight, RoundedCornerShape(DesignTokens.CornerRadius))
            .clickable { onClickDetails() },
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text("Batas Belanja Aman Hari Ini", fontSize = 12.sp, color = DesignTokens.TextSecondary, fontWeight = FontWeight.SemiBold)
                        Text(report.burnStatus.label, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Bold)
                    }
                }
                IconButton(onClick = onClickDetails, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Info, contentDescription = "Rincian", tint = DesignTokens.CobaltAccent, modifier = Modifier.size(18.dp))
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = formatter.format(report.remainingSafeToday),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (report.burnStatus == BurnStatus.CRITICAL) DesignTokens.CrimsonAccent else DesignTokens.TextPrimary
                )
                Text("Tersisa dari batas harian ${formatter.format(report.dailySafeToSpend)}", fontSize = 11.sp, color = DesignTokens.TextMuted)
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { progressAnim },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = statusColor,
                    trackColor = DesignTokens.BorderLight
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Terpakai: ${formatter.format(report.spentToday)}", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text("Sisa ${report.daysRemainingInMonth} hari bulan ini", fontSize = 10.sp, color = DesignTokens.TextMuted)
                }
            }
        }
    }
}
