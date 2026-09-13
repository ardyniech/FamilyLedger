package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette
import com.example.shared.utils.MathUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WifeRecentHouseholdList(
    transactions: List<Transaction>,
    categories: List<Category>,
    wallets: List<WalletAccount>,
    onTransactionClick: (String) -> Unit
) {
    val palette = RoleThemePalette.istri()
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated),
        border = BorderStroke(1.dp, palette.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "📋 Catatan Belanja Terkini Bunda",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DesignTokens.TextPrimary
            )

            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Belum ada catatan belanja. Ketuk tombol preset di atas untuk mulai!",
                        fontSize = 11.sp,
                        color = DesignTokens.TextSecondary
                    )
                }
            } else {
                transactions.take(5).forEach { tx ->
                    val category = categories.find { it.id == tx.categoryId }
                    val wallet = wallets.find { it.id == tx.walletId }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTransactionClick(tx.id) }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.note.ifBlank { category?.name ?: "Belanja Dapur" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DesignTokens.TextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "${wallet?.name ?: "Dompet"} • ${dateFormat.format(Date(tx.timestamp))}",
                                fontSize = 10.sp,
                                color = DesignTokens.TextSecondary
                            )
                        }
                        Text(
                            text = "-${MathUtils.formatRupiah(kotlin.math.abs(tx.amount))}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.secondaryAccent
                        )
                    }
                }
            }
        }
    }
}
