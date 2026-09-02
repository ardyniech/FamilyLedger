package com.example.modules.dashboard.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun WindowsTilePersonalizeGrid() {
    Column {
        Text(
            text = "Personalization (Koleksi Icon Windows OS):",
            color = DesignTokens.CobaltAccent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.height(280.dp)
        ) {
            items(WindowsTileIconHelper.presetIcons) { preset ->
                WindowsIconTile(
                    title = preset.label.split(" ").first(),
                    icon = preset.icon,
                    accentColor = preset.tileColor,
                    isSelected = false,
                    onClick = {}
                )
            }
        }
    }
}
