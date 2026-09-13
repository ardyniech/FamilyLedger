package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.NafkahAllocationReport
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette
import com.example.shared.utils.MathUtils

@Composable
fun WifeBudgetGuideCard(
    report: NafkahAllocationReport,
    onInitTemplates: () -> Unit
) {
    val palette = RoleThemePalette.istri()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated),
        border = BorderStroke(1.dp, palette.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("🌸 Panduan Uang Belanja Istri", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    Text("Kelola nafkah & pos dapur rapi & transparan", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = palette.primarySoft
                ) {
                    Text("Mode Istri", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.primaryAccent, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            // Metrics row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Nafkah Diterima", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text(MathUtils.formatRupiah(report.totalReceivedFromHusband), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Sudah Dibelanjakan", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text(MathUtils.formatRupiah(report.totalHouseholdExpensesSpent), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.secondaryAccent)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Sisa Kas Belanja", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text(MathUtils.formatRupiah(report.remainingBudget), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (report.remainingBudget >= 0) DesignTokens.EmeraldAccent else DesignTokens.RoseAccent)
                }
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { report.spentPercentage },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (report.spentPercentage > 0.9f) DesignTokens.RoseAccent else palette.primaryAccent,
                trackColor = palette.primarySoft
            )

            // Explanatory note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.backgroundTint)
                    .padding(10.dp)
            ) {
                Text(
                    text = "💡 Tips Bunda: Pisahkan uang belanja ke Kas Dapur (pasar/sayur), BCA (sembako/bulanan), dan ShopeePay (belanja praktis). Catat langsung setelah belanja agar sisa uang selalu akurat!",
                    fontSize = 11.sp,
                    color = DesignTokens.TextPrimary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
