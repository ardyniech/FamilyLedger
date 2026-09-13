package com.example.modules.dashboard.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.modules.dashboard.TransferState
import com.example.modules.dashboard.management.primitives.TransferFormFields
import com.example.modules.dashboard.management.primitives.TransferWalletPickers
import com.example.modules.dashboard.primitives.TransferCapWarningCard
import com.example.shared.models.Member
import com.example.shared.models.WalletAccount
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

    val formatter = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")) }
    val sameWallet = fromWalletId == toWalletId
    val sourceBalance = wallets.find { it.id == fromWalletId }?.balance ?: 0L
    val insufficientBalance = (amountStr.toLongOrNull() ?: 0L) > sourceBalance

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Antar Dompet & Pasangan", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TransferCapWarningCard(capEvaluation)

            TransferWalletPickers(
                wallets = wallets,
                members = members,
                fromWalletId = fromWalletId,
                toWalletId = toWalletId,
                formatter = formatter,
                onSelectFromWallet = { fromWalletId = it },
                onSelectToWallet = { toWalletId = it }
            )

            TransferFormFields(
                amountStr = amountStr,
                onAmountChanged = { amountStr = it },
                note = note,
                onNoteChanged = { note = it },
                transferState = transferState,
                sameWallet = sameWallet,
                insufficientBalance = insufficientBalance,
                onSubmit = {
                    val amount = amountStr.toLongOrNull() ?: amountStr.toDoubleOrNull()?.toLong() ?: 0L
                    if (amount > 0L && !sameWallet && fromWalletId.isNotBlank() && toWalletId.isNotBlank()) {
                        onTransfer(amount, note.ifBlank { "Transfer Dana" }, fromWalletId, toWalletId)
                    }
                }
            )
        }
    }
}
