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
fun MemberTransactionsDialog(
    member: Member,
    transactions: List<Transaction>,
    categories: List<Category>,
    onTransactionClick: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val memberTxs = transactions.filter { it.memberId == member.id }.sortedByDescending { it.timestamp }
    val totalExpense = memberTxs.filter { it.amount < 0 }.sumOf { -it.amount }
    val totalIncome = memberTxs.filter { it.amount > 0 }.sumOf { it.amount }
    val roleColor = if (member.role == "Husband") DesignTokens.CobaltAccent else DesignTokens.AmberAccent

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(roleColor)
                        )
                        Column {
                            Text(
                                text = "Riwayat ${member.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = DesignTokens.TextPrimary
                            )
                            Text(
                                text = "${member.role} • ${memberTxs.size} Transaksi",
                                fontSize = 12.sp,
                                color = DesignTokens.TextSecondary
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(roleColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = member.role,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = roleColor
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DesignTokens.SurfaceGlass)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Pengeluaran", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                        Text(fmt.format(totalExpense), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DesignTokens.RoseAccent)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Pemasukan", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                        Text("+${fmt.format(totalIncome)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DesignTokens.EmeraldGlow)
                    }
                }

                HorizontalDivider(color = DesignTokens.BorderGlass)

                if (memberTxs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada transaksi untuk ${member.name}.",
                            color = DesignTokens.TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(memberTxs) { tx ->
                            TransactionItem(
                                tx = tx,
                                member = member,
                                category = categories.find { it.id == tx.categoryId },
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
