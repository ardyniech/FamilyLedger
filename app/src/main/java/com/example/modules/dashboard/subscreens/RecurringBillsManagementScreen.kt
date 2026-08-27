package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
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
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedWalletToPayId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    var billToPayId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscriptions & Bills", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DesignTokens.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(DesignTokens.CornerRadius),
                    colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass),
                    elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
                ) {
                    val totalUnpaid = bills.filter { !it.isPaid }.sumOf { it.amount }
                    val activeAutoPayCount = bills.count { it.autoPay }
                    
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Active Budget Commitments", color = DesignTokens.TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = formatter.format(totalUnpaid),
                                    color = if (totalUnpaid > 0) Color.Red else DesignTokens.EmeraldGlow,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text("Total Unpaid This Month", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DesignTokens.CobaltAccent.copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "⚡ $activeAutoPayCount Auto-Debit",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DesignTokens.CobaltAccent
                                )
                            }
                        }
                    }
                }
            }

            // Quick Add Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAddDialog = true },
                    shape = RoundedCornerShape(DesignTokens.CornerRadius),
                    colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("+ Schedule New Subscription / Bill", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent, fontSize = 14.sp)
                    }
                }
            }

            // Scheduled List Section
            item {
                Text(
                    text = "Active Scheduled Agreements",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DesignTokens.TextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (bills.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.CornerRadius),
                        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass)
                    ) {
                        Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No active subscriptions or recurring payments.", color = DesignTokens.TextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(bills) { bill ->
                    val matchedCategory = categories.find { it.id == bill.categoryId }
                    val targetWallet = wallets.find { it.id == bill.targetWalletId }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bill.name,
                                        fontWeight = FontWeight.Bold,
                                        color = DesignTokens.TextPrimary,
                                        fontSize = 15.sp
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(DesignTokens.TextSecondary.copy(alpha = 0.1f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = bill.frequency,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DesignTokens.TextSecondary
                                            )
                                        }
                                        Text(
                                            text = "Due: ${bill.dueDate}",
                                            fontSize = 11.sp,
                                            color = DesignTokens.TextSecondary
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = formatter.format(bill.amount),
                                        fontWeight = FontWeight.Bold,
                                        color = if (bill.isPaid) DesignTokens.TextSecondary else Color.Red,
                                        fontSize = 14.sp
                                    )
                                    
                                    IconButton(
                                        onClick = { onDeleteBill(bill.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.Red.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Divider(color = DesignTokens.BorderGlass.copy(alpha = 0.5f), thickness = 1.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (bill.autoPay && targetWallet != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(DesignTokens.EmeraldGlow.copy(alpha = 0.1f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "⚡ Auto-Deduct: ${targetWallet.name}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DesignTokens.EmeraldGlow
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(DesignTokens.TextSecondary.copy(alpha = 0.1f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "Manual Payment Required",
                                                fontSize = 10.sp,
                                                color = DesignTokens.TextSecondary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (bill.isPaid) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(DesignTokens.EmeraldGlow.copy(alpha = 0.15f))
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text("Paid ✓", color = DesignTokens.EmeraldGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = { billToPayId = bill.id },
                                            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.height(28.dp),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Pay Now", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (billToPayId != null) {
        val targetBill = bills.find { it.id == billToPayId }
        AlertDialog(
            onDismissRequest = { billToPayId = null },
            title = { Text("Deduct Account Payment", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select which source wallet account to debit RP ${formatter.format(targetBill?.amount ?: 0.0)} from:", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                    wallets.forEach { wallet ->
                        val isSelected = wallet.id == selectedWalletToPayId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DesignTokens.CobaltAccent.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { selectedWalletToPayId = wallet.id }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(wallet.name, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 13.sp)
                            Text(formatter.format(wallet.balance), fontSize = 11.sp, color = DesignTokens.TextSecondary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val billId = billToPayId
                        if (billId != null && selectedWalletToPayId.isNotEmpty()) {
                            onPayBill(billId, selectedWalletToPayId)
                        }
                        billToPayId = null
                    }
                ) {
                    Text("Confirm Debit", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { billToPayId = null }) {
                    Text("Cancel", color = DesignTokens.TextSecondary)
                }
            }
        )
    }

    if (showAddDialog) {
        var billName by remember { mutableStateOf("") }
        var billAmount by remember { mutableStateOf("") }
        var billDueDate by remember { mutableStateOf("") }
        var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }
        var autoPay by remember { mutableStateOf(false) }
        var targetWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
        var frequency by remember { mutableStateOf("Monthly") }

        val frequencies = listOf("Monthly", "Weekly", "Daily", "One-Time")

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Schedule Subscription / Bill", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = billName,
                        onValueChange = { billName = it },
                        label = { Text("Agreement / Bill Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = billAmount,
                        onValueChange = { billAmount = it },
                        label = { Text("Amount (Rp)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = billDueDate,
                        onValueChange = { billDueDate = it },
                        label = { Text("Next Due Date (e.g., Sep 01, 2026)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Frequency Selector
                    Text("Billing Frequency", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        frequencies.forEach { freq ->
                            val isSel = freq == frequency
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) DesignTokens.CobaltAccent else DesignTokens.BorderGlass.copy(alpha = 0.1f))
                                    .clickable { frequency = freq }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = freq,
                                    color = if (isSel) Color.White else DesignTokens.TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Auto Pay Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Automatic Ledger Population", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Auto-deduct and post on specific date", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                        }
                        Switch(
                            checked = autoPay,
                            onCheckedChange = { autoPay = it }
                        )
                    }

                    if (autoPay) {
                        Text("Auto-Deduct From Account", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DesignTokens.TextSecondary)
                        wallets.forEach { wallet ->
                            val isSelected = wallet.id == targetWalletId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) DesignTokens.CobaltAccent.copy(alpha = 0.1f) else Color.Transparent)
                                    .clickable { targetWalletId = wallet.id }
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(wallet.name, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 12.sp)
                                Text(formatter.format(wallet.balance), fontSize = 10.sp, color = DesignTokens.TextSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsedAmount = billAmount.toDoubleOrNull() ?: 0.0
                        if (billName.isNotEmpty() && parsedAmount > 0.0 && billDueDate.isNotEmpty()) {
                            onAddBill(
                                billName,
                                parsedAmount,
                                billDueDate,
                                selectedCategoryId,
                                autoPay,
                                if (autoPay) targetWalletId else null,
                                frequency
                            )
                        }
                        showAddDialog = false
                    }
                ) {
                    Text("Schedule Commit", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = DesignTokens.TextSecondary)
                }
            }
        )
    }
}
