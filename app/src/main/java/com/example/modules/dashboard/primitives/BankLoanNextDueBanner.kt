package com.example.modules.dashboard.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.SingleLoanProgress
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun BankLoanNextDueBanner(
    nextLoan: SingleLoanProgress?,
    onOpenEarlyPayoffSimulator: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        nextLoan?.let { loan ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DesignTokens.BackgroundBottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Cicilan Terdekat: ${loan.debt.personName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DesignTokens.TextPrimary
                        )
                        Text(
                            text = "${MathUtils.formatRupiah(loan.debt.monthlyInstallment)} / bln",
                            fontSize = 10.sp,
                            color = DesignTokens.AmberAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    DueDateUrgencyBadge(dueDateInfo = loan.dueDateInfo)
                }
            }
        }

        Button(
            onClick = onOpenEarlyPayoffSimulator,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.EmeraldAccent.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Calculate, contentDescription = "Simulasi", tint = DesignTokens.EmeraldAccent, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Simulasi Pelunasan Dipercepat & Hemat Bunga", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldAccent)
        }
    }
}
