package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max

@Composable
fun DailyBurnRateCard(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val currencyFmt = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    
    val burnData = remember(transactions) {
        val expenses = transactions.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }
        val cal = Calendar.getInstance()
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        val earliestTx = transactions.minOfOrNull { it.timestamp } ?: System.currentTimeMillis()
        val latestTx = transactions.maxOfOrNull { it.timestamp } ?: System.currentTimeMillis()
        val daysSpan = max(1L, (latestTx - earliestTx) / (1000 * 60 * 60 * 24) + 1)
        val activeDays = if (daysSpan > 31) 30L else daysSpan
        
        val dailyAvg = if (activeDays > 0) expenses / activeDays else 0L
        val projected = dailyAvg * daysInMonth
        
        Triple(dailyAvg, projected, expenses)
    }
    
    val (dailyAvg, projected, totalExpense) = burnData
    if (totalExpense == 0L) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.LocalFireDepartment, contentDescription = "Burn Rate", tint = DesignTokens.AmberAccent, modifier = Modifier.size(16.dp))
                    Text("DAILY BURN RATE (RATA-RATA HARIAN)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextSecondary, letterSpacing = 0.5.sp)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column {
                    Text(text = currencyFmt.format(dailyAvg), fontSize = 22.sp, fontWeight = FontWeight.Black, color = DesignTokens.TextPrimary)
                    Text(text = "Rata-rata pengeluaran per hari", fontSize = 11.sp, color = DesignTokens.TextMuted)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Proyeksi Akhir Bulan", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text(text = currencyFmt.format(projected), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DesignTokens.AmberAccent)
                }
            }
        }
    }
}
