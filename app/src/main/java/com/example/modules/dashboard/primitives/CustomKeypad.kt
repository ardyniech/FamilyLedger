package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun CustomKeypad(onKeyPress: (String) -> Unit) {
    val keys = listOf(
        listOf("C", "Del", "/", "*"),
        listOf("7", "8", "9", "-"),
        listOf("4", "5", "6", "+"),
        listOf("1", "2", "3", "="),
        listOf("000", "0", ".", "")
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1.5f))
                    } else {
                        val isOperator = key in listOf("/", "*", "-", "+", "=")
                        val isAction = key in listOf("C", "Del")
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.5f)
                                .clip(RoundedCornerShape(DesignTokens.PaddingMedium))
                                .background(if (isOperator) DesignTokens.CobaltAccent.copy(alpha = 0.15f) else DesignTokens.Surface)
                                .clickable { onKeyPress(key) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                color = if (isAction) Color.Red else if (isOperator) DesignTokens.CobaltAccent else DesignTokens.TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
