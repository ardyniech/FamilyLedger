package com.example.modules.dashboard.primitives

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.theme.DesignTokens

@Composable
fun CategorySuggestionChipRow(
    suggestions: List<Category>,
    selectedCategoryId: String,
    onSelectCategory: (String) -> Unit
) {
    if (suggestions.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = "AI", tint = DesignTokens.CobaltAccent, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Saran Kategori (AI):", color = DesignTokens.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            suggestions.forEach { cat ->
                val isSelected = cat.id == selectedCategoryId
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectCategory(cat.id) },
                    label = { Text(cat.name, fontSize = 11.sp) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DesignTokens.CobaltAccent,
                        selectedLabelColor = DesignTokens.TextPrimary,
                        containerColor = DesignTokens.SurfaceElevated,
                        labelColor = DesignTokens.TextSecondary
                    )
                )
            }
        }
    }
}
