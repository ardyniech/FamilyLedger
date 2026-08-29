package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.RecurringBill
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringBillsManagementScreen(
    bills: List<RecurringBill>,
    wallets: List<WalletAccount>,
    categories: List<Category>,
    onPayBill: (billId: String, walletId: String) -> Unit,
    onAddBill: (name: String, amount: Double, dueDate: String, categoryId: String, autoPay: Boolean, targetWalletId: String?, frequency: String) -> Unit,
    onDeleteBill: (billId: String) -> Unit,
    onBack: () -> Unit
) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    var showAddDialog by remember { mutableStateOf(false) }
    var billToPayId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscriptions & Bills", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = DesignTokens.TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(DesignTokens.CornerRadius),
                    colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
                    border = BorderStroke(1.dp, DesignTokens.BorderGlass),
                    elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
                ) {
                    val totalUnpaid = bills.filter { !it.isPaid }.sumOf { it.amount }
                    val activeAutoPayCount = bills.count { it.autoPay }
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Active Budget Commitments", color = DesignTokens.TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = formatter.format(totalUnpaid), color = if (totalUnpaid > 0) Color.Red else DesignTokens.EmeraldGlow, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                                Text("Total Unpaid This Month", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                            }
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(DesignTokens.CobaltAccent.copy(alpha = 0.12f)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(text = "⚡ $activeAutoPayCount Auto-Debit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                            }
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { showAddDialog = true }, shape = RoundedCornerShape(DesignTokens.CornerRadius),
                    colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface), border = BorderStroke(1.dp, DesignTokens.BorderGlass)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text("+ Schedule New Subscription / Bill", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent, fontSize = 14.sp)
                    }
                }
            }
            item { Text(text = "Active Scheduled Agreements", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DesignTokens.TextPrimary, modifier = Modifier.padding(top = 8.dp)) }
            if (bills.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(DesignTokens.CornerRadius), colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface), border = BorderStroke(1.dp, DesignTokens.BorderGlass)) {
                        Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No active subscriptions or recurring payments.", color = DesignTokens.TextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(bills) { bill ->
                    RecurringBillItemCard(
                        bill = bill, targetWallet = wallets.find { it.id == bill.targetWalletId }, formatter = formatter,
                        onDeleteBill = onDeleteBill, onPayClick = { billToPayId = it }
                    )
                }
            }
        }
    }
    if (billToPayId != null) {
        PayBillDialog(targetBill = bills.find { it.id == billToPayId }, wallets = wallets, formatter = formatter, onDismiss = { billToPayId = null }, onConfirm = { id, wId -> onPayBill(id, wId); billToPayId = null })
    }
    if (showAddDialog) {
        AddRecurringBillDialog(wallets = wallets, categories = categories, formatter = formatter, onDismiss = { showAddDialog = false }, onAdd = onAddBill)
    }
}
