package com.example.modules.dashboard.management

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
fun WalletManagementScreen(
    wallets: List<WalletAccount>,
    members: List<Member>,
    onSaveWallet: (id: String?, memberId: String, type: String, name: String, balance: Double) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editWallet by remember { mutableStateOf<WalletAccount?>(null) }
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallets", fontWeight = FontWeight.Bold) },
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
                Card(colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface), border = BorderStroke(1.dp, DesignTokens.BorderGlass), modifier = Modifier.fillMaxWidth().clickable { editWallet = wallet }) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(wallet.name, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextPrimary)
                            Text("${wallet.type} • ${member?.name ?: "Unknown"}", color = DesignTokens.TextSecondary, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(formatter.format(wallet.balance), fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DesignTokens.TextSecondary, modifier = Modifier.size(16.dp).padding(top = 4.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog || editWallet != null) {
        val isEdit = editWallet != null
        var name by remember { mutableStateOf(editWallet?.name ?: "") }
        var type by remember { mutableStateOf(editWallet?.type ?: "E-Wallet") }
        var memberId by remember { mutableStateOf(editWallet?.memberId ?: members.firstOrNull()?.id ?: "") }
        var balanceStr by remember { mutableStateOf(if (isEdit) editWallet?.balance?.toLong().toString() else "") }
        
        AlertDialog(
            onDismissRequest = { showAddDialog = false; editWallet = null },
            title = { Text(if (isEdit) "Edit Wallet" else "Add Wallet") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Wallet Name") }, modifier = Modifier.fillMaxWidth())
                    if (!isEdit) OutlinedTextField(value = balanceStr, onValueChange = { if (it.all { c -> c.isDigit() }) balanceStr = it }, label = { Text("Initial Balance (Rp)") }, modifier = Modifier.fillMaxWidth())
                    Text("Type:")
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Bank", "E-Wallet", "Cash", "Vault").forEach { t ->
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (type == t) DesignTokens.CobaltAccent else DesignTokens.Surface).clickable { type = t }.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(t, color = if (type == t) Color.White else DesignTokens.TextPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                    Text("Owner:")
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        members.forEach { m ->
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (memberId == m.id) DesignTokens.AmberAccent else DesignTokens.Surface).clickable { memberId = m.id }.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(m.name, color = if (memberId == m.id) Color.White else DesignTokens.TextPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val bal = balanceStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onSaveWallet(editWallet?.id, memberId, type, name, if (isEdit) editWallet!!.balance else bal)
                        showAddDialog = false; editWallet = null
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false; editWallet = null }) { Text("Cancel") } }
        )
    }
}

