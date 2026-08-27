package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WalletFlowReportCard(
    wallets: List<WalletAccount>,
    members: List<Member>,
    transactions: List<Transaction>,
    onWalletClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

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
                text = "Arus Kas Transparan per Dompet",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DesignTokens.TextPrimary
            )

            if (wallets.isEmpty()) {
                Text("Belum ada dompet terdaftar.", fontSize = 12.sp, color = DesignTokens.TextSecondary)
            } else {
                wallets.forEach { wallet ->
                    val owner = members.find { it.id == wallet.memberId }
                    val walletTxs = transactions.filter { it.walletId == wallet.id }
                    val inFlow = walletTxs.filter { it.amount > 0 }.sumOf { it.amount }
                    val outFlow = walletTxs.filter { it.amount < 0 }.sumOf { -it.amount }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (onWalletClick != null) Modifier.clickable { onWalletClick(wallet.id) } else Modifier),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(wallet.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DesignTokens.TextPrimary)
                            Text("${wallet.type} • ${owner?.name ?: "Bersama"}", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("+${fmt.format(inFlow)}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DesignTokens.EmeraldGlow)
                                Text("-${fmt.format(outFlow)}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFFF5252))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Saldo", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                                Text(fmt.format(wallet.balance), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DesignTokens.TextPrimary)
                            }
                        }
                    }

                    if (wallet != wallets.last()) {
                        HorizontalDivider(color = DesignTokens.BorderGlass.copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}
