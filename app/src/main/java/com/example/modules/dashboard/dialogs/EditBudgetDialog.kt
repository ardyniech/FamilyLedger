package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun EditBudgetDialog(
    monthlyBudget: Double,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var inputBudget by remember { mutableStateOf(monthlyBudget.toLong().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Atur Anggaran Bulanan", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tentukan batas pengeluaran rumah tangga yang disepakati bersama:", fontSize = 12.sp)
                OutlinedTextField(
                    value = inputBudget,
                    onValueChange = { inputBudget = it.filter { char -> char.isDigit() } },
                    label = { Text("Nominal Anggaran (Rp)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = inputBudget.toDoubleOrNull() ?: monthlyBudget
                    onConfirm(parsed)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
            ) {
                Text("Simpan Kesepakatan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = DesignTokens.TextSecondary)
            }
        }
    )
}
