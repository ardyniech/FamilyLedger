package com.example.shared.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Member
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun UniversalWalletChipSelector(
    wallets: List<WalletAccount>,
    selectedWalletId: String,
    onSelectWallet: (String) -> Unit,
    members: List<Member> = emptyList(),
    activeAccentColor: Color = DesignTokens.CobaltAccent,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        wallets.forEach { wallet ->
            val isSelected = wallet.id == selectedWalletId
            val owner = members.find { it.id == wallet.memberId }
            val ownerName = owner?.name?.ifBlank { owner.role } ?: ""

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) activeAccentColor else DesignTokens.SurfaceCard)
                    .clickable { onSelectWallet(wallet.id) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = wallet.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else DesignTokens.TextPrimary
                    )
                    Text(
                        text = if (ownerName.isNotBlank()) "$ownerName • ${MathUtils.formatRupiah(wallet.balance)}" else MathUtils.formatRupiah(wallet.balance),
                        fontSize = 10.sp,
                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else DesignTokens.TextSecondary
                    )
                }
            }
        }
    }
}
