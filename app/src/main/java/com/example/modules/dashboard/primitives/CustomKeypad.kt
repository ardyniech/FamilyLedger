package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.semantics
import com.example.shared.atoms.springClickable
import com.example.shared.theme.DesignTokens

private val KEYPAD_ROWS = listOf(
    listOf("C", "Del", "/", "*"),
    listOf("7", "8", "9", "-"),
    listOf("4", "5", "6", "+"),
    listOf("1", "2", "3", "="),
    listOf("000", "0", ".", "")
)

@Composable
fun CustomKeypad(
    onKeyPress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val currentOnKeyPress = rememberUpdatedState(onKeyPress)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        KEYPAD_ROWS.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(2.2f))
                    } else {
                        val isOperator = key in listOf("/", "*", "-", "+", "=")
                        val isAction = key in listOf("C", "Del")

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(2.2f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isOperator) DesignTokens.CobaltAccent.copy(alpha = 0.15f) else DesignTokens.Surface)
                                .semantics(mergeDescendants = true) {}
                                .springClickable {
                                    try {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    } catch (_: Exception) {}
                                    currentOnKeyPress.value(key)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                color = if (isAction) Color.Red else if (isOperator) DesignTokens.CobaltAccent else DesignTokens.TextPrimary,
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
