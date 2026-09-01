package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.DashboardPeriod
import com.example.modules.dashboard.logic.PeriodFilterHelper
import com.example.shared.atoms.springClickable
import com.example.shared.theme.DesignTokens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivePeriodStatusBanner(
    selectedPeriod: DashboardPeriod,
    transactionCount: Int,
    onPeriodClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFmt = remember { SimpleDateFormat("d MMM yyyy", Locale("id", "ID")) }
    val monthFmt = remember { SimpleDateFormat("MMMM yyyy", Locale("id", "ID")) }
    val now = remember { System.currentTimeMillis() }
    val (startMs, endMs) = remember(selectedPeriod, now) { PeriodFilterHelper.getPeriodDateRange(selectedPeriod, now) }
    
    val currentMonthName = remember(now) { monthFmt.format(Date(now)) }
    val startDateStr = remember(startMs) { dateFmt.format(Date(startMs)) }
    val endDateStr = remember(endMs) { dateFmt.format(Date(endMs)) }

    Card(
        modifier = modifier.fillMaxWidth().springClickable { onPeriodClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.CobaltAccent.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, DesignTokens.CobaltAccent.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(8.dp), color = DesignTokens.CobaltAccent, modifier = Modifier.size(32.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Period", tint = DesignTokens.TextPrimary, modifier = Modifier.size(18.dp))
                    }
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("PERIODE: ${currentMonthName.uppercase()}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DesignTokens.CobaltAccent, letterSpacing = 0.5.sp)
                        Surface(shape = RoundedCornerShape(4.dp), color = DesignTokens.EmeraldGlow.copy(alpha = 0.2f)) {
                            Text(selectedPeriod.displayName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldGlow, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                        }
                    }
                    Text("$startDateStr - $endDateStr ($transactionCount Transaksi)", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = DesignTokens.TextSecondary)
                }
            }
            Surface(shape = RoundedCornerShape(6.dp), color = DesignTokens.SurfaceGlass, border = BorderStroke(0.5.dp, DesignTokens.BorderGlass)) {
                Text("Ganti ▾", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}
