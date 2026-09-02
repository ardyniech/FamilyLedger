package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.DailyTransactionGroup
import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

@Composable
fun GroupedTransactionsSection(
    groups: List<DailyTransactionGroup>,
    members: List<Member>,
    categories: List<Category>,
    onTransactionClick: (Transaction) -> Unit,
    maxGroups: Int = 10,
    modifier: Modifier = Modifier
) {
    val currencyFmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    if (groups.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard),
            border = BorderStroke(1.dp, DesignTokens.BorderGlass)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "💸", fontSize = 28.sp)
                Text(
                    text = "Belum Ada Transaksi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DesignTokens.TextPrimary
                )
                Text(
                    text = "Catat pengeluaran atau pemasukan pertama Anda untuk melihat riwayat.",
                    fontSize = 12.sp,
                    color = DesignTokens.TextSecondary
                )
            }
        }
    } else {
        Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            groups.take(maxGroups).forEach { group ->
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = group.displayHeader,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DesignTokens.TextSecondary
                    )

                    if (group.dayTotalOutflow > 0 || group.dayTotalInflow > 0) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (group.dayTotalInflow > 0) {
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(DesignTokens.EmeraldGlow.copy(alpha = 0.12f)).padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("+${currencyFmt.format(group.dayTotalInflow)}", fontSize = 10.sp, color = DesignTokens.EmeraldGlow, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (group.dayTotalOutflow > 0) {
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFFF5252).copy(alpha = 0.12f)).padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("-${currencyFmt.format(group.dayTotalOutflow)}", fontSize = 10.sp, color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                group.transactions.forEach { tx ->
                    TransactionItem(
                        tx = tx,
                        member = members.find { it.id == tx.memberId },
                        category = categories.find { it.id == tx.categoryId },
                        onClick = { onTransactionClick(tx) }
                    )
                }
            }
        }
    }
}
}
