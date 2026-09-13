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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.SingleLoanProgress
import com.example.shared.theme.DesignTokens

@Composable
fun LoanProgressBar(
    progress: SingleLoanProgress,
    modifier: Modifier = Modifier
) {
    val animatedRatio by animateFloatAsState(
        targetValue = progress.paidRatio,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "loan_paid_progress"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progress Pelunasan Pokok",
                color = DesignTokens.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "%.1f%% Terbayar".format(progress.paidPercentage),
                color = if (progress.isSettled) DesignTokens.EmeraldGlow else DesignTokens.EmeraldAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DesignTokens.BorderLight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedRatio)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(DesignTokens.CobaltAccent, DesignTokens.EmeraldGlow)
                        )
                    )
            )
        }

        if (progress.tenorTotalMonths > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Bulan ke-${progress.tenorMonthsElapsed} dari ${progress.tenorTotalMonths} bln",
                    color = DesignTokens.TextSecondary,
                    fontSize = 10.sp
                )
                Text(
                    text = "Sisa ${progress.tenorRemainingMonths} bln (%.0f%% waktu)".format(progress.tenorProgressPercentage),
                    color = DesignTokens.TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
