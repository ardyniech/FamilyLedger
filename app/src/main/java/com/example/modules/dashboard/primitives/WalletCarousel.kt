package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared.models.Member
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

import com.example.shared.atoms.springClickable

@Composable
fun WalletCarousel(wallets: List<WalletAccount>, members: List<Member>, onWalletClick: (String) -> Unit) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(DesignTokens.PaddingMedium)
    ) {
        items(wallets) { wallet ->
            val member = members.find { it.id == wallet.memberId }
            val tint = if (member?.role == "Husband") DesignTokens.CobaltAccent else DesignTokens.AmberAccent
            
            Card(
                modifier = Modifier
                    .width(160.dp)
                    .height(100.dp)
                    .springClickable { onWalletClick(wallet.id) },
                shape = RoundedCornerShape(DesignTokens.PaddingMedium),
                colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(DesignTokens.PaddingMedium),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(wallet.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(tint)
                        )
                    }
                    Text(
                        text = formatter.format(wallet.balance),
                        style = MaterialTheme.typography.titleMedium,
                        color = DesignTokens.TextPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
