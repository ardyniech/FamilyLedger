package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun TransactionSubmitButton(
    amount: String,
    note: String,
    onValidatedSubmit: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(DesignTokens.CornerRadius))
            .background(Brush.linearGradient(listOf(DesignTokens.CobaltDark, DesignTokens.EmeraldGlow)))
            .clickable {
                if (amount.isNotEmpty() && note.isNotEmpty()) {
                    MathUtils.evaluateMath(amount)?.let { result ->
                        if (result.isFinite() && result > 0) {
                            onValidatedSubmit(result.toLong())
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text("Konfirmasi & Simpan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
