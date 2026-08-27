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
    onTransfer: (amount: Double, note: String, fromWalletId: String, toWalletId: String) -> Unit,
    onBack: () -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var fromWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    var toWalletId by remember { mutableStateOf(wallets.lastOrNull()?.id ?: "") }

    LaunchedEffect(fromWalletId) {
        if (toWalletId == fromWalletId) {
            val nextWallet = wallets.firstOrNull { it.id != fromWalletId }
            if (nextWallet != null) {
                toWalletId = nextWallet.id
            }
        }
    }

    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Funds", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
                border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "💡 If you transfer to an account owned by a different family member (e.g., Husband to Wife), it will automatically be recorded as an Expense for you and Income for them, adhering to accounting standards.", 
                        style = MaterialTheme.typography.bodySmall, 
                        color = DesignTokens.TextSecondary
                    )
                }
            }

            OutlinedTextField(
                value = amountStr,
                onValueChange = { if(it.all { c -> c.isDigit() }) amountStr = it },
                label = { Text("Transfer Amount (Rp)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DesignTokens.CobaltAccent)
            )
            
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DesignTokens.CobaltAccent)
            )
            
            Text("From Wallet", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                wallets.forEach { w ->
                    val m = members.find { it.id == w.memberId }
                    val isSelected = w.id == fromWalletId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) DesignTokens.CobaltAccent else DesignTokens.Surface)
                            .clickable { fromWalletId = w.id }
                            .padding(12.dp)
                    ) {
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) DesignTokens.EmeraldGlow else DesignTokens.Surface)
                            .clickable { toWalletId = w.id }
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(w.name, color = if (isSelected) Color.White else DesignTokens.TextPrimary, fontWeight = FontWeight.Bold)
                            Text("${m?.name} • ${formatter.format(w.balance)}", color = if (isSelected) Color.White.copy(alpha=0.8f) else DesignTokens.TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && fromWalletId != toWalletId && fromWalletId.isNotBlank() && toWalletId.isNotBlank()) {
                        onTransfer(amount, if (note.isBlank()) "Transfer" else note, fromWalletId, toWalletId)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Confirm Transfer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
