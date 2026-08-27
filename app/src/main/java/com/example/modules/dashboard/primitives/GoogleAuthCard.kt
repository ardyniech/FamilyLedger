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
import androidx.compose.runtime.Composable
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
fun GoogleAuthCard(
    authState: AuthUiState,
    onSignIn: (Context) -> Unit,
    onSignOut: (Context) -> Unit,
    onClearError: () -> Unit
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(DesignTokens.PaddingMedium), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Lock, contentDescription = "Security", tint = DesignTokens.CobaltAccent, modifier = Modifier.size(20.dp))
                Text("Autentikasi Multi-User", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 15.sp)
            }

            when (authState) {
                is AuthUiState.Authenticated -> {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(DesignTokens.EmeraldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = DesignTokens.EmeraldAccent)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(authState.user.displayName, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 14.sp)
                                Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = DesignTokens.EmeraldAccent, modifier = Modifier.size(14.dp))
                            }
                            Text(authState.user.email, fontSize = 12.sp, color = DesignTokens.TextSecondary)
                        }
                        OutlinedButton(
                            onClick = { onSignOut(context) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DesignTokens.RoseAccent)
                        ) {
                            Text("Keluar", fontSize = 11.sp)
                        }
                    }
                }
                is AuthUiState.Authenticating -> {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = DesignTokens.CobaltAccent, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Menghubungkan ke Google...", fontSize = 13.sp, color = DesignTokens.TextSecondary)
                    }
                }
                is AuthUiState.Error -> {
                    Text(authState.message, color = DesignTokens.RoseAccent, fontSize = 12.sp)
                    Button(
                        onClick = { onClearError(); onSignIn(context) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.CornerRadius),
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
                    ) {
                        Text("Coba Masuk Lagi")
                    }
                }
                is AuthUiState.Unauthenticated -> {
                    Text("Masuk dengan Akun Google untuk mengamankan data dan menghubungkan sesi multi-user pasangan.", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                    Button(
                        onClick = { onSignIn(context) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.CornerRadius),
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent, contentColor = Color.White)
                    ) {
                        Text("Masuk dengan Google", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
