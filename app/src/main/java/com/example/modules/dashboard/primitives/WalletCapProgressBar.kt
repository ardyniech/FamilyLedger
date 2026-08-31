package com.example.modules.dashboard.primitives

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.TransferCapEvaluation
import com.example.modules.dashboard.logic.TransferCapStatus
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WalletCapProgressBar(
    evaluation: TransferCapEvaluation?,
    modifier: Modifier = Modifier
) {
    if (evaluation == null || evaluation.monthlyCap <= 0L) return
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val pct = (evaluation.currentMonthTransfers.toFloat() / evaluation.monthlyCap.toFloat()).coerceIn(0f, 1f)
    val animatedPct by animateFloatAsState(
        targetValue = pct,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "cap_bar"
    )

    val barColor = when (evaluation.status) {
        TransferCapStatus.EXCEEDED -> DesignTokens.RoseAccent
        TransferCapStatus.NEAR_LIMIT -> DesignTokens.AmberAccent
        TransferCapStatus.SAFE -> DesignTokens.EmeraldAccent
    }

    Column(modifier = modifier.fillMaxWidth().padding(top = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Terpakai: ${formatter.format(evaluation.currentMonthTransfers)}",
                color = DesignTokens.TextSecondary,
                fontSize = 11.sp
            )
            Text(
                "${String.format(Locale.US, "%.0f", evaluation.percentageUsed)}%",
                color = barColor,
                fontSize = 11.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(DesignTokens.Surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedPct)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )
        }
    }
}
