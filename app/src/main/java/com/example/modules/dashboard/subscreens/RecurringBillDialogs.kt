package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.RecurringBill
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat

@Composable
fun PayBillDialog(
    targetBill: RecurringBill?,
    wallets: List<WalletAccount>,
    formatter: NumberFormat,
    onDismiss: () -> Unit,
    onConfirm: (billId: String, walletId: String) -> Unit
) {
    var selectedWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Deduct Account Payment", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select source wallet to debit ${formatter.format(targetBill?.amount ?: 0.0)}:", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                wallets.forEach { wallet ->
                    val isSelected = wallet.id == selectedWalletId
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(if (isSelected) DesignTokens.CobaltAccent.copy(alpha = 0.1f) else Color.Transparent).clickable { selectedWalletId = wallet.id }.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(wallet.name, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 13.sp)
                        Text(formatter.format(wallet.balance), fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { if (targetBill != null && selectedWalletId.isNotEmpty()) onConfirm(targetBill.id, selectedWalletId) }) {
                Text("Confirm Debit", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = DesignTokens.TextSecondary) } }
    )
}

@Composable
fun AddRecurringBillDialog(
    wallets: List<WalletAccount>,
    categories: List<Category>,
    formatter: NumberFormat,
    onDismiss: () -> Unit,
    onAdd: (name: String, amount: Double, dueDate: String, categoryId: String, autoPay: Boolean, targetWalletId: String?, frequency: String) -> Unit
) {
    var billName by remember { mutableStateOf("") }
    var billAmount by remember { mutableStateOf("") }
    var billDueDate by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }
    var autoPay by remember { mutableStateOf(false) }
    var targetWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    var frequency by remember { mutableStateOf("Monthly") }
    val frequencies = listOf("Monthly", "Weekly", "Daily", "One-Time")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Subscription / Bill", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = billName, onValueChange = { billName = it }, label = { Text("Agreement / Bill Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = billAmount, onValueChange = { billAmount = it }, label = { Text("Amount (Rp)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = billDueDate, onValueChange = { billDueDate = it }, label = { Text("Next Due Date (e.g., Sep 01, 2026)") }, modifier = Modifier.fillMaxWidth())
                Text("Billing Frequency", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DesignTokens.TextSecondary)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    frequencies.forEach { freq ->
                        val isSel = freq == frequency
                        Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp)).background(if (isSel) DesignTokens.CobaltAccent else DesignTokens.BorderGlass.copy(alpha = 0.1f)).clickable { frequency = freq }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                            Text(text = freq, color = if (isSel) Color.White else DesignTokens.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Automatic Ledger Population", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Auto-deduct and post on specific date", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    }
                    Switch(checked = autoPay, onCheckedChange = { autoPay = it })
                }
                if (autoPay) {
                    Text("Auto-Deduct From Account", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    wallets.forEach { wallet ->
                        val isSelected = wallet.id == targetWalletId
                        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(if (isSelected) DesignTokens.CobaltAccent.copy(alpha = 0.1f) else Color.Transparent).clickable { targetWalletId = wallet.id }.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(wallet.name, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 12.sp)
                            Text(formatter.format(wallet.balance), fontSize = 10.sp, color = DesignTokens.TextSecondary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val parsed = billAmount.toDoubleOrNull() ?: 0.0
                if (billName.isNotEmpty() && parsed > 0.0 && billDueDate.isNotEmpty()) {
                    onAdd(billName, parsed, billDueDate, selectedCategoryId, autoPay, if (autoPay) targetWalletId else null, frequency)
                }
                onDismiss()
            }) { Text("Schedule Commit", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = DesignTokens.TextSecondary) } }
    )
}
