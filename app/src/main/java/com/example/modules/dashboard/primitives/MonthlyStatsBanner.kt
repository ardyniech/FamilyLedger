package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MonthlyStatsBanner(
    totalIncome: Double,
    totalExpense: Double,
    netBalance: Double,
    modifier: Modifier = Modifier
) {
    val currencyFmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.PaddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Pemasukan", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                Text(
                    text = "+${currencyFmt.format(totalIncome)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DesignTokens.EmeraldGlow
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(DesignTokens.BorderGlass)
            )

            Column {
                Text("Pengeluaran", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                Text(
                    text = "-${currencyFmt.format(totalExpense)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5252)
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(DesignTokens.BorderGlass)
            )

            Column(horizontalAlignment = Alignment.End) {
                Text("Selisih Bersih", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                Text(
                    text = currencyFmt.format(netBalance),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (netBalance >= 0) DesignTokens.EmeraldGlow else Color(0xFFFF5252)
                )
            }
        }
    }
}
