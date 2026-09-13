package com.example.shared.atoms

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun UniversalAmountDisplay(
    rawAmount: String,
    isIncome: Boolean,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 28.sp,
    textAlign: TextAlign = TextAlign.Center
) {
    val parsedEval = remember(rawAmount) {
        if (rawAmount.isBlank()) null else MathUtils.evaluateMath(rawAmount)
    }

    val displayAmountText = remember(rawAmount, parsedEval) {
        if (rawAmount.isEmpty()) "Rp 0"
        else if (parsedEval != null && parsedEval.isFinite() && parsedEval > 0) MathUtils.formatRupiah(parsedEval.toLong())
        else "Rp $rawAmount"
    }

    AnimatedContent(
        targetState = displayAmountText,
        transitionSpec = {
            (fadeIn(animationSpec = tween(90)) + scaleIn(initialScale = 0.96f, animationSpec = tween(90)))
                .togetherWith(fadeOut(animationSpec = tween(70)))
        },
        label = "UniversalAmountAnimation",
        modifier = modifier
    ) { targetText ->
        Text(
            text = targetText,
            fontSize = fontSize,
            color = if (isIncome) DesignTokens.EmeraldGlow else DesignTokens.RoseAccent,
            fontWeight = FontWeight.ExtraBold,
            textAlign = textAlign,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
