package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MonthlyOverviewCard(
    totalExpenses: Long,
    budget: Long = 5000000L,
    onClick: () -> Unit
) {
    val currentMonth = remember { SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date()) }
    val progress = if (budget > 0L) (totalExpenses.toFloat() / budget.toFloat()).coerceIn(0f, 1f) else 0f
    val percentage = (progress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Batas Anggaran Bulanan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DesignTokens.TextSecondary
                    )
                    Text(
                        text = "Ringkasan $currentMonth",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DesignTokens.TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DesignTokens.CobaltAccent.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$percentage% Terpakai",
                        color = DesignTokens.CobaltAccent,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(DesignTokens.BorderLight)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(DesignTokens.CobaltAccent, DesignTokens.AmberAccent)
                                )
                            )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Terpakai: ${MathUtils.formatRupiah(totalExpenses)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (progress > 0.85f) DesignTokens.RoseAccent else DesignTokens.TextPrimary
                    )
                    Text(
                        text = "Anggaran: ${MathUtils.formatRupiah(budget)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = DesignTokens.TextSecondary
                    )
                }
            }
        }
    }
}
