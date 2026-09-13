package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.dialogs.EditPresetAmountDialog
import com.example.modules.dashboard.dialogs.ScanReceiptQuickModal
import com.example.modules.dashboard.logic.*
import com.example.modules.dashboard.primitives.*
import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifeHouseholdExpenseHubScreen(
    report: NafkahAllocationReport,
    activeMember: Member?,
    wallets: List<WalletAccount>,
    categories: List<Category>,
    transactions: List<Transaction>,
    onQuickRecordPreset: (WifeExpensePresetItem) -> Unit,
    onCustomRecordPreset: (WifeExpensePresetItem, Long, String) -> Unit = { _, _, _ -> },
    onRecordParsedReceipt: (Long, String, String, String) -> Unit = { _, _, _, _ -> },
    onManualRecordClick: () -> Unit,
    onWalletClick: (String) -> Unit,
    onTransactionClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val palette = RoleThemePalette.istri()
    var editingPreset by remember { mutableStateOf<WifeExpensePresetItem?>(null) }
    var showReceiptModal by remember { mutableStateOf(false) }
    var pantryItems by remember { mutableStateOf(KitchenPantryDefaults.getInitialKitchenItems()) }

    val wifeTransactions = transactions.filter { tx ->
        tx.amount < 0 && (tx.memberId == activeMember?.id || tx.walletId.contains("deina") || tx.walletId.contains("dapur"))
    }

    if (editingPreset != null) {
        EditPresetAmountDialog(
            preset = editingPreset!!,
            onDismiss = { editingPreset = null },
            onSaveAndRecord = { p, amt, note ->
                onCustomRecordPreset(p, amt, note)
                editingPreset = null
            }
        )
    }

    if (showReceiptModal) {
        ScanReceiptQuickModal(
            onDismiss = { showReceiptModal = false },
            onConfirmRecord = { amt, note, catKw, walType ->
                onRecordParsedReceipt(amt, note, catKw, walType)
                showReceiptModal = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Pusat Belanja & Pos Dapur Istri", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                        Text("Kelola uang dapur, stok bahan & scan struk", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = DesignTokens.TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showReceiptModal = true }) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = "Scan Struk", tint = palette.primaryAccent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DesignTokens.Surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onManualRecordClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) },
                text = { Text("Catat Belanja Lainnya", fontWeight = FontWeight.Bold, color = Color.White) },
                containerColor = palette.primaryAccent,
                shape = RoundedCornerShape(16.dp)
            )
        },
        containerColor = DesignTokens.BackgroundBottom
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
        ) {
            item { WifeBudgetGuideCard(report = report, onInitTemplates = {}) }
            item { WifeWalletsRow(wallets = wallets, wifeMemberId = activeMember?.id, onWalletClick = onWalletClick) }
            item {
                KitchenPantryStockCard(
                    pantryItems = pantryItems,
                    onRestockItem = { item ->
                        onRecordParsedReceipt(item.defaultRestockCost, "Restock ${item.name}", "dapur", "Cash")
                        pantryItems = pantryItems.map { if (it.id == item.id) it.copy(stockLevel = PantryStockLevel.FULL, isRestockNeeded = false, estimatedDaysLeft = 14) else it }
                    },
                    onToggleStockStatus = { item ->
                        val nextLevel = when (item.stockLevel) {
                            PantryStockLevel.FULL -> PantryStockLevel.MEDIUM
                            PantryStockLevel.MEDIUM -> PantryStockLevel.LOW
                            PantryStockLevel.LOW -> PantryStockLevel.FULL
                        }
                        pantryItems = pantryItems.map { if (it.id == item.id) it.copy(stockLevel = nextLevel, isRestockNeeded = nextLevel == PantryStockLevel.LOW) else it }
                    }
                )
            }
            item {
                WifeQuickExpensePresetsCard(
                    onSelectPreset = onQuickRecordPreset,
                    onEditPreset = { editingPreset = it }
                )
            }
            item {
                WifeRecentHouseholdList(
                    transactions = wifeTransactions,
                    categories = categories,
                    wallets = wallets,
                    onTransactionClick = onTransactionClick
                )
            }
        }
    }
}
