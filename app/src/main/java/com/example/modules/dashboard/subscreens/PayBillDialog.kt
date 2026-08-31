package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        title = { Text("Bayar Tagihan Rutin", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Pilih dompet untuk mendebit ${formatter.format(targetBill?.amount ?: 0L)}:", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                wallets.forEach { wallet ->
                    val isSelected = wallet.id == selectedWalletId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) DesignTokens.CobaltAccent.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { selectedWalletId = wallet.id }
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(wallet.name, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 13.sp)
                        Text(formatter.format(wallet.balance), fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { if (targetBill != null && selectedWalletId.isNotEmpty()) onConfirm(targetBill.id, selectedWalletId) }) {
                Text("Konfirmasi Pembayaran", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) } }
    )
}
