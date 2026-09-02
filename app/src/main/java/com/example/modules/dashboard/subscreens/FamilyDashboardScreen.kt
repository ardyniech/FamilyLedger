package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.primitives.FamilyMemberOverviewCard
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyDashboardScreen(
    members: List<Member>,
    wallets: List<WalletAccount>,
    transactions: List<Transaction>,
    onBack: () -> Unit
) {
    val totalHouseholdBalance = wallets.sumOf { it.balance }
    val totalHouseholdSpent = transactions.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Family Dashboard (Semua Anggota)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DesignTokens.TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DesignTokens.BackgroundBottom)
            )
        },
        containerColor = DesignTokens.BackgroundBottom
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DesignTokens.CobaltAccent.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Total Aset Keluarga", color = DesignTokens.TextSecondary, fontSize = 12.sp)
                        Text(MathUtils.formatRupiah(totalHouseholdBalance), color = DesignTokens.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Column {
                        Text("Total Pengeluaran", color = DesignTokens.TextSecondary, fontSize = 12.sp)
                        Text(MathUtils.formatRupiah(totalHouseholdSpent), color = DesignTokens.RoseAccent, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Text("Monitoring Ringkasan Per Anggota:", color = DesignTokens.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(members) { member ->
                    val memberWallets = wallets.filter { it.memberId == member.id }
                    val memberTxs = transactions.filter { it.memberId == member.id }
                    FamilyMemberOverviewCard(
                        member = member,
                        memberWallets = memberWallets,
                        memberTransactions = memberTxs
                    )
                }
            }
        }
    }
}
