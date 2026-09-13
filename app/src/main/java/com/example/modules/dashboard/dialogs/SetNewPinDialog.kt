package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun SetNewPinDialog(
    onDismiss: () -> Unit,
    onSavePin: (String) -> Unit
) {
    var pinText by remember { mutableStateOf("") }
    var confirmPinText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = DesignTokens.CobaltAccent)
                Text("Setel 4 Digit PIN Baru", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("PIN disimpan aman di EncryptedSharedPreferences (AES-256).", fontSize = 11.sp, color = DesignTokens.TextSecondary)

                OutlinedTextField(
                    value = pinText,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pinText = it },
                    label = { Text("PIN Baru (4 Digit)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPinText,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) confirmPinText = it },
                    label = { Text("Konfirmasi PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                errorMsg?.let { msg ->
                    Text(msg, fontSize = 11.sp, color = DesignTokens.RoseAccent)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pinText.length != 4) {
                        errorMsg = "PIN harus tepat 4 digit angka."
                    } else if (pinText != confirmPinText) {
                        errorMsg = "Konfirmasi PIN tidak cocok."
                    } else {
                        onSavePin(pinText)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
            ) {
                Text("Simpan PIN")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        containerColor = DesignTokens.SurfaceElevated,
        shape = RoundedCornerShape(16.dp)
    )
}
