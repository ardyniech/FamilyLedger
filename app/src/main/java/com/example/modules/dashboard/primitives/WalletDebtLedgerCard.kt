package com.example.modules.dashboard.primitives

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.modules.dashboard.logic.DebtLedgerCalculator
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens

@Composable
fun WalletDebtLedgerCard(
    wallets: List<WalletAccount>,
    members: List<Member>,
    transactions: List<Transaction>,
    onSettleClick: ((WalletAccount) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val debtItems = remember(wallets, members, transactions) {
        DebtLedgerCalculator.calculate(wallets, members, transactions)
    }
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth().clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🤝", fontSize = 18.sp)
                    Text("Ledger Hutang-Piutang Partner", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 14.sp)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(DesignTokens.CobaltAccent.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("Kontribusi", color = DesignTokens.CobaltAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                "Running balance: (Transfer Masuk) - (Transfer Keluar) - (Expense). Bukan saldo kas, melainkan status talangan.",
                fontSize = 11.sp,
                color = DesignTokens.TextSecondary
            )

            debtItems.filter { it.totalTransferIn > 0 || it.totalExpensePaid > 0 || it.netDebtBalance != 0.0 }.forEach { item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("${item.wallet.name} (${item.owner?.name ?: "Partner"})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextPrimary)
                        Text(item.statusText, fontSize = 11.sp, color = if (item.netDebtBalance > 0) DesignTokens.CobaltAccent else if (item.netDebtBalance < 0) DesignTokens.CrimsonAccent else DesignTokens.EmeraldAccent)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = (if (item.netDebtBalance > 0) "+" else "") + "Rp ${String.format("%,.0f", item.netDebtBalance)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (item.netDebtBalance > 0) DesignTokens.CobaltAccent else if (item.netDebtBalance < 0) DesignTokens.CrimsonAccent else DesignTokens.TextPrimary
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    HorizontalDivider(color = DesignTokens.SurfaceGlass)
                    debtItems.forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tf In: Rp ${String.format("%,.0f", item.totalTransferIn)} | Exp: Rp ${String.format("%,.0f", item.totalExpensePaid)}", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
