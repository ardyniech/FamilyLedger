package com.example.modules.dashboard.subscreens

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
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun DebtTrackerSummaryCard(
    monthlyInstallmentSum: Long,
    totalRemainingLoan: Long,
    onOpenEarlyPayoffSimulator: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Kewajiban Cicilan / Bulan", color = DesignTokens.TextSecondary, fontSize = 11.sp)
                    Text(MathUtils.formatRupiah(monthlyInstallmentSum), color = DesignTokens.AmberAccent, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Sisa Pokok Pinjaman", color = DesignTokens.TextSecondary, fontSize = 11.sp)
                    Text(MathUtils.formatRupiah(totalRemainingLoan), color = DesignTokens.RoseAccent, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
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
}
