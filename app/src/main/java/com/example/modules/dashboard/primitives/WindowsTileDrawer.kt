package com.example.modules.dashboard.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared.models.Category
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WindowsTileDrawer(
    title: String,
    subtitle: String,
    wallets: List<WalletAccount> = emptyList(),
    selectedWalletId: String = "",
    categories: List<Category> = emptyList(),
    selectedCategoryId: String = "",
    isIncome: Boolean = false,
    onSelectWallet: ((String) -> Unit)? = null,
    onSelectCategory: ((String) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var isPersonalizeMode by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DesignTokens.BackgroundBottom,
        dragHandle = {
            Surface(
                modifier = Modifier.padding(top = 8.dp),
                color = DesignTokens.BorderLight,
                shape = RoundedCornerShape(4.dp)
            ) {
                Box(modifier = Modifier.size(width = 36.dp, height = 4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            WindowsTileDrawerHeader(
                title = title,
                subtitle = subtitle,
                isPersonalizeMode = isPersonalizeMode,
                onTogglePersonalizeMode = { isPersonalizeMode = !isPersonalizeMode },
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isPersonalizeMode) {
                WindowsTilePersonalizeGrid()
            } else if (wallets.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 320.dp)
                ) {
                    items(wallets) { w ->
                        val isSelected = w.id == selectedWalletId
                        val icon = WindowsTileIconHelper.getIconForItem(w.name, "Wallet")
                        WindowsIconTile(
                            title = w.name,
                            subtitle = w.type,
                            icon = icon,
                            accentColor = DesignTokens.CobaltAccent,
                            isSelected = isSelected,
                            onClick = {
                                onSelectWallet?.invoke(w.id)
                                onDismiss()
                            }
                        )
                    }
                }
            } else if (categories.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 320.dp)
                ) {
                    items(categories) { c ->
                        val isSelected = c.id == selectedCategoryId
                        val icon = WindowsTileIconHelper.getIconForItem(c.name, "Category")
                        val accentColor = if (isIncome) DesignTokens.EmeraldGlow else DesignTokens.AmberAccent
                        WindowsIconTile(
                            title = c.name,
                            icon = icon,
                            accentColor = accentColor,
                            isSelected = isSelected,
                            onClick = {
                                onSelectCategory?.invoke(c.id)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}
