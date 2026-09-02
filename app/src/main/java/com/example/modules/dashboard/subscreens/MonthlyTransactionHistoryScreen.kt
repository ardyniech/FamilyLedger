package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.dialogs.AddExpenseDialog
import com.example.modules.dashboard.dialogs.CsvExportDialog
import com.example.modules.dashboard.logic.MonthFilterHelper
import com.example.modules.dashboard.logic.TransactionGroupingHelper
import com.example.modules.dashboard.primitives.*
import com.example.shared.models.*
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyTransactionHistoryScreen(
    transactions: List<Transaction>,
    wallets: List<WalletAccount>,
    categories: List<Category>,
    members: List<Member>,
    onTransactionClick: (Transaction) -> Unit,
    onAddExpense: (amount: Long, note: String, walletId: String, categoryId: String, timestamp: Long) -> Unit,
    onImportCsvClick: () -> Unit,
    onBack: () -> Unit
) {
    var monthOffset by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showCsvExportDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val monthData = remember(transactions, monthOffset) { MonthFilterHelper.filterForMonth(transactions, monthOffset) }
    val filteredTransactions = remember(monthData.transactions, searchQuery) {
        if (searchQuery.isBlank()) monthData.transactions
        else monthData.transactions.filter { tx ->
            tx.note.contains(searchQuery, ignoreCase = true) ||
            categories.find { it.id == tx.categoryId }?.name?.contains(searchQuery, ignoreCase = true) == true ||
            wallets.find { it.id == tx.walletId }?.name?.contains(searchQuery, ignoreCase = true) == true
        }
    }
    val groupedByDay = remember(filteredTransactions) { TransactionGroupingHelper.groupByDay(filteredTransactions) }
    var dragAccumulator by remember { mutableFloatStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histori Transaksi Bulanan", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = DesignTokens.TextPrimary) } },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Menu CSV", tint = DesignTokens.TextPrimary) }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(text = { Text("📤 Ekspor Laporan CSV") }, onClick = { showMenu = false; showCsvExportDialog = true })
                            DropdownMenuItem(text = { Text("📥 Impor Backup CSV") }, onClick = { showMenu = false; onImportCsvClick() })
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }, containerColor = DesignTokens.CobaltAccent, contentColor = Color.White,
                shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) { Icon(Icons.Default.Add, contentDescription = "Tambah Transaksi") }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = DesignTokens.PaddingMedium)
                .draggable(orientation = Orientation.Horizontal, state = rememberDraggableState { delta ->
                    dragAccumulator += delta
                    if (dragAccumulator > 120f) { monthOffset--; dragAccumulator = 0f }
                    else if (dragAccumulator < -120f) { if (monthOffset < 12) monthOffset++; dragAccumulator = 0f }
                }),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MonthNavigationBar(currentMonthOffset = monthOffset, onPreviousMonth = { monthOffset-- }, onNextMonth = { if (monthOffset < 12) monthOffset++ }, onResetToCurrent = { monthOffset = 0 })
            MonthlyStatsBanner(totalIncome = monthData.totalIncome, totalExpense = monthData.totalExpense, netBalance = monthData.netBalance)
            TransactionSearchFilterBar(searchQuery = searchQuery, onQueryChange = { searchQuery = it }, selectedCategoryFilter = null, onCategoryFilterChange = {}, onOpenFilterSheet = {})

            if (groupedByDay.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Belum ada transaksi di bulan ini.", color = DesignTokens.TextSecondary, fontSize = 13.sp)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                    item { GroupedTransactionsSection(groups = groupedByDay, members = members, categories = categories, onTransactionClick = onTransactionClick, maxGroups = 100) }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(wallets = wallets, categories = categories, onConfirm = { amt, note, w, c, t -> onAddExpense(amt, note, w, c, t); showAddDialog = false }, onDismiss = { showAddDialog = false })
    }

    if (showCsvExportDialog) {
        CsvExportDialog(transactions = monthData.transactions, wallets = wallets, categories = categories, members = members, onDismiss = { showCsvExportDialog = false })
    }
}
