package com.example.modules.dashboard.management

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CategoryListItem(
    cat: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().clickable { onEdit() }.testTag("category_card_${cat.name}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(
                        if (cat.type == "Expense") DesignTokens.RoseAccent.copy(alpha = 0.15f) else DesignTokens.EmeraldGlow.copy(alpha = 0.15f)
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        color = if (cat.type == "Expense") DesignTokens.RoseAccent else DesignTokens.EmeraldGlow
                    )
                }
                Column {
                    Text(text = cat.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = DesignTokens.TextPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = if (cat.type == "Expense") "Keluaran" else "Pemasukan", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                        if (cat.type == "Expense" && cat.budgetLimit > 0) {
                            val fmt = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
                            Text(text = "• Batas: ${fmt.format(cat.budgetLimit)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.AmberAccent)
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Kategori", tint = DesignTokens.TextSecondary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("delete_category_icon_${cat.name}")) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Hapus Kategori", tint = DesignTokens.RoseAccent, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
