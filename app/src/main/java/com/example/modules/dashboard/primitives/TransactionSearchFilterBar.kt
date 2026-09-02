package com.example.modules.dashboard.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun TransactionSearchFilterBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedCategoryFilter: String?,
    onCategoryFilterChange: (String?) -> Unit,
    onOpenFilterSheet: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f).height(48.dp),
            placeholder = { Text("Cari transaksi (catatan/nama)...", fontSize = 12.sp, color = DesignTokens.TextMuted) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = DesignTokens.TextSecondary, modifier = Modifier.size(18.dp)) },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = DesignTokens.TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            } else null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DesignTokens.SurfaceElevated,
                unfocusedContainerColor = DesignTokens.Surface,
                focusedBorderColor = DesignTokens.CobaltAccent,
                unfocusedBorderColor = DesignTokens.BorderGlass,
                focusedTextColor = DesignTokens.TextPrimary,
                unfocusedTextColor = DesignTokens.TextPrimary
            )
        )

        IconButton(
            onClick = onOpenFilterSheet,
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = if (selectedCategoryFilter != null) DesignTokens.CobaltAccent else DesignTokens.SurfaceElevated)
        ) {
            Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = DesignTokens.TextPrimary)
        }
    }
}
