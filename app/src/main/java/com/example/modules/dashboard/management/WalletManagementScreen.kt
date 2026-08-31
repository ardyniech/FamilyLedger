package com.example.modules.dashboard.management

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.dialogs.WalletEditDialog
import com.example.modules.dashboard.logic.TransferBudgetCapCalculator
import com.example.modules.dashboard.primitives.WalletCapProgressBar
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletManagementScreen(
    wallets: List<WalletAccount>,
    members: List<Member>,
    transactions: List<Transaction> = emptyList(),
    onSaveWallet: (id: String?, memberId: String, type: String, name: String, balance: Long, transferCap: Long) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editWallet by remember { mutableStateOf<WalletAccount?>(null) }
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallets & Plafon", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = DesignTokens.CobaltAccent, contentColor = Color.White) {
                Icon(Icons.Default.Add, "Add Wallet")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(wallets) { wallet ->
                val member = members.find { it.id == wallet.memberId }
                val capEval = remember(wallet, transactions) {
                    TransferBudgetCapCalculator.evaluate(wallet, 0L, transactions)
                }
                Card(colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface), border = BorderStroke(1.dp, DesignTokens.BorderGlass), modifier = Modifier.fillMaxWidth().clickable { editWallet = wallet }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(wallet.name, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextPrimary)
                                Text("${wallet.type} • ${member?.name ?: "Unknown"}", color = DesignTokens.TextSecondary, fontSize = 12.sp)
                                if (wallet.monthlyTransferCap > 0L) {
                                    Text("Plafon: ${formatter.format(wallet.monthlyTransferCap)}/bln", color = DesignTokens.AmberAccent, fontSize = 11.sp)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(formatter.format(wallet.balance), fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DesignTokens.TextSecondary, modifier = Modifier.size(16.dp).padding(top = 4.dp))
                            }
                        }
                        if (capEval != null) {
                            WalletCapProgressBar(evaluation = capEval)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog || editWallet != null) {
        WalletEditDialog(
            wallet = editWallet,
            members = members,
            onDismiss = { showAddDialog = false; editWallet = null },
            onSave = { id, mId, type, name, bal, cap ->
                onSaveWallet(id, mId, type, name, bal, cap)
                showAddDialog = false
                editWallet = null
            }
        )
    }
}
