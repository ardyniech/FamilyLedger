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
import com.example.modules.dashboard.logic.PortfolioLoanProgress
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun BankLoanProgressHeader(
    portfolio: PortfolioLoanProgress,
    modifier: Modifier = Modifier
) {
    val animatedRatio by animateFloatAsState(
        targetValue = portfolio.overallPaidRatio,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "portfolio_paid_ratio"
    )

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Progress Pelunasan KPR & Bank", color = DesignTokens.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("${portfolio.activeLoansCount} Pinjaman Aktif Terpantau", color = DesignTokens.TextSecondary, fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DesignTokens.CobaltAccent.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "%.1f%% Lunas".format(portfolio.overallPaidPercentage),
                    color = DesignTokens.CobaltAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(DesignTokens.BorderLight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedRatio)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        Brush.horizontalGradient(listOf(DesignTokens.CobaltAccent, DesignTokens.EmeraldGlow))
                    )
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Sisa Pokok Pinjaman", color = DesignTokens.TextSecondary, fontSize = 10.sp)
                Text(MathUtils.formatRupiah(portfolio.totalRemaining), color = DesignTokens.RoseAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Total Pokok Terbayar", color = DesignTokens.TextSecondary, fontSize = 10.sp)
                Text(MathUtils.formatRupiah(portfolio.totalPaid), color = DesignTokens.EmeraldGlow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
