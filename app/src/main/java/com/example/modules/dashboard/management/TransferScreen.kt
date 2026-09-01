package com.example.modules.dashboard.management

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.TransferState
import com.example.shared.models.Member
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    wallets: List<WalletAccount>,
    members: List<Member>,
    transactions: List<com.example.shared.models.Transaction> = emptyList(),
    transferState: TransferState = TransferState.Idle,
    onTransfer: (amount: Long, note: String, fromWalletId: String, toWalletId: String) -> Unit,
    onResetState: () -> Unit = {},
    onBack: () -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var fromWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    var toWalletId by remember { mutableStateOf(wallets.lastOrNull()?.id ?: "") }

    LaunchedEffect(fromWalletId) {
        if (toWalletId == fromWalletId) {
            val nextWallet = wallets.firstOrNull { it.id != fromWalletId }
            if (nextWallet != null) toWalletId = nextWallet.id
        }
    }
    
    LaunchedEffect(transferState) {
        if (transferState is TransferState.Success || transferState is TransferState.Error) {
            onResetState()
            if (transferState is TransferState.Success) onBack()
        }
    }

    val toWallet = remember(toWalletId, wallets) { wallets.find { it.id == toWalletId } }
    val capEvaluation = remember(toWallet, amountStr, transactions) {
        val amt = amountStr.toLongOrNull() ?: amountStr.toDoubleOrNull()?.toLong() ?: 0L
        com.example.modules.dashboard.logic.TransferBudgetCapCalculator.evaluate(toWallet, amt, transactions)
    }

    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Funds", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            com.example.modules.dashboard.primitives.TransferCapWarningCard(capEvaluation)

            OutlinedTextField(value = amountStr, onValueChange = { if(it.all { c -> c.isDigit() }) amountStr = it }, label = { Text("Transfer Amount (Rp)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note (Optional)") }, modifier = Modifier.fillMaxWidth())
            
            Text("From Wallet", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                wallets.forEach { w ->
                    val m = members.find { it.id == w.memberId }
                    val isSelected = w.id == fromWalletId
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (isSelected) DesignTokens.CobaltAccent else DesignTokens.Surface).clickable { fromWalletId = w.id }.padding(12.dp)) {
                        Column {
                            Text(w.name, color = if (isSelected) Color.White else DesignTokens.TextPrimary, fontWeight = FontWeight.Bold)
                            Text("${m?.name} • ${formatter.format(w.balance)}", color = if (isSelected) Color.White.copy(alpha=0.8f) else DesignTokens.TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
            
            Text("To Wallet", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                wallets.filter { it.id != fromWalletId }.forEach { w ->
                    val m = members.find { it.id == w.memberId }
                    val isSelected = w.id == toWalletId
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (isSelected) DesignTokens.EmeraldGlow else DesignTokens.Surface).clickable { toWalletId = w.id }.padding(12.dp)) {
                        Column {
                            Text(w.name, color = if (isSelected) Color.White else DesignTokens.TextPrimary, fontWeight = FontWeight.Bold)
                            val capNote = if (w.monthlyTransferCap > 0) " (Plafon: ${formatter.format(w.monthlyTransferCap)})" else ""
                            Text("${m?.name}$capNote • ${formatter.format(w.balance)}", color = if (isSelected) Color.White.copy(alpha=0.8f) else DesignTokens.TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            if (transferState is TransferState.Error) {
                Text(
                    text = transferState.message,
                    color = DesignTokens.RoseAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }
            
            val isLoading = transferState is TransferState.Loading
            val sameWallet = fromWalletId == toWalletId
            val insufficientBalance = (amountStr.toLongOrNull() ?: 0L) > (wallets.find { it.id == fromWalletId }?.balance ?: 0L)
            val isDisabled = isLoading || sameWallet || amountStr.isBlank() || amountStr.toLongOrNull() == 0L || insufficientBalance
            
            Button(
                onClick = {
                    val amount = amountStr.toLongOrNull() ?: amountStr.toDoubleOrNull()?.toLong() ?: 0L
                    if (amount > 0L && fromWalletId != toWalletId && fromWalletId.isNotBlank() && toWalletId.isNotBlank()) {
                        onTransfer(amount, if (note.isBlank()) "Transfer" else note, fromWalletId, toWalletId)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (sameWallet) DesignTokens.RoseAccent else DesignTokens.CobaltAccent,
                    disabledContainerColor = DesignTokens.BorderLight
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = !isDisabled
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (sameWallet) "Pilih Wallet Berbeda" else if (insufficientBalance) "Saldo Tidak Cukup" else "Confirm Transfer",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
