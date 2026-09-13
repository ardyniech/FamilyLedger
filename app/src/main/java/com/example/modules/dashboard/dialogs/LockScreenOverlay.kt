package com.example.modules.dashboard.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
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
import com.example.core.auth.BiometricAuthHelper
import com.example.shared.theme.DesignTokens
import kotlinx.coroutines.launch

@Composable
fun LockScreenOverlay(
    isBiometricAllowed: Boolean = false,
    onVerifyPin: (String) -> Boolean,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var showBiometricSuccessMsg by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }

    fun handlePinVerification(pinToTest: String) {
        if (onVerifyPin(pinToTest)) {
            BiometricAuthHelper.triggerHapticFeedback(context, true)
            onSuccess()
        } else {
            BiometricAuthHelper.triggerHapticFeedback(context, false)
            isError = true
            enteredPin = ""
            coroutineScope.launch {
                shakeOffset.animateTo(0f, animationSpec = keyframes {
                    durationMillis = 350
                    -20f at 50
                    20f at 100
                    -15f at 150
                    15f at 200
                    -8f at 250
                    8f at 300
                    0f at 350
                })
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(DesignTokens.BackgroundBottom).padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(x = shakeOffset.value.dp)
        ) {
            Surface(shape = CircleShape, color = DesignTokens.CobaltAccent.copy(alpha = 0.15f), modifier = Modifier.size(76.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(Icons.Filled.Lock, contentDescription = "Lock", tint = DesignTokens.CobaltAccent, modifier = Modifier.size(40.dp))
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text("Aplikasi Terkunci", color = DesignTokens.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isError) "PIN Salah! Coba lagi." else if (showBiometricSuccessMsg) "Verifikasi Berhasil..." else "Masukkan 4 digit PIN atau Biometrik",
                color = if (isError) DesignTokens.RoseAccent else DesignTokens.TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in 0 until 4) {
                    val filled = i < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (filled) DesignTokens.CobaltAccent else DesignTokens.BorderGlass)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            LockKeypadGrid(
                isBiometricAllowed = isBiometricAllowed,
                onDigitClick = { digit ->
                    isError = false
                    if (enteredPin.length < 4) {
                        enteredPin += digit
                        if (enteredPin.length == 4) handlePinVerification(enteredPin)
                    }
                },
                onClearClick = {
                    isError = false
                    enteredPin = ""
                },
                onBiometricClick = {
                    BiometricAuthHelper.triggerHapticFeedback(context, true)
                    showBiometricSuccessMsg = true
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(200)
                        onSuccess()
                    }
                },
                onOkClick = {
                    if (enteredPin.isNotEmpty()) handlePinVerification(enteredPin)
                }
            )
        }
    }
}
