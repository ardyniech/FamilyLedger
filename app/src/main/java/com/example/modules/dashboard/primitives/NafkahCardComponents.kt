package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.NafkahAllocationReport
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette
import com.example.shared.utils.MathUtils

@Composable
fun NafkahCardHeader(report: NafkahAllocationReport, palette: RoleThemePalette) {
    val isWife = report.isWifeRole
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(if (isWife) "🌸" else "🛡️", fontSize = 20.sp)
            Column {
                Text(
                    if (isWife) "Kelola Uang Belanja dari Suami" else "Alokasi Nafkah Rumah Tangga",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DesignTokens.TextPrimary
                )
                Text(
                    if (isWife) "Transfer ${report.husbandName} ➔ Pos Dapur & Rumah" else "Penyaluran ke ${report.wifeName} & Tanggungan",
                    fontSize = 10.sp,
                    color = DesignTokens.TextSecondary
                )
            }
        }
        Surface(shape = RoundedCornerShape(12.dp), color = palette.primarySoft) {
            Text(
                if (isWife) "Mode Istri" else "Mode Suami",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = palette.primaryAccent,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun NafkahCardMetricsRow(report: NafkahAllocationReport, palette: RoleThemePalette) {
    val isWife = report.isWifeRole
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            color = palette.primaryContainer,
            border = BorderStroke(1.dp, palette.cardBorder.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(if (isWife) "Nafkah Diterima" else "Nafkah Disalurkan", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                Text(MathUtils.formatRupiah(report.totalReceivedFromHusband), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.primaryAccent)
            }
        }
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            color = if (report.remainingBudget >= 0) DesignTokens.SurfaceGlass else DesignTokens.RoseAccent.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, if (report.remainingBudget >= 0) DesignTokens.BorderGlass else DesignTokens.RoseAccent)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(if (isWife) "Sisa Uang Dapur" else "Sisa Pos Belanja", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                Text(
                    MathUtils.formatRupiah(report.remainingBudget),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (report.remainingBudget >= 0) DesignTokens.EmeraldAccent else DesignTokens.RoseAccent
                )
            }
        }
    }
}
