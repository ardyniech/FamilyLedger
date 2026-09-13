package com.example.modules.dashboard.primitives

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.NafkahAllocationReport
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleTheme
import com.example.shared.utils.MathUtils

@Composable
fun WifeNafkahAllocationCard(
    report: NafkahAllocationReport,
    onRecordHouseholdExpense: () -> Unit,
    onTransferNafkah: () -> Unit
) {
    val palette = RoleTheme.current
    val isWife = report.isWifeRole

    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated),
        border = BorderStroke(1.5.dp, palette.cardBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NafkahCardHeader(report = report, palette = palette)
            NafkahCardMetricsRow(report = report, palette = palette)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Terpakai Kebutuhan Rumah: ${MathUtils.formatRupiah(report.totalHouseholdExpensesSpent)}", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text("${(report.spentPercentage * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.primaryAccent)
                }
                LinearProgressIndicator(
                    progress = { report.spentPercentage },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = if (report.remainingBudget >= 0) palette.primaryAccent else DesignTokens.RoseAccent,
                    trackColor = palette.primarySoft
                )
            }

            Surface(shape = RoundedCornerShape(12.dp), color = palette.primarySoft.copy(alpha = 0.6f), border = BorderStroke(1.dp, palette.cardBorder.copy(alpha = 0.4f))) {
                Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, contentDescription = "Tip", tint = palette.primaryAccent, modifier = Modifier.size(16.dp))
                    Text(report.feedbackNote, fontSize = 11.sp, color = DesignTokens.TextPrimary, lineHeight = 15.sp)
                }
            }

            Button(
                onClick = if (isWife) onRecordHouseholdExpense else onTransferNafkah,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(if (isWife) Icons.Default.AddShoppingCart else Icons.AutoMirrored.Filled.Send, contentDescription = "Action", tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (isWife) "Catat Pengeluaran Kebutuhan Rumah / Dapur" else "Transfer Uang Belanja / Nafkah ke Istri",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
