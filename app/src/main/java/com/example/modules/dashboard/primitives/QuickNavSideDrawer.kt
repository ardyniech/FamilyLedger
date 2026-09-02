package com.example.modules.dashboard.primitives

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

data class QuickNavItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val action: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickNavSideDrawer(
    onDismiss: () -> Unit,
    onNavigateDashboard: () -> Unit,
    onNavigateWallets: () -> Unit,
    onNavigateCategories: () -> Unit,
    onNavigateTransfer: () -> Unit,
    onNavigateAnalytics: () -> Unit,
    onNavigateGoals: () -> Unit,
    onNavigateRecurring: () -> Unit,
    onNavigateFamily: () -> Unit,
    onNavigateDebt: () -> Unit,
    onNavigateCsv: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    val navItems = listOf(
        QuickNavItem("Dashboard", "Utama", Icons.Default.Dashboard, { onNavigateDashboard(); onDismiss() }),
        QuickNavItem("Dompet", "Akun & Saldo", Icons.Default.AccountBalanceWallet, { onNavigateWallets(); onDismiss() }),
        QuickNavItem("Kategori", "Grup Pengeluaran", Icons.Default.Category, { onNavigateCategories(); onDismiss() }),
        QuickNavItem("Transfer", "Antar Dompet", Icons.Default.SwapHoriz, { onNavigateTransfer(); onDismiss() }),
        QuickNavItem("Laporan", "Analisis & Graf", Icons.Default.BarChart, { onNavigateAnalytics(); onDismiss() }),
        QuickNavItem("Budget/Goal", "Target Tabungan", Icons.Default.Savings, { onNavigateGoals(); onDismiss() }),
        QuickNavItem("Tagihan", "Recurring Bills", Icons.Default.ReceiptLong, { onNavigateRecurring(); onDismiss() }),
        QuickNavItem("Keluarga", "Family Ledger", Icons.Default.People, { onNavigateFamily(); onDismiss() }),
        QuickNavItem("Utang/Piutang", "Debt Ledger", Icons.Default.Handshake, { onNavigateDebt(); onDismiss() }),
        QuickNavItem("Impor CSV", "Smarter Importer", Icons.Default.CloudUpload, { onNavigateCsv(); onDismiss() }),
        QuickNavItem("Referensi", "Setting & App Info", Icons.Default.Settings, { onNavigateSettings(); onDismiss() })
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DesignTokens.BackgroundBottom,
        dragHandle = {
            Surface(modifier = Modifier.padding(top = 8.dp), color = DesignTokens.BorderLight, shape = RoundedCornerShape(4.dp)) {
                Box(modifier = Modifier.size(36.dp, 4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Navigasi Cepat App ⚡", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    Text("Pindah halaman dengan 1 kali ketuk", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = DesignTokens.TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(navItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { item.action() },
                        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DesignTokens.CobaltAccent.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(imageVector = item.icon, contentDescription = item.title, tint = DesignTokens.CobaltAccent, modifier = Modifier.padding(8.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                                Text(item.subtitle, fontSize = 10.sp, color = DesignTokens.TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}
