package com.example.modules.dashboard.subscreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.GroupSpendingSummary
import com.example.shared.theme.DesignTokens

@Composable
fun CategoryGroupRowItem(summary: GroupSpendingSummary, isExpenseMode: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    val groupColor = try {
        Color(android.graphics.Color.parseColor(summary.group.colorHex))
    } catch (e: Exception) {
        Color(0xFF3B82F6)
    }

    val amount = if (isExpenseMode) summary.totalExpense else summary.totalIncome
    val pct = if (isExpenseMode) summary.percentOfTotalExpense else summary.percentOfTotalIncome

    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(groupColor.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Text(summary.group.iconName.ifEmpty { "📁" }, fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(summary.group.name, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 14.sp)
                    Text("${summary.categoryItems.size} Kategori", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Rp ${String.format("%,d", amount)}", fontWeight = FontWeight.Bold, color = if (isExpenseMode) DesignTokens.CrimsonAccent else DesignTokens.EmeraldAccent, fontSize = 14.sp)
                    Text("${String.format("%.1f", pct)}%", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = groupColor)
                }
                Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = "Expand", tint = DesignTokens.TextSecondary, modifier = Modifier.padding(start = 4.dp))
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider(color = DesignTokens.SurfaceGlass)
                    summary.categoryItems.forEach { catItem ->
                        val catAmount = if (isExpenseMode) catItem.totalExpense else catItem.totalIncome
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• ${catItem.category.name}", fontSize = 12.sp, color = DesignTokens.TextPrimary)
                            Text("Rp ${String.format("%,d", catAmount)}", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
