package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.modules.dashboard.primitives.TransactionItem
import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CategoryTransactionsDialog(
    category: Category,
    transactions: List<Transaction>,
    members: List<Member>,
    onTransactionClick: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val catTxs = transactions.filter { it.categoryId == category.id }.sortedByDescending { it.timestamp }
    val totalAmount = catTxs.sumOf { if (it.amount < 0) -it.amount else it.amount }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(DesignTokens.CornerRadius),
            color = DesignTokens.Surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(DesignTokens.PaddingMedium)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = category.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DesignTokens.TextPrimary
                        )
                        Text(
                            text = "${catTxs.size} Transaksi • Total: ${fmt.format(totalAmount)}",
                            fontSize = 12.sp,
                            color = DesignTokens.CobaltAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DesignTokens.CobaltAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = category.type,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DesignTokens.CobaltAccent
                        )
                    }
                }

                HorizontalDivider(color = DesignTokens.BorderGlass)

                if (catTxs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada transaksi di kategori ini.",
                            color = DesignTokens.TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(catTxs) { tx ->
                            TransactionItem(
                                tx = tx,
                                member = members.find { it.id == tx.memberId },
                                category = category,
                                onClick = {
                                    onTransactionClick(tx)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Tutup", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                    }
                }
            }
        }
    }
}
