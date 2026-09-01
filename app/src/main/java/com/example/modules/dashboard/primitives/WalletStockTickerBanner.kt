package com.example.modules.dashboard.primitives

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.atoms.springClickable
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MemberRoleHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Calendar
import java.util.Locale

@Composable
fun WalletStockTickerBanner(
    wallets: List<WalletAccount>,
    members: List<Member>,
    transactions: List<Transaction> = emptyList(),
    onWalletClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (wallets.isEmpty()) return
    val scrollState = rememberScrollState()

    val todayStartMillis = remember {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        cal.timeInMillis
    }

    LaunchedEffect(wallets) {
        while (isActive && wallets.isNotEmpty()) {
            val max = scrollState.maxValue
            if (max > 0) {
                scrollState.animateScrollTo(max, animationSpec = tween(durationMillis = maxOf(8000, wallets.size * 3500), easing = LinearEasing))
                delay(800)
                scrollState.scrollTo(0)
                delay(300)
            } else { delay(1000) }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(6.dp), color = DesignTokens.CobaltAccent.copy(alpha = 0.15f), border = BorderStroke(0.5.dp, DesignTokens.CobaltAccent.copy(alpha = 0.4f))) {
                Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Ticker", tint = DesignTokens.CobaltAccent, modifier = Modifier.size(12.dp))
                    Text("IDX SALDO", fontSize = 9.sp, fontWeight = FontWeight.Black, color = DesignTokens.CobaltAccent, letterSpacing = 0.5.sp)
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Row(modifier = Modifier.weight(1f).horizontalScroll(scrollState), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                wallets.forEach { wallet ->
                    val member = members.find { it.id == wallet.memberId }
                    val roleColor = MemberRoleHelper.getRoleColor(member, members)
                    val todayDelta = remember(transactions, wallet.id) {
                        transactions.filter { it.walletId == wallet.id && it.timestamp >= todayStartMillis }.sumOf { it.amount }
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = DesignTokens.SurfaceElevated, border = BorderStroke(0.5.dp, DesignTokens.BorderLight), modifier = Modifier.springClickable { onWalletClick?.invoke(wallet.id) }) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Surface(modifier = Modifier.size(6.dp).clip(CircleShape), color = roleColor) {}
                            Text(text = wallet.name.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                            Text(text = formatTickerBalance(wallet.balance), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace, color = if (wallet.balance >= 0) DesignTokens.EmeraldGlow else DesignTokens.CrimsonAccent)
                            if (todayDelta != 0L) {
                                val isPositive = todayDelta > 0
                                Text(text = "${if (isPositive) "▲+" else "▼"}${formatTickerBalance(todayDelta)}", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = if (isPositive) DesignTokens.EmeraldGlow else DesignTokens.CrimsonAccent)
                            }
                        }
                    }
                    Text("•", fontSize = 10.sp, color = DesignTokens.TextMuted)
                }
            }
        }
    }
}

private fun formatTickerBalance(amount: Long): String {
    val absAmount = kotlin.math.abs(amount)
    val prefix = if (amount < 0) "-" else ""
    return when {
        absAmount >= 1_000_000_000 -> "$prefix${String.format(Locale.US, "%.1f", absAmount / 1_000_000_000.0)}B"
        absAmount >= 1_000_000 -> "$prefix${String.format(Locale.US, "%.1f", absAmount / 1_000_000.0)}M"
        absAmount >= 1_000 -> "$prefix${absAmount / 1_000}K"
        else -> "$prefix$absAmount"
    }
}
