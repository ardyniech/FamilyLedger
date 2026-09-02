package com.example.modules.dashboard.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@Composable
fun FamilyMemberOverviewCard(
    member: Member,
    memberWallets: List<WalletAccount>,
    memberTransactions: List<Transaction>
) {
    val totalSpent = memberTransactions.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }
    val totalIncome = memberTransactions.filter { it.amount > 0 }.sumOf { it.amount }
    val totalBalance = memberWallets.sumOf { it.balance }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(member.name, color = DesignTokens.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Peran: ${member.role}", color = DesignTokens.TextSecondary, fontSize = 11.sp)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DesignTokens.CobaltAccent.copy(alpha = 0.2f)
                ) {
                    Text("${memberWallets.size} Dompet", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = DesignTokens.CobaltAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Saldo Dompet", color = DesignTokens.TextMuted, fontSize = 11.sp)
                    Text(MathUtils.formatRupiah(totalBalance), color = DesignTokens.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Pemasukan", color = DesignTokens.TextMuted, fontSize = 11.sp)
                    Text(MathUtils.formatRupiah(totalIncome), color = DesignTokens.EmeraldGlow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Pengeluaran", color = DesignTokens.TextMuted, fontSize = 11.sp)
                    Text(MathUtils.formatRupiah(totalSpent), color = DesignTokens.RoseAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
