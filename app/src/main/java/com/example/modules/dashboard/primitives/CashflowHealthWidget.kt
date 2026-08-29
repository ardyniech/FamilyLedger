package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.CashflowHealthCalculator
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens

@Composable
fun CashflowHealthWidget(
    filteredTransactions: List<Transaction>,
    allTransactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val summary = remember(filteredTransactions, allTransactions) {
        CashflowHealthCalculator.calculate(filteredTransactions, allTransactions)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📊", fontSize = 18.sp)
                    Text("Cashflow Health", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 14.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (summary.isSurplus) DesignTokens.EmeraldAccent.copy(alpha = 0.18f) else DesignTokens.CrimsonAccent.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (summary.isSurplus) "SURPLUS" else "DEFISIT",
                        color = if (summary.isSurplus) DesignTokens.EmeraldAccent else DesignTokens.CrimsonAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Net Cashflow", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text(
                        text = (if (summary.netCashflow > 0L) "+" else "") + "Rp ${String.format("%,d", summary.netCashflow)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = if (summary.isSurplus) DesignTokens.EmeraldAccent else DesignTokens.CrimsonAccent
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Savings Margin", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text("${String.format("%.1f", summary.savingsRate)}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DesignTokens.TextPrimary)
                }
            }

            // 3-Month Mini Trend
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                summary.last3MonthsTrend.forEach { point ->
                    val isPositive = point.netCashflow >= 0
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DesignTokens.SurfaceGlass)
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(point.monthLabel, fontSize = 10.sp, color = DesignTokens.TextSecondary)
                            Text(
                                text = (if (point.netCashflow > 0) "+" else "") + String.format("%.1fjt", point.netCashflow / 1_000_000.0),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPositive) DesignTokens.EmeraldAccent else DesignTokens.CrimsonAccent
                            )
                        }
                    }
                }
            }

            Text(summary.comparisonMessage, fontSize = 11.sp, color = DesignTokens.TextSecondary)
        }
    }
}
