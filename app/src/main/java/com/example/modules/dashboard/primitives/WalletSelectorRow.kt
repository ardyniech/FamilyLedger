package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens

@Composable
fun WalletSelectorRow(
    wallets: List<WalletAccount>,
    selectedWalletId: String,
    onSelectWallet: (String) -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    val selectedWallet = wallets.find { it.id == selectedWalletId }
    val selectedIcon = selectedWallet?.name?.let { WindowsTileIconHelper.getIconForItem(it, "Wallet") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Akun / Sumber Dana",
                color = DesignTokens.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenDrawer() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Grid3x3,
                    contentDescription = "Grid",
                    tint = DesignTokens.CobaltAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Windows Icon Grid",
                    color = DesignTokens.CobaltAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            wallets.forEach { w ->
                val isSelected = w.id == selectedWalletId
                val icon = WindowsTileIconHelper.getIconForItem(w.name, "Wallet")
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) DesignTokens.CobaltAccent else DesignTokens.Surface)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) DesignTokens.CobaltAccent else DesignTokens.BorderGlass,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectWallet(w.id) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = w.name,
                        tint = if (isSelected) Color.White else DesignTokens.CobaltAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = w.name,
                        color = if (isSelected) Color.White else DesignTokens.TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
