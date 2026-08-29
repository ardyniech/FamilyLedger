package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.primitives.DateTimePickerRow
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens

@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    wallets: List<WalletAccount>,
    categories: List<Category>,
    onSave: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by remember { mutableStateOf(kotlin.math.abs(transaction.amount).toLong().toString()) }
    var noteText by remember { mutableStateOf(transaction.note) }
    var isIncome by remember { mutableStateOf(transaction.amount > 0) }
    var selectedWalletId by remember { mutableStateOf(transaction.walletId) }
    var selectedCategoryId by remember { mutableStateOf(transaction.categoryId) }
    var selectedTimestamp by remember { mutableLongStateOf(transaction.timestamp) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Transaksi", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { isIncome = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (!isIncome) Color(0xFFFF5252) else DesignTokens.Surface),
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                    ) { Text("Pengeluaran", color = if (!isIncome) Color.White else DesignTokens.TextPrimary, fontSize = 11.sp) }
                    Button(
                        onClick = { isIncome = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isIncome) DesignTokens.EmeraldGlow else DesignTokens.Surface),
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                    ) { Text("Pemasukan", color = if (isIncome) Color.White else DesignTokens.TextPrimary, fontSize = 11.sp) }
                }

                DateTimePickerRow(selectedTimestamp = selectedTimestamp, onTimestampChanged = { selectedTimestamp = it })

                OutlinedTextField(value = amountText, onValueChange = { input -> amountText = input.filter { it.isDigit() } }, label = { Text("Nominal (Rp)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = noteText, onValueChange = { noteText = it }, label = { Text("Catatan") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                Text("Pilih Dompet", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DesignTokens.TextPrimary)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    wallets.forEach { w ->
                        val isSelected = w.id == selectedWalletId
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (isSelected) DesignTokens.CobaltAccent else DesignTokens.Surface).clickable { selectedWalletId = w.id }.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) { Text(w.name, color = if (isSelected) Color.White else DesignTokens.TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    }
                }

                Text("Pilih Kategori", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DesignTokens.TextPrimary)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val filteredCats = categories.filter { if (isIncome) it.type == "Income" else it.type == "Expense" }
                    (if (filteredCats.isEmpty()) categories else filteredCats).forEach { c ->
                        val isSelected = c.id == selectedCategoryId
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (isSelected) DesignTokens.AmberAccent else DesignTokens.Surface).clickable { selectedCategoryId = c.id }.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) { Text(c.name, color = if (isSelected) Color.White else DesignTokens.TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toLongOrNull() ?: amountText.toDoubleOrNull()?.toLong() ?: 0L
                    val finalAmount = if (isIncome) kotlin.math.abs(amt) else -kotlin.math.abs(amt)
                    onSave(transaction.copy(amount = finalAmount, note = noteText, walletId = selectedWalletId, categoryId = selectedCategoryId, timestamp = selectedTimestamp))
                },
                colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
            ) { Text("Simpan Perubahan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) } }
    )
}
