package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.primitives.TransactionItem
import com.example.shared.models.*
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailScreen(
    walletId: String,
    wallets: List<WalletAccount>,
    members: List<Member>,
    transactions: List<Transaction>,
    categories: List<Category>,
    onTransactionClick: (Transaction) -> Unit = {},
    onBack: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val wallet = wallets.find { it.id == walletId }
    val member = members.find { it.id == wallet?.memberId }
    val walletTransactions = transactions.filter { it.walletId == walletId }
    val tint = com.example.shared.utils.MemberRoleHelper.getRoleColor(member, members)

    val tfIn = walletTransactions.filter { it.amount > 0 && (it.note.contains("transfer", ignoreCase = true) || it.categoryId.contains("tf", ignoreCase = true)) }.sumOf { it.amount }
    val tfOut = walletTransactions.filter { it.amount < 0 && (it.note.contains("transfer", ignoreCase = true) || it.categoryId.contains("tf", ignoreCase = true)) }.sumOf { kotlin.math.abs(it.amount) }
    val expPaid = walletTransactions.filter { it.amount < 0 && !it.note.contains("transfer", ignoreCase = true) && !it.categoryId.contains("tf", ignoreCase = true) }.sumOf { kotlin.math.abs(it.amount) }
    val debtBalance = tfIn - tfOut - expPaid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(wallet?.name ?: "Wallet Detail", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DesignTokens.TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.PaddingMedium)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth().height(140.dp), shape = RoundedCornerShape(DesignTokens.CornerRadius), elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(tint, tint.copy(alpha = 0.7f)))).padding(DesignTokens.PaddingLarge)) {
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(wallet?.name ?: "Unknown Wallet", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(wallet?.type ?: "Savings", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                            }
                            Column {
                                Text("Balance", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                                Text(formatter.format(wallet?.balance ?: 0L), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Saldo Hutang-Piutang", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                            Text((if (debtBalance > 0L) "+" else "") + formatter.format(debtBalance), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (debtBalance > 0L) DesignTokens.CobaltAccent else if (debtBalance < 0L) DesignTokens.CrimsonAccent else DesignTokens.TextPrimary)
                        }
                        if ((wallet?.monthlyTransferCap ?: 0L) > 0L) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Plafon Transfer Bulanan", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                                Text(formatter.format(wallet?.monthlyTransferCap ?: 0L), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextPrimary)
                            }
                        }
                    }
                }
            }

            item { Text("Transaction History", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = DesignTokens.TextPrimary) }

            if (walletTransactions.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(DesignTokens.CornerRadius), colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface), border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass)) {
                        Column(modifier = Modifier.padding(32.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🪹", fontSize = 32.sp)
                            Text("Belum Ada Transaksi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DesignTokens.TextPrimary)
                            Text("Transaksi dari dompet ini akan muncul di sini.", color = DesignTokens.TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(walletTransactions) { tx ->
                    TransactionItem(tx = tx, member = member, category = categories.find { it.id == tx.categoryId }, onClick = { onTransactionClick(tx) })
                }
            }
        }
    }
}
