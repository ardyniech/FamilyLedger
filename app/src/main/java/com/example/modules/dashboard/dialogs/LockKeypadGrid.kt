package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun LockKeypadGrid(
    isBiometricAllowed: Boolean,
    onDigitClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onBiometricClick: () -> Unit,
    onOkClick: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", if (isBiometricAllowed) "BIO" else "OK")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { item ->
                    Button(
                        onClick = {
                            when (item) {
                                "C" -> onClearClick()
                                "BIO" -> onBiometricClick()
                                "OK" -> onOkClick()
                                else -> onDigitClick(item)
                            }
                        },
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item == "BIO") DesignTokens.EmeraldAccent.copy(alpha = 0.2f) else DesignTokens.SurfaceElevated
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        if (item == "BIO") {
                            Icon(
                                Icons.Default.Fingerprint,
                                contentDescription = "Biometrik",
                                tint = DesignTokens.EmeraldAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        } else {
                            Text(
                                text = item,
                                color = if (item == "C") DesignTokens.RoseAccent else DesignTokens.TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
