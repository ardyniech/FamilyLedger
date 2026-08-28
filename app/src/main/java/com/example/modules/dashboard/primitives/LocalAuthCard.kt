package com.example.modules.dashboard.primitives

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.AuthUiState
import com.example.shared.theme.DesignTokens

@Composable
fun LocalAuthCard(
    authState: AuthUiState,
    onSignIn: (String, String, Context) -> Unit,
    onCreateAccount: (String, String, Context) -> Unit,
    onSignOut: (Context) -> Unit,
    onClearError: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var isCreateMode by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            when (authState) {
                is AuthUiState.Authenticated -> {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(DesignTokens.EmeraldGlow.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DesignTokens.EmeraldGlow)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Akun Lokal Terhubung", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(authState.user.email, fontSize = 12.sp, color = DesignTokens.TextSecondary)
                        }
                        OutlinedButton(onClick = { onSignOut(context) }, colors = ButtonDefaults.outlinedButtonColors(contentColor = DesignTokens.RoseAccent)) {
                            Text("Keluar", fontSize = 11.sp)
                        }
                    }
                }
                is AuthUiState.Authenticating -> {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DesignTokens.CobaltAccent, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Memverifikasi...", fontSize = 13.sp, color = DesignTokens.TextSecondary)
                    }
                }
                else -> {
                    if (authState is AuthUiState.Error) {
                        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DesignTokens.RoseAccent.copy(alpha = 0.15f)).padding(10.dp)) {
                            Column {
                                Text(authState.message, fontSize = 11.sp, color = DesignTokens.RoseAccent, fontWeight = FontWeight.SemiBold)
                                Text("Ketuk untuk menutup", fontSize = 10.sp, color = DesignTokens.TextSecondary, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(DesignTokens.CobaltAccent.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = DesignTokens.CobaltAccent)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Keamanan Lokal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Amankan aplikasi ini dengan sandi lokal. Tanpa cloud tracking.", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { isCreateMode = false; showDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
                        ) {
                            Text("Masuk", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        OutlinedButton(
                            onClick = { isCreateMode = true; showDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Buat Akun", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (isCreateMode) "Buat Akun Lokal" else "Masuk") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { userId = it },
                        label = { Text("ID Pengguna") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Kata Sandi") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (isCreateMode) {
                        onCreateAccount(userId, password, context)
                    } else {
                        onSignIn(userId, password, context)
                    }
                    showDialog = false
                }) {
                    Text("Konfirmasi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
