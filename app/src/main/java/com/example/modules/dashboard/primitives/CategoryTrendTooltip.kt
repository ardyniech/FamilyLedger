package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.MonthlyCategoryPoint
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat

@Composable
fun CategoryTrendTooltip(
    pt: MonthlyCategoryPoint,
    selectedCategoryId: String?,
    expenseCategories: List<Category>,
    currencyFmt: NumberFormat
) {
    val colors = listOf(DesignTokens.CobaltAccent, DesignTokens.AmberAccent, DesignTokens.RoseAccent, DesignTokens.EmeraldGlow, Color(0xFF8B5CF6))
    val activeCats = if (selectedCategoryId != null) expenseCategories.filter { it.id == selectedCategoryId } else expenseCategories.take(4)

    Card(colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass), border = BorderStroke(1.dp, DesignTokens.BorderGlass), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Rincian ${pt.monthLabel}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DesignTokens.TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            activeCats.forEachIndexed { idx, cat ->
                val amount = pt.categoryAmounts[cat.id] ?: 0.0
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(colors[idx % colors.size]))
                        Text(text = cat.name, fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    }
                    Text(text = currencyFmt.format(amount), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextPrimary)
                }
            }
            val totalMonth = if (selectedCategoryId != null) pt.categoryAmounts[selectedCategoryId] ?: 0.0 else pt.categoryAmounts.values.sum()
            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = DesignTokens.BorderGlass)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total Terpilih", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                Text(text = currencyFmt.format(totalMonth), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
            }
        }
    }
}
