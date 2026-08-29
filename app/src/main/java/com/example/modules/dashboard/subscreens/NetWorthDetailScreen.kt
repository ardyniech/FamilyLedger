package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
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
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetWorthDetailScreen(
    totalBalance: Long,
    wallets: List<WalletAccount>,
    members: List<Member>,
    onBack: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val husbandTotal = wallets.filter { w -> members.find { it.id == w.memberId }?.role == "Husband" }.sumOf { it.balance }
    val wifeTotal = wallets.filter { w -> members.find { it.id == w.memberId }?.role == "Wife" }.sumOf { it.balance }
    val totalSum = (husbandTotal + wifeTotal).coerceAtLeast(1L)
    val husbandRatio = (husbandTotal.toFloat() / totalSum.toFloat())
    val wifeRatio = (wifeTotal.toFloat() / totalSum.toFloat())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Household Net Worth", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DesignTokens.TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.PaddingMedium)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(DesignTokens.CornerRadius),
                colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface), border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass)
            ) {
                Column(modifier = Modifier.padding(DesignTokens.PaddingLarge), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Total Assets", color = DesignTokens.TextSecondary, style = MaterialTheme.typography.titleMedium)
                    Text(formatter.format(totalBalance), color = DesignTokens.CobaltAccent, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                }
            }

            Text("Distribution of Wealth", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = DesignTokens.TextPrimary)

            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(DesignTokens.CornerRadius),
                colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface), border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass)
            ) {
                Column(modifier = Modifier.padding(DesignTokens.PaddingMedium), verticalArrangement = Arrangement.spacedBy(DesignTokens.PaddingMedium)) {
                    Row(modifier = Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(12.dp)).background(DesignTokens.BorderLight)) {
                        if (husbandRatio > 0f) Box(modifier = Modifier.fillMaxHeight().weight(husbandRatio.coerceAtLeast(0.05f)).background(DesignTokens.CobaltAccent))
                        if (wifeRatio > 0f) Box(modifier = Modifier.fillMaxHeight().weight(wifeRatio.coerceAtLeast(0.05f)).background(DesignTokens.AmberAccent))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(DesignTokens.CobaltAccent))
                            Column {
                                Text("Suami", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                                Text(formatter.format(husbandTotal), style = MaterialTheme.typography.bodySmall, color = DesignTokens.TextSecondary)
                                Text("${(husbandRatio * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(DesignTokens.AmberAccent))
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Istri", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                                Text(formatter.format(wifeTotal), style = MaterialTheme.typography.bodySmall, color = DesignTokens.TextSecondary)
                                Text("${(wifeRatio * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = DesignTokens.AmberAccent)
                            }
                        }
                    }
                }
            }

            Text("Family Ledger Members", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = DesignTokens.TextPrimary)
            
            members.forEach { member ->
                val tint = if (member.role == "Husband") DesignTokens.CobaltAccent else DesignTokens.AmberAccent
                val walletCount = wallets.count { it.memberId == member.id }
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(DesignTokens.PaddingMedium),
                    colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface), border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass)
                ) {
                    Row(modifier = Modifier.padding(DesignTokens.PaddingMedium), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(DesignTokens.PaddingMedium)) {
                            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(24.dp)).background(tint.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                                Text(member.name.take(1), color = tint, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Column {
                                Text(member.name, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                                Text(member.role, style = MaterialTheme.typography.bodySmall, color = tint)
                            }
                        }
                        Text("$walletCount Wallets", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = DesignTokens.TextSecondary)
                    }
                }
            }
        }
    }
}
