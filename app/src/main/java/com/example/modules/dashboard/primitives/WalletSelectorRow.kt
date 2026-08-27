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
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens

@Composable
fun WalletSelectorRow(
    wallets: List<WalletAccount>,
    selectedWalletId: String,
    onSelectWallet: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        wallets.forEach { w ->
            val isSelected = w.id == selectedWalletId
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) DesignTokens.CobaltAccent else DesignTokens.Surface)
                    .clickable { onSelectWallet(w.id) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = w.name,
                    color = if (isSelected) Color.White else DesignTokens.TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
