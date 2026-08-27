package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens

@Composable
fun AddExpenseDialog(
    wallets: List<WalletAccount>,
    categories: List<Category>,
    onConfirm: (Double, String, String, String, Long) -> Unit,
    onDismiss: () -> Unit
) {
    var expenseAmount by remember { mutableStateOf("") }
    var expenseNote by remember { mutableStateOf("") }
    var selectedWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull { it.type == "Expense" }?.id ?: "") }
    var selectedTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Catat Pengeluaran", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                DateTimePickerRow(
                    selectedTimestamp = selectedTimestamp,
                    onTimestampChanged = { selectedTimestamp = it }
                )
                OutlinedTextField(
                    value = expenseAmount,
                    onValueChange = { input -> expenseAmount = input.filter { it.isDigit() } },
                    label = { Text("Nominal (Rp)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = expenseNote,
                    onValueChange = { expenseNote = it },
                    label = { Text("Catatan / Kebutuhan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Pilih Dompet", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DesignTokens.TextPrimary)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    wallets.forEach { w ->
                        val isSelected = w.id == selectedWalletId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DesignTokens.CobaltAccent else DesignTokens.Surface)
                                .clickable { selectedWalletId = w.id }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(w.name, color = if (isSelected) Color.White else DesignTokens.TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Text("Pilih Kategori", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DesignTokens.TextPrimary)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.filter { it.type == "Expense" }.forEach { c ->
                        val isSelected = c.id == selectedCategoryId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DesignTokens.AmberAccent else DesignTokens.Surface)
                                .clickable { selectedCategoryId = c.id }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(c.name, color = if (isSelected) Color.White else DesignTokens.TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = expenseAmount.toDoubleOrNull()
                    if (amt != null && amt > 0 && expenseNote.isNotBlank()) {
                        onConfirm(amt, expenseNote, selectedWalletId, selectedCategoryId, selectedTimestamp)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) }
        }
    )
}
