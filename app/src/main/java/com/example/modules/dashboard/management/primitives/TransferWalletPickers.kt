package com.example.modules.dashboard.management.primitives

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
import androidx.compose.ui.unit.sp
import com.example.shared.models.Member
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat

@Composable
fun TransferWalletPickers(
    wallets: List<WalletAccount>,
    members: List<Member>,
    fromWalletId: String,
    toWalletId: String,
    formatter: NumberFormat,
    onSelectFromWallet: (String) -> Unit,
    onSelectToWallet: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Dari Rekening / Dompet Asal", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DesignTokens.TextPrimary)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            wallets.forEach { w ->
                val m = members.find { it.id == w.memberId }
                val isSelected = w.id == fromWalletId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) DesignTokens.CobaltAccent else DesignTokens.Surface)
                        .clickable { onSelectFromWallet(w.id) }
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(w.name, color = if (isSelected) Color.White else DesignTokens.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${m?.name ?: ""} • ${formatter.format(w.balance)}", color = if (isSelected) Color.White.copy(alpha = 0.85f) else DesignTokens.TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        Text("Ke Rekening / Dompet Tujuan", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DesignTokens.TextPrimary)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            wallets.filter { it.id != fromWalletId }.forEach { w ->
                val m = members.find { it.id == w.memberId }
                val isSelected = w.id == toWalletId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) DesignTokens.EmeraldGlow else DesignTokens.Surface)
                        .clickable { onSelectToWallet(w.id) }
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(w.name, color = if (isSelected) Color.White else DesignTokens.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        val capNote = if (w.monthlyTransferCap > 0) " (Plafon: ${formatter.format(w.monthlyTransferCap)})" else ""
                        Text("${m?.name ?: ""}$capNote • ${formatter.format(w.balance)}", color = if (isSelected) Color.White.copy(alpha = 0.85f) else DesignTokens.TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
