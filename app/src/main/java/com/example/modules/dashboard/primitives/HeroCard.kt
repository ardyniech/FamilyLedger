package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared.models.Member
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HeroCard(totalBalance: Double, wallets: List<WalletAccount>, members: List<Member>, onClick: () -> Unit) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val husbandTotal = wallets.filter { w -> members.find { it.id == w.memberId }?.role == "Husband" }.sumOf { it.balance }
    val wifeTotal = wallets.filter { w -> members.find { it.id == w.memberId }?.role == "Wife" }.sumOf { it.balance }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(DesignTokens.CobaltAccent, Color(0xFF0284C7))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(DesignTokens.PaddingLarge),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Household Net Worth",
                    color = DesignTokens.TextOnDark.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatter.format(totalBalance),
                    color = DesignTokens.TextOnDark,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Suami", color = DesignTokens.TextOnDark.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                        Text(formatter.format(husbandTotal), color = DesignTokens.TextOnDark)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Istri", color = DesignTokens.TextOnDark.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                        Text(formatter.format(wifeTotal), color = DesignTokens.TextOnDark)
                    }
                }
            }
        }
    }
}
