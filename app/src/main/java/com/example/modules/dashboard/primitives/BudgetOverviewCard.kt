package com.example.modules.dashboard.primitives

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetOverviewCard(
    monthlyBudget: Long,
    totalExpenses: Long,
    wallets: List<WalletAccount> = emptyList(),
    onEditBudget: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val budgetProgress = if (monthlyBudget > 0L) (totalExpenses.toFloat() / monthlyBudget.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedBudgetProgress by animateFloatAsState(targetValue = budgetProgress, label = "budgetProgress")
    val totalRealBalance = remember(wallets) { wallets.sumOf { it.balance } }
    val remaining = (monthlyBudget - totalExpenses).coerceAtLeast(0L)

    Card(
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Plafond Anggaran Bulanan (Batas Maksimal)", fontSize = 11.sp, color = DesignTokens.TextSecondary, fontWeight = FontWeight.SemiBold)
                    Text(formatter.format(monthlyBudget), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = DesignTokens.TextPrimary)
                }
                IconButton(onClick = onEditBudget) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Anggaran", tint = DesignTokens.CobaltAccent)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Terpakai: ${formatter.format(totalExpenses)}", fontSize = 11.sp, color = if (totalExpenses > monthlyBudget) DesignTokens.RoseAccent else DesignTokens.TextSecondary)
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Sisa Kuota Belanja: ${formatter.format(remaining)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (totalExpenses > monthlyBudget) DesignTokens.RoseAccent else DesignTokens.EmeraldAccent)
                        Text("(Bukan Saldo Kas)", fontSize = 9.sp, color = DesignTokens.TextSecondary)
                    }
                }
                LinearProgressIndicator(
                    progress = { animatedBudgetProgress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = if (budgetProgress >= 0.9f) DesignTokens.RoseAccent else DesignTokens.CobaltAccent,
                    trackColor = DesignTokens.BorderLight,
                )
            }

            Column(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(DesignTokens.BorderLight.copy(alpha = 0.3f)).padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Total Saldo Kas Riil Pasangan", fontSize = 11.sp, color = DesignTokens.TextPrimary, fontWeight = FontWeight.Medium)
                    Text(formatter.format(totalRealBalance), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                }
                
                if (remaining > totalRealBalance) {
                    Text(
                        "⚠️ Sisa kuota belanja (${formatter.format(remaining)}) melebihi uang kas riil (${formatter.format(totalRealBalance)}). Belanjalah berdasarkan kas nyata!",
                        fontSize = 10.sp, color = DesignTokens.RoseAccent, fontWeight = FontWeight.Bold, lineHeight = 14.sp
                    )
                } else {
                    Text("✅ Sisa kuota belanja terjamin aman oleh kas nyata tersedia.", fontSize = 10.sp, color = DesignTokens.EmeraldGlow, fontWeight = FontWeight.Medium, lineHeight = 14.sp)
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (totalExpenses > monthlyBudget) DesignTokens.RoseAccent.copy(alpha = 0.15f) else DesignTokens.EmeraldAccent.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (totalExpenses > monthlyBudget) "⚠️ Pengeluaran melebihi batas anggaran." else "✅ Pengeluaran masih dalam batas sehat dan terkendali.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (totalExpenses > monthlyBudget) DesignTokens.RoseAccent else DesignTokens.EmeraldAccent,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
