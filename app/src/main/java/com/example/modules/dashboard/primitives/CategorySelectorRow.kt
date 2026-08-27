package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared.models.Category
import com.example.shared.theme.DesignTokens

@Composable
fun CategorySelectorRow(
    categories: List<Category>,
    selectedCategoryId: String,
    isIncome: Boolean,
    onSelectCategory: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { c ->
            val isSelected = c.id == selectedCategoryId
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) (if (isIncome) DesignTokens.EmeraldGlow else DesignTokens.AmberAccent) else DesignTokens.Surface)
                    .clickable { onSelectCategory(c.id) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = c.name,
                    color = if (isSelected) Color.White else DesignTokens.TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
