package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CategoryBreakdownReportCard(
    transactions: List<Transaction>,
    categories: List<Category>,
    totalExpenses: Long,
    onCategoryClick: ((Category) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    val categoryTotals = categories
        .filter { it.type == "Expense" }
        .map { cat ->
            val catTxs = transactions.filter { it.categoryId == cat.id && it.amount < 0 }
            val sum = catTxs.sumOf { -it.amount }
            val count = catTxs.size
            Triple(cat, sum, count)
        }
        .filter { it.second > 0L }
        .sortedByDescending { it.second }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
    ) {
        Column(
            modifier = Modifier.padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Rincian Kategori Pengeluaran",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DesignTokens.TextPrimary
            )

            if (categoryTotals.isEmpty()) {
                Text("Belum ada kategori pengeluaran bulan ini.", fontSize = 12.sp, color = DesignTokens.TextSecondary)
            } else {
                categoryTotals.forEach { (cat, sum, count) ->
                    val percentage = if (totalExpenses > 0L) (sum.toFloat() / totalExpenses.toFloat()) else 0f

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (onCategoryClick != null) Modifier.clickable { onCategoryClick(cat) } else Modifier),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${cat.name} (${count}x)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = DesignTokens.TextPrimary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = fmt.format(sum),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DesignTokens.TextPrimary
                                )
                                Text(
                                    text = "${(percentage * 100).toInt()}%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = DesignTokens.CobaltAccent
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { percentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = DesignTokens.CobaltAccent,
                            trackColor = DesignTokens.SurfaceGlass
                        )
                    }
                }
            }
        }
    }
}
