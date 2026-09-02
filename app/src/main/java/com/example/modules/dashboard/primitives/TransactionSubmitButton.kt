package com.example.modules.dashboard.primitives

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.atoms.springClickable
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun TransactionSubmitButton(
    amount: String,
    note: String,
    isIncome: Boolean = false,
    isLoading: Boolean = false,
    onValidatedSubmit: (Long) -> Unit
) {
    val context = LocalContext.current

    fun cleanExpression(str: String): String {
        var cleaned = str.trim()
        while (cleaned.endsWith("+") || cleaned.endsWith("-") || cleaned.endsWith("*") || cleaned.endsWith("/")) {
            cleaned = cleaned.dropLast(1).trim()
        }
        return cleaned
    }

    val cleanedAmount = cleanExpression(amount)
    val evaluatedValue = if (cleanedAmount.isNotEmpty()) MathUtils.evaluateMath(cleanedAmount) else null
    val isValidAmount = evaluatedValue != null && evaluatedValue.isFinite() && evaluatedValue > 0

    val btnGradient = if (isLoading) {
        listOf(DesignTokens.BorderLight, DesignTokens.BorderLight)
    } else if (isValidAmount) {
        if (isIncome) listOf(DesignTokens.EmeraldGlow, DesignTokens.CobaltDark)
        else listOf(DesignTokens.CobaltDark, DesignTokens.CobaltAccent)
    } else {
        listOf(DesignTokens.SurfaceElevated, DesignTokens.SurfaceCard)
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent,
        modifier = Modifier
            .testTag("transaction_submit_button")
            .fillMaxWidth()
            .height(52.dp)
            .background(Brush.horizontalGradient(btnGradient), RoundedCornerShape(14.dp))
            .springClickable {
                if (isLoading) return@springClickable
                if (cleanedAmount.isEmpty()) {
                    Toast.makeText(context, "Harap masukkan nominal transaksi terlebih dahulu", Toast.LENGTH_SHORT).show()
                    return@springClickable
                }
                if (evaluatedValue == null || !evaluatedValue.isFinite() || evaluatedValue <= 0) {
                    Toast.makeText(context, "Nominal transaksi tidak valid (harus > 0)", Toast.LENGTH_SHORT).show()
                    return@springClickable
                }
                onValidatedSubmit(evaluatedValue.toLong())
            }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(color = DesignTokens.CobaltAccent, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.Check, contentDescription = "Save", tint = if (isValidAmount) Color.White else DesignTokens.TextSecondary, modifier = Modifier.size(20.dp))
                    Text(
                        text = if (isValidAmount) {
                            val formatted = MathUtils.formatRupiah(evaluatedValue!!.toLong())
                            "SIMPAN TRANSAKSI • $formatted"
                        } else {
                            "KONFIRMASI & SIMPAN"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isValidAmount) Color.White else DesignTokens.TextSecondary
                    )
                }
            }
        }
    }
}
