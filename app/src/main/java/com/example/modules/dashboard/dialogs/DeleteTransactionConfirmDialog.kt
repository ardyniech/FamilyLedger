package com.example.modules.dashboard.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens

@Composable
fun DeleteTransactionConfirmDialog(
    transaction: Transaction,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF5252)) },
        title = { Text("Hapus Transaksi?", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
        text = {
            Text(
                "Transaksi '${transaction.note.ifBlank { "Tanpa Catatan" }}' akan dihapus permanen dan saldo dompet akan dikembalikan secara otomatis.",
                color = DesignTokens.TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
            ) {
                Text("Ya, Hapus", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) }
        }
    )
}
