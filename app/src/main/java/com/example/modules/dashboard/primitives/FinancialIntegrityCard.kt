package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.FinancialIntegrityReport
import com.example.shared.models.CashflowCadence
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun FinancialIntegrityCard(
    report: FinancialIntegrityReport,
    selectedCadence: CashflowCadence,
    onSelectCadence: (CashflowCadence) -> Unit,
    onOpenLoans: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inc = report.requiredIncomeData
    val statusBg = when (inc.urgencyLevel) {
        2 -> DesignTokens.RoseAccent.copy(alpha = 0.15f)
        1 -> DesignTokens.AmberAccent.copy(alpha = 0.15f)
        else -> DesignTokens.EmeraldGlow.copy(alpha = 0.12f)
    }
    val statusColor = when (inc.urgencyLevel) {
        2 -> DesignTokens.RoseAccent
        1 -> DesignTokens.AmberAccent
        else -> DesignTokens.EmeraldGlow
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Integritas Keuangan & Cicilan", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = DesignTokens.TextPrimary)
                    Text("Analisis tagihan berdekatan & target income", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(statusBg).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(if (inc.urgencyLevel == 2) "KRITIS" else if (inc.urgencyLevel == 1) "PERSIAPAN" else "SEHAT", color = statusColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }

            if (report.activeCluster != null) {
                val cl = report.activeCluster
                Surface(shape = RoundedCornerShape(10.dp), color = DesignTokens.RoseAccent.copy(alpha = 0.08f), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Warning, contentDescription = "Cluster", tint = DesignTokens.RoseAccent, modifier = Modifier.size(18.dp))
                        Column {
                            Text("Tagihan Berdekatan Terdeteksi!", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.RoseAccent)
                            Text("Tgl ${cl.startDay} s/d ${cl.endDay} (${cl.items.size} kewajiban) - Total ${MathUtils.formatRupiah(cl.totalAmount)}", fontSize = 11.sp, color = DesignTokens.TextPrimary)
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DesignTokens.BackgroundBottom).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                CashflowCadence.entries.forEach { cadence ->
                    val isSel = cadence == selectedCadence
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp)).background(if (isSel) DesignTokens.CobaltAccent else Color.Transparent).clickable { onSelectCadence(cadence) }.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                        Text(cadence.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else DesignTokens.TextSecondary)
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(DesignTokens.BackgroundBottom.copy(alpha = 0.7f)).padding(12.dp)) {
                Text("Target Penghasilan Yang Harus Dicapai (${selectedCadence.label}):", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                Text("${MathUtils.formatRupiah(inc.requiredAmount)} ${inc.unitLabel}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = DesignTokens.TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(inc.statusMessage, fontSize = 11.sp, color = DesignTokens.TextSecondary, lineHeight = 15.sp)
            }

            report.recommendations.firstOrNull()?.let { tip ->
                Text("💡 $tip", fontSize = 11.sp, color = DesignTokens.EmeraldGlow, lineHeight = 15.sp)
            }

            TextButton(onClick = onOpenLoans, modifier = Modifier.align(Alignment.End), contentPadding = PaddingValues(0.dp)) {
                Text("Kelola KPR & Kredit Bank →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
            }
        }
    }
}
