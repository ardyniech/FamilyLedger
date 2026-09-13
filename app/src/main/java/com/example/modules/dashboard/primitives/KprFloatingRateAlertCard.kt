package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.KprFloatingRateAnalysis
import com.example.modules.dashboard.logic.KprFloatingSeverity
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun KprFloatingRateAlertCard(
    analysis: KprFloatingRateAnalysis,
    onOpenSimulator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
    val badgeColor = when (analysis.severityLevel) {
        KprFloatingSeverity.SHOCK_IMMINENT -> DesignTokens.RoseAccent
        KprFloatingSeverity.UPCOMING -> DesignTokens.AmberAccent
        KprFloatingSeverity.SAFE -> DesignTokens.EmeraldGlow
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.WarningAmber, contentDescription = "Alert", tint = badgeColor, modifier = Modifier.size(18.dp))
                    Text("Radar Suku Bunga KPR Floating", color = DesignTokens.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(badgeColor.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text(analysis.severityLevel.label, color = badgeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(DesignTokens.BackgroundBottom.copy(alpha = 0.5f)).padding(8.dp)) {
                    Text("Masa Fixed (${analysis.fixedInterestRate}%)", color = DesignTokens.TextSecondary, fontSize = 10.sp)
                    Text("Rp ${formatter.format(analysis.fixedMonthlyInstallment)}", color = DesignTokens.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(DesignTokens.BackgroundBottom.copy(alpha = 0.5f)).padding(8.dp)) {
                    Text("Estimasi Floating (${analysis.floatingInterestRate}%)", color = DesignTokens.RoseAccent, fontSize = 10.sp)
                    Text("Rp ${formatter.format(analysis.floatingMonthlyInstallment)}", color = DesignTokens.RoseAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Potensi Lonjakan Cicilan:", color = DesignTokens.TextSecondary, fontSize = 11.sp)
                Text("+Rp ${formatter.format(analysis.monthlyDifference)}/bln (+${String.format(Locale.US, "%.1f", analysis.percentageSpike)}%)", color = DesignTokens.AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Text(analysis.recommendationText, color = DesignTokens.TextSecondary, fontSize = 10.sp, lineHeight = 15.sp)

            OutlinedButton(
                onClick = onOpenSimulator,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DesignTokens.CobaltAccent),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                Icon(Icons.Default.Calculate, contentDescription = "Simulate", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simulasikan Pelunasan Cepat / Amortisasi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
