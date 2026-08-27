package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TopExpensesReportCard(
    transactions: List<Transaction>,
    categories: List<Category>,
    members: List<Member>,
    onTransactionClick: ((Transaction) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val dateFmt = SimpleDateFormat("dd MMM", Locale("id", "ID"))

    val topExpenses = transactions
        .filter { t -> t.amount < 0 && categories.find { it.id == t.categoryId }?.type == "Expense" }
        .sortedBy { it.amount } // lowest amount is largest negative
        .take(5)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
    ) {
        Column(
            modifier = Modifier.padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "5 Pengeluaran Terbesar Bulan Ini",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DesignTokens.TextPrimary
            )

            if (topExpenses.isEmpty()) {
                Text("Belum ada transaksi pengeluaran bulan ini.", fontSize = 12.sp, color = DesignTokens.TextSecondary)
            } else {
                topExpenses.forEachIndexed { index, tx ->
                    val category = categories.find { it.id == tx.categoryId }
                    val member = members.find { it.id == tx.memberId }
                    val memberTag = if (member?.role == "Husband") "Suami" else "Istri"
                    val tagBg = if (member?.role == "Husband") DesignTokens.CobaltAccent else DesignTokens.AmberAccent

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTransactionClick?.invoke(tx) },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(DesignTokens.SurfaceGlass),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("#${index + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextSecondary)
                            }
                            Column {
                                Text(
                                    text = tx.note.ifEmpty { category?.name ?: "Pengeluaran" },
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = DesignTokens.TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(dateFmt.format(Date(tx.timestamp)), fontSize = 10.sp, color = DesignTokens.TextSecondary)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(tagBg.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(memberTag, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = tagBg)
                                    }
                                }
                            }
                        }

                        Text(
                            text = "-${fmt.format(-tx.amount)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFFF5252)
                        )
                    }

                    if (index < topExpenses.lastIndex) {
                        HorizontalDivider(color = DesignTokens.BorderGlass.copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}
