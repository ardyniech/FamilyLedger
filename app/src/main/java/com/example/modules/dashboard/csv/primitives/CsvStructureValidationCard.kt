package com.example.modules.dashboard.csv.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.csv.CsvParseResult
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun CsvStructureValidationCard(
    result: CsvParseResult,
    skipDuplicates: Boolean,
    onToggleSkipDuplicates: (Boolean) -> Unit
) {
    val isValid = result.records.isNotEmpty()
    val statusColor = if (isValid) DesignTokens.EmeraldGlow else DesignTokens.AmberAccent

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = if (isValid) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                        contentDescription = "Status",
                        tint = statusColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isValid) "Struktur Valid (${result.records.size} Baris)" else "Format Perlu Diperiksa",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DesignTokens.TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = result.detectedFormatName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            HorizontalDivider(color = DesignTokens.BorderGlass)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Pemasukan", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text(MathUtils.formatRupiah(result.totalIncome), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldGlow)
                }
                Column {
                    Text("Total Pengeluaran", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text(MathUtils.formatRupiah(result.totalExpense), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextSecondary)
                }
                Column {
                    Text("Duplikat Ditemukan", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text("${result.duplicateCount} Baris", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (result.duplicateCount > 0) DesignTokens.AmberAccent else DesignTokens.TextPrimary)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().background(DesignTokens.SurfaceElevated, RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lewati Transaksi Duplikat", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextPrimary)
                Switch(
                    checked = skipDuplicates,
                    onCheckedChange = onToggleSkipDuplicates,
                    colors = SwitchDefaults.colors(checkedThumbColor = DesignTokens.EmeraldGlow)
                )
            }
        }
    }
}
