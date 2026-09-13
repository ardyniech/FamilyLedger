package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.LoanProgressCalculator
import com.example.shared.models.DebtRecord
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun DebtItemCard(
    debt: DebtRecord,
    onPay: (Long) -> Unit,
    onDelete: () -> Unit,
    onSimulatePayoff: (String) -> Unit = {}
) {
    val progress = remember(debt) { LoanProgressCalculator.calculateSingle(debt) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (debt.isBankLoanOrInstallment) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(DesignTokens.CobaltAccent.copy(alpha = 0.2f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text(debt.loanType.label, color = DesignTokens.CobaltAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(if (debt.institutionName.isNotBlank()) debt.institutionName else if (debt.isHutang) "Hutang" else "Piutang", color = DesignTokens.TextSecondary, fontSize = 11.sp)
                    }
                    Text(debt.personName, color = DesignTokens.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (debt.isBankLoanOrInstallment && !debt.isSettled) {
                        IconButton(onClick = { onSimulatePayoff(debt.id) }, modifier = Modifier.size(26.dp)) {
                            Icon(Icons.Filled.Calculate, contentDescription = "Simulasi", tint = DesignTokens.EmeraldAccent, modifier = Modifier.size(18.dp))
                        }
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = DesignTokens.TextSecondary.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (debt.isBankLoanOrInstallment && debt.monthlyInstallment > 0L) {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DesignTokens.BackgroundBottom).padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Cicilan Bulanan", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                        Text(MathUtils.formatRupiah(debt.monthlyInstallment), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.AmberAccent)
                    }
                    DueDateUrgencyBadge(dueDateInfo = progress.dueDateInfo)
                }
            }

            LoanProgressBar(progress = progress)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Sisa Pokok", color = DesignTokens.TextSecondary, fontSize = 10.sp)
                    Text(MathUtils.formatRupiah(debt.remainingAmount), color = DesignTokens.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!debt.isSettled) {
                        val payAmt = if (debt.monthlyInstallment in 1 until debt.remainingAmount) debt.monthlyInstallment else debt.remainingAmount
                        Button(
                            onClick = { onPay(payAmt) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
                        ) {
                            Text(if (debt.monthlyInstallment in 1 until debt.remainingAmount) "Bayar 1 Bln" else "Lunas", fontSize = 11.sp, color = Color.White)
                        }
                    } else {
                        Text("LUNAS", color = DesignTokens.EmeraldGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
