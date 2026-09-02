package com.example.modules.dashboard.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.DebtRecord
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun DebtItemCard(
    debt: DebtRecord,
    onPay: (Long) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(debt.personName, color = DesignTokens.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(if (debt.isHutang) "Hutang (Saya Utang)" else "Piutang (Orang Utang)", color = if (debt.isHutang) DesignTokens.RoseAccent else DesignTokens.EmeraldGlow, fontSize = 11.sp)
                }
                Text(MathUtils.formatRupiah(debt.amount), color = DesignTokens.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { if (debt.amount > 0) debt.paidAmount.toFloat() / debt.amount else 0f },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = DesignTokens.EmeraldGlow,
                trackColor = DesignTokens.BorderGlass
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Sisa: ${MathUtils.formatRupiah(debt.remainingAmount)}", color = DesignTokens.TextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!debt.isSettled) {
                        Button(
                            onClick = { onPay(debt.remainingAmount) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
                        ) {
                            Text("Bayar/Lunas", fontSize = 11.sp, color = DesignTokens.TextPrimary)
                        }
                    } else {
                        Text("LUNAS", color = DesignTokens.EmeraldGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
