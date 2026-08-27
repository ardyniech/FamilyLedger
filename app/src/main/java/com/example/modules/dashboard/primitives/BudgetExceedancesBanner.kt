package com.example.modules.dashboard.primitives

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.CategoryExceedance
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetExceedancesBanner(
    exceedances: List<CategoryExceedance>,
    modifier: Modifier = Modifier
) {
    if (exceedances.isEmpty()) return

    val fmt = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEF2F2) // Light pastel red background
        ),
        border = BorderStroke(1.dp, DesignTokens.RoseAccent.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        modifier = modifier
            .fillMaxWidth()
            .testTag("budget_exceedance_banner")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(DesignTokens.RoseAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚠️", fontSize = 16.sp)
                }
                Text(
                    text = "Batas Anggaran Terlewati!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DesignTokens.RoseAccent
                )
            }

            exceedances.take(3).forEach { exceedance ->
                val overspent = exceedance.currentSpent - exceedance.budgetLimit
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kategori: ${exceedance.category.name}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = DesignTokens.TextPrimary
                        )
                        Text(
                            text = "Lebih: ${fmt.format(overspent)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = DesignTokens.RoseAccent
                        )
                    }

                    // Spend Progress Visual
                    val ratio = (exceedance.currentSpent / exceedance.budgetLimit).toFloat().coerceAtLeast(1.0f)
                    val progressValue = (exceedance.budgetLimit / exceedance.currentSpent).toFloat().coerceIn(0f, 1f)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { progressValue },
                            color = DesignTokens.AmberAccent,
                            trackColor = DesignTokens.RoseAccent,
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Text(
                            text = "${(ratio * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DesignTokens.RoseAccent
                        )
                    }
                    Text(
                        text = "Terpakai ${fmt.format(exceedance.currentSpent)} dari limit bulanan ${fmt.format(exceedance.budgetLimit)}",
                        fontSize = 10.sp,
                        color = DesignTokens.TextSecondary
                    )
                }
            }

            if (exceedances.size > 3) {
                Text(
                    text = "+ ${exceedances.size - 3} kategori pengeluaran lainnya juga melebihi batas!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = DesignTokens.TextSecondary
                )
            }
        }
    }
}
