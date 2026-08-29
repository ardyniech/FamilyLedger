package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared.theme.DesignTokens

@Composable
fun AddGoalDialog(
    onConfirm: (title: String, targetAmount: Long, initialAmount: Long, category: String, iconEmoji: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var initialAmount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Rumah") }
    var iconEmoji by remember { mutableStateOf("🏡") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Impian Bersama", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Impian (misal: Rumah Idaman)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it.filter { char -> char.isDigit() } },
                    label = { Text("Target Dana (Rp)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = initialAmount,
                    onValueChange = { initialAmount = it.filter { char -> char.isDigit() } },
                    label = { Text("Dana Terkumpul Saat Ini (Rp)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            title,
                            targetAmount.toLongOrNull() ?: targetAmount.toDoubleOrNull()?.toLong() ?: 10000000L,
                            initialAmount.toLongOrNull() ?: initialAmount.toDoubleOrNull()?.toLong() ?: 0L,
                            category,
                            iconEmoji
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
            ) {
                Text("Simpan Impian")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = DesignTokens.TextSecondary)
            }
        }
    )
}
