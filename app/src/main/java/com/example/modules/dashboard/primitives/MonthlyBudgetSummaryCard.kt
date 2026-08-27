package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MonthlyBudgetSummaryCard(
    remainingBudget: Double,
    totalExpenses: Double,
    budget: Double,
    progress: Float,
    wallets: List<WalletAccount> = emptyList(),
    onEditBudget: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val totalRealBalance = remember(wallets) { wallets.sumOf { it.balance } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
    ) {
        Column(
            modifier = Modifier.padding(DesignTokens.PaddingLarge),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Sisa Kuota Belanja Bulanan", color = DesignTokens.TextSecondary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = formatter.format(remainingBudget),
                        color = DesignTokens.EmeraldGlow,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text("⚠️ Batas Limit, Bukan Uang Kas", fontSize = 10.sp, color = DesignTokens.TextSecondary, fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DesignTokens.EmeraldGlow.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${((1f - progress) * 100).toInt()}% Sisa",
                        color = DesignTokens.EmeraldGlow,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Real Cash Comparison Box
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DesignTokens.BorderLight.copy(alpha = 0.25f))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Uang Kas Riil Tersedia (Gabungan)", fontSize = 11.sp, color = DesignTokens.TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text(formatter.format(totalRealBalance), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                }
                
                if (remainingBudget > totalRealBalance) {
                    Text(
                        text = "⚠️ Sisa kuota belanja bulanan (${formatter.format(remainingBudget)}) lebih tinggi dari saldo kas nyata (${formatter.format(totalRealBalance)}). Pastikan untuk tidak berbelanja melebihi uang tunai yang ada!",
                        fontSize = 10.sp,
                        color = DesignTokens.RoseAccent,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 14.sp
                    )
                } else {
                    Text(
                        text = "✅ Saldo kas riil aman dan mencukupi sisa kuota belanja bulanan Anda.",
                        fontSize = 10.sp,
                        color = DesignTokens.EmeraldGlow,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(DesignTokens.BorderLight))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Terpakai", color = DesignTokens.TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text(formatter.format(totalExpenses), fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onEditBudget() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Total Anggaran ✏️", color = DesignTokens.TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text(formatter.format(budget), fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                }
            }
        }
    }
}
