package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.auth.AppLockManager
import com.example.shared.theme.DesignTokens

@Composable
fun AppSecuritySettingsSection(
    appLockManager: AppLockManager,
    onRequirePinSetup: () -> Unit
) {
    var isLockEnabled by remember { mutableStateOf(appLockManager.isLockEnabled()) }
    var isBiometricEnabled by remember { mutableStateOf(appLockManager.isBiometricEnabled()) }

    Card(
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, DesignTokens.BorderLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = DesignTokens.CobaltAccent, modifier = Modifier.size(20.dp))
                    Column {
                        Text("Kunci Aplikasi (Passcode)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                        Text("Amankan data ledger sebelum dibuka", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    }
                }
                Switch(
                    checked = isLockEnabled,
                    onCheckedChange = { enabled ->
                        isLockEnabled = enabled
                        appLockManager.setLockEnabled(enabled)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = DesignTokens.CobaltAccent)
                )
            }

            if (isLockEnabled) {
                HorizontalDivider(color = DesignTokens.BorderLight, thickness = 0.5.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = DesignTokens.EmeraldAccent, modifier = Modifier.size(20.dp))
                        Column {
                            Text("Buka via Biometrik", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = DesignTokens.TextPrimary)
                            Text("Buka instan dengan sidik jari", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                        }
                    }
                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = { enabled ->
                            isBiometricEnabled = enabled
                            appLockManager.setBiometricEnabled(enabled)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = DesignTokens.EmeraldAccent)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Pin, contentDescription = null, tint = DesignTokens.AmberAccent, modifier = Modifier.size(20.dp))
                        Text("PIN Saat Ini: ${appLockManager.getMaskedPin()}", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    }
                    TextButton(onClick = onRequirePinSetup, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                        Text("Ubah PIN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                    }
                }
            }
        }
    }
}
