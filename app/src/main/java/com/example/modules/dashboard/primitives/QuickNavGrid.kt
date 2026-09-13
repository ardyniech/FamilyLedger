package com.example.modules.dashboard.primitives

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

data class QuickNavItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val action: () -> Unit
)

@Composable
fun QuickNavGrid(
    items: List<QuickNavItem>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { item.action() },
                colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DesignTokens.CobaltAccent.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = item.icon, contentDescription = item.title, tint = DesignTokens.CobaltAccent, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                        Text(item.subtitle, fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    }
                }
            }
        }
    }
}
