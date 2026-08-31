package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Member
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens

@Composable
fun WalletEditDialog(
    wallet: WalletAccount?,
    members: List<Member>,
    onDismiss: () -> Unit,
    onSave: (id: String?, memberId: String, type: String, name: String, balance: Long, transferCap: Long) -> Unit
) {
    val isEdit = wallet != null
    var name by remember { mutableStateOf(wallet?.name ?: "") }
    var type by remember { mutableStateOf(wallet?.type ?: "E-Wallet") }
    var memberId by remember { mutableStateOf(wallet?.memberId ?: members.firstOrNull()?.id ?: "") }
    var balanceStr by remember { mutableStateOf(if (isEdit) wallet?.balance?.toLong().toString() else "") }
    var capStr by remember { mutableStateOf(if (isEdit && wallet != null && wallet.monthlyTransferCap > 0L) wallet.monthlyTransferCap.toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "Edit Wallet" else "Add Wallet") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Wallet Name") }, modifier = Modifier.fillMaxWidth())
                if (!isEdit) {
                    OutlinedTextField(value = balanceStr, onValueChange = { if (it.all { c -> c.isDigit() }) balanceStr = it }, label = { Text("Initial Balance (Rp)") }, modifier = Modifier.fillMaxWidth())
                }
                OutlinedTextField(value = capStr, onValueChange = { if (it.all { c -> c.isDigit() }) capStr = it }, label = { Text("Plafon Transfer Bulanan (Rp, Opsional)") }, modifier = Modifier.fillMaxWidth())
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
                val bal = balanceStr.toLongOrNull() ?: balanceStr.toDoubleOrNull()?.toLong() ?: 0L
                val cap = capStr.toLongOrNull() ?: 0L
                if (name.isNotBlank()) {
                    onSave(wallet?.id, memberId, type, name, if (isEdit) wallet!!.balance else bal, cap)
                    onDismiss()
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
