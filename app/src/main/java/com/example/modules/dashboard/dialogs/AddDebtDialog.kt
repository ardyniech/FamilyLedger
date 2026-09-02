package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.DebtRecord
import com.example.shared.theme.DesignTokens

@Composable
fun AddDebtDialog(
    onDismiss: () -> Unit,
    onSubmit: (DebtRecord) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var isHutang by remember { mutableStateOf(true) }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Catatan Catat Hutang/Piutang", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = isHutang,
                        onClick = { isHutang = true },
                        label = { Text("Hutang (Saya Utang)") }
                    )
                    FilterChip(
                        selected = !isHutang,
                        onClick = { isHutang = false },
                        label = { Text("Piutang (Dia Utang)") }
                    )
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Orang / Pihak") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    label = { Text("Jumlah (Rp)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Catatan / Jatuh Tempo") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toLongOrNull() ?: 0L
                    if (name.isNotBlank() && amt > 0) {
                        onSubmit(
                            DebtRecord(
                                personName = name,
                                isHutang = isHutang,
                                amount = amt,
                                dueDate = System.currentTimeMillis() + 30 * 86400000L,
                                note = note
                            )
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
            ) {
                Text("Simpan", color = DesignTokens.TextPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) }
        },
        containerColor = DesignTokens.SurfaceElevated
    )
}
