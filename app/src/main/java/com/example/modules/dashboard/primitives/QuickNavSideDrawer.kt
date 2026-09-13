package com.example.modules.dashboard.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

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
    onNavigateExpenses: () -> Unit = {},
    onNavigateWifeHub: () -> Unit = {},
    onNavigateFamily: () -> Unit,
    onNavigateDebt: () -> Unit,
    onNavigateEarlyPayoff: () -> Unit = {},
    onNavigateCsv: () -> Unit,
    onNavigateSettings: () -> Unit,
    onClearDatabase: () -> Unit
) {
    val navItems = listOf(
        QuickNavItem("Dashboard", "Utama", Icons.Default.Dashboard, { onNavigateDashboard(); onDismiss() }),
        QuickNavItem("Dompet", "Akun & Saldo", Icons.Default.AccountBalanceWallet, { onNavigateWallets(); onDismiss() }),
        QuickNavItem("Kategori", "Grup Pengeluaran", Icons.Default.Category, { onNavigateCategories(); onDismiss() }),
        QuickNavItem("Transfer", "Antar Dompet", Icons.Default.SwapHoriz, { onNavigateTransfer(); onDismiss() }),
        QuickNavItem("Laporan", "Analisis & Graf", Icons.Default.BarChart, { onNavigateAnalytics(); onDismiss() }),
        QuickNavItem("Budget/Goal", "Target Tabungan", Icons.Default.Savings, { onNavigateGoals(); onDismiss() }),
        QuickNavItem("Pusat Istri", "Dapur & Belanja", Icons.Default.Kitchen, { onNavigateWifeHub(); onDismiss() }),
        QuickNavItem("Pos Belanja", "Pengeluaran RT", Icons.Default.ShoppingBag, { onNavigateExpenses(); onDismiss() }),
        QuickNavItem("Tagihan", "Recurring Bills", Icons.AutoMirrored.Filled.ReceiptLong, { onNavigateRecurring(); onDismiss() }),
        QuickNavItem("Keluarga", "Family Ledger", Icons.Default.People, { onNavigateFamily(); onDismiss() }),
        QuickNavItem("Kredit & KPR", "Integritas & Cicilan", Icons.Default.Handshake, { onNavigateDebt(); onDismiss() }),
        QuickNavItem("Simulasi KPR", "Early Payoff", Icons.Default.Calculate, { onNavigateEarlyPayoff(); onDismiss() }),
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
            QuickNavGrid(items = navItems)
            Spacer(modifier = Modifier.height(16.dp))
            QuickNavDatabaseResetSection(
                onConfirmClearDatabase = {
                    onClearDatabase()
                    onDismiss()
                }
            )
        }
    }
}
