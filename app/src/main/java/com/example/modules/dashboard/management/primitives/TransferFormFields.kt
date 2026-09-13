package com.example.modules.dashboard.management.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.TransferState
import com.example.shared.theme.DesignTokens

@Composable
fun TransferFormFields(
    amountStr: String,
    onAmountChanged: (String) -> Unit,
    note: String,
    onNoteChanged: (String) -> Unit,
    transferState: TransferState,
    sameWallet: Boolean,
    insufficientBalance: Boolean,
    onSubmit: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = amountStr,
            onValueChange = { if (it.all { c -> c.isDigit() }) onAmountChanged(it) },
            label = { Text("Nominal Transfer (Rp)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DesignTokens.TextPrimary,
                unfocusedTextColor = DesignTokens.TextPrimary,
                focusedBorderColor = DesignTokens.CobaltAccent,
                unfocusedBorderColor = DesignTokens.BorderLight
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = note,
            onValueChange = onNoteChanged,
            label = { Text("Catatan / Keterangan (Opsional)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DesignTokens.TextPrimary,
                unfocusedTextColor = DesignTokens.TextPrimary,
                focusedBorderColor = DesignTokens.CobaltAccent,
                unfocusedBorderColor = DesignTokens.BorderLight
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (transferState is TransferState.Error) {
            Text(
                text = transferState.message,
                color = DesignTokens.RoseAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        val isLoading = transferState is TransferState.Loading
        val isDisabled = isLoading || sameWallet || amountStr.isBlank() || amountStr.toLongOrNull() == 0L || insufficientBalance

        Button(
            onClick = onSubmit,
            enabled = !isDisabled,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (sameWallet) DesignTokens.RoseAccent else DesignTokens.CobaltAccent,
                disabledContainerColor = DesignTokens.BorderLight
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = when {
                        sameWallet -> "Pilih Dompet Berbeda"
                        insufficientBalance -> "Saldo Tidak Mencukupi"
                        else -> "Kirim Transfer Dana"
                    },
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
