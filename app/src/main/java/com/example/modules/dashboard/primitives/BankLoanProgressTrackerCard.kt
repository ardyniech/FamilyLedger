package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.modules.dashboard.logic.PortfolioLoanProgress
import com.example.shared.theme.DesignTokens

@Composable
fun BankLoanProgressTrackerCard(
    portfolio: PortfolioLoanProgress,
    onOpenEarlyPayoffSimulator: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BankLoanProgressHeader(portfolio = portfolio)
            BankLoanNextDueBanner(
                nextLoan = portfolio.nextUpcomingLoanProgress,
                onOpenEarlyPayoffSimulator = onOpenEarlyPayoffSimulator
            )
        }
    }
}
