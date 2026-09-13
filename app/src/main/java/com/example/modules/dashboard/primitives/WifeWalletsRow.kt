package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette
import com.example.shared.utils.MathUtils

@Composable
fun WifeWalletsRow(
    wallets: List<WalletAccount>,
    wifeMemberId: String?,
    onWalletClick: (String) -> Unit
) {
    val wifeWallets = wallets.filter { it.memberId == wifeMemberId || it.id.contains("deina") || it.id.contains("dapur") }
    val palette = RoleThemePalette.istri()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "👛 Dompet Belanja Bunda (${wifeWallets.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DesignTokens.TextPrimary
            )
            Text(
                text = "Pisah kas pasar & online",
                fontSize = 11.sp,
                color = DesignTokens.TextSecondary
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
        ) {
            items(wifeWallets) { wallet ->
                val emoji = when (wallet.type.lowercase()) {
                    "cash" -> "💵"
                    "bank" -> "🏦"
                    "e-wallet" -> "📱"
                    else -> "🏺"
                }

                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .clickable { onWalletClick(wallet.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated),
                    border = BorderStroke(1.dp, palette.cardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                            Text(
                                text = wallet.type,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.primaryAccent
                            )
                        }
                        Text(
                            text = wallet.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DesignTokens.TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = MathUtils.formatRupiah(wallet.balance),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DesignTokens.TextPrimary
                        )
                    }
                }
            }
        }
    }
}
