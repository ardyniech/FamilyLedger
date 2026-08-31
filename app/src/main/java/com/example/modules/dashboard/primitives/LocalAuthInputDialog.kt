package com.example.modules.dashboard.primitives

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LocalAuthInputDialog(
    isCreateMode: Boolean,
    context: Context,
    onDismiss: () -> Unit,
    onSignIn: (String, String, Context) -> Unit,
    onCreateAccount: (String, String, Context) -> Unit
) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isCreateMode) "Buat Akun Lokal" else "Masuk") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("ID Pengguna") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Kata Sandi") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                if (isCreateMode) onCreateAccount(userId, password, context) else onSignIn(userId, password, context)
                onDismiss()
            }) { Text("Konfirmasi") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}
