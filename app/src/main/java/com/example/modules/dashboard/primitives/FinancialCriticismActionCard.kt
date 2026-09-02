package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.PeriodSummary
import com.example.shared.atoms.springClickable
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils
import kotlin.math.abs

@Composable
fun FinancialCriticismActionCard(
    summary: PeriodSummary,
    transactions: List<Transaction>,
    categories: List<Category>,
    onAnalyticsClick: () -> Unit,
    onGoalsClick: () -> Unit,
    onViewAllExpensesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val critiqueData = remember(summary, transactions, categories) {
        val income = summary.totalInflow
        val expense = summary.totalOutflow
        val net = income - expense
        val savingsRate = if (income > 0) (net.toDouble() / income.toDouble() * 100.0) else 0.0

        val expenseTx = transactions.filter { it.amount < 0 }
        val topCategory = expenseTx.groupBy { it.categoryId }
            .maxByOrNull { entry -> entry.value.sumOf { abs(it.amount) } }
            ?.let { entry ->
                val catName = categories.find { it.id == entry.key }?.name ?: "Pengeluaran"
                val catSum = entry.value.sumOf { abs(it.amount) }
                val pct = if (expense > 0) (catSum.toDouble() / expense.toDouble() * 100.0).toInt() else 0
                Pair(catName, pct)
            }

        val mainCriticism = when {
            income == 0L && expense == 0L -> "💡 Belum ada data transaksi tercatat untuk periode ini. Mulai catat pengeluaran Anda."
            net < 0 -> "⚠️ DEFISIT: Pengeluaran melampaui pemasukan sebesar ${MathUtils.formatRupiah(-net)}. Evaluasi pos belanja tersier!"
            savingsRate < 15.0 -> "🔥 TABUNGAN RENDAH: Baru ${String.format("%.1f", savingsRate)}% pemasukan yang disisihkan. Target ideal minimal 20%."
            else -> "💪 FINANSIAL SEHAT: Anda berhasil menabung ${String.format("%.1f", savingsRate)}% (${MathUtils.formatRupiah(net)}) dari total pemasukan."
        }

        val secondaryTip = topCategory?.let { (name, pct) ->
            if (pct >= 30) "📌 Konsentrasi Tinggi: $pct% dana terserap di kategori $name." else null
        }

        Triple(mainCriticism, secondaryTip, net >= 0)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🤖", fontSize = 18.sp)
                    Text("Analitik & Kritik Keuangan", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 14.sp)
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        .background(if (critiqueData.third) DesignTokens.EmeraldGlow.copy(alpha = 0.15f) else DesignTokens.CrimsonAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (critiqueData.third) "SURPLUS" else "PERHATIAN",
                        color = if (critiqueData.third) DesignTokens.EmeraldGlow else DesignTokens.CrimsonAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Text(text = critiqueData.first, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DesignTokens.TextPrimary, lineHeight = 18.sp)

            critiqueData.second?.let { tip ->
                Text(text = tip, fontSize = 12.sp, color = DesignTokens.TextSecondary)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionBtn(label = "Analitik", icon = Icons.Default.Analytics, onClick = onAnalyticsClick, modifier = Modifier.weight(1f))
                ActionBtn(label = "Target", icon = Icons.Default.Savings, onClick = onGoalsClick, modifier = Modifier.weight(1f))
                ActionBtn(label = "Riwayat", icon = Icons.Default.ReceiptLong, onClick = onViewAllExpensesClick, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ActionBtn(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DesignTokens.SurfaceElevated,
        border = BorderStroke(1.dp, DesignTokens.BorderLight),
        modifier = modifier.height(38.dp).springClickable { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = label, tint = DesignTokens.CobaltAccent, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
        }
    }
}
