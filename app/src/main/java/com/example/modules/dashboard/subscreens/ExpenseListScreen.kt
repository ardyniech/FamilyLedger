package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.dialogs.AddExpenseDialog
import com.example.modules.dashboard.primitives.ExpenseListItemCard
import com.example.shared.models.*
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    transactions: List<Transaction>,
    wallets: List<WalletAccount>,
    categories: List<Category>,
    members: List<Member>,
    onTransactionClick: (Transaction) -> Unit,
    onAddExpense: (amount: Long, note: String, walletId: String, categoryId: String, timestamp: Long) -> Unit,
    onBack: () -> Unit
) {
    val expenses = remember(transactions, categories) {
        transactions.filter { t -> t.amount < 0 && categories.find { it.id == t.categoryId }?.type == "Expense" }.sortedByDescending { it.timestamp }
    }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterCategory by remember { mutableStateOf<String?>(null) }

    val filteredExpenses = remember(expenses, searchQuery, selectedFilterCategory) {
        expenses.filter {
            (selectedFilterCategory == null || it.categoryId == selectedFilterCategory) &&
            (searchQuery.isEmpty() || it.note.contains(searchQuery, ignoreCase = true) || categories.find { c -> c.id == it.categoryId }?.name?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Pengeluaran", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DesignTokens.TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddExpenseDialog = true }, containerColor = DesignTokens.AmberAccent, contentColor = Color.White,
                shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) { Icon(Icons.Default.Add, contentDescription = "Add Expense") }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = DesignTokens.PaddingMedium)) {
            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it },
                placeholder = { Text("Cari pengeluaran...", color = DesignTokens.TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = DesignTokens.TextSecondary) },
                singleLine = true, shape = RoundedCornerShape(DesignTokens.CornerRadius), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (selectedFilterCategory == null) DesignTokens.CobaltAccent else DesignTokens.SurfaceGlass).clickable { selectedFilterCategory = null }.padding(horizontal = 12.dp, vertical = 6.dp)
                ) { Text("Semua", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedFilterCategory == null) Color.White else DesignTokens.TextSecondary) }
                categories.filter { it.type == "Expense" }.forEach { cat ->
                    val isSel = selectedFilterCategory == cat.id
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (isSel) DesignTokens.AmberAccent else DesignTokens.SurfaceGlass).clickable { selectedFilterCategory = if (isSel) null else cat.id }.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) { Text(cat.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else DesignTokens.TextSecondary) }
                }
            }

            if (filteredExpenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Tidak ada pengeluaran ditemukan", color = DesignTokens.TextSecondary, fontWeight = FontWeight.SemiBold)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(filteredExpenses) { expense ->
                        ExpenseListItemCard(
                            expense = expense, category = categories.find { it.id == expense.categoryId },
                            wallet = wallets.find { it.id == expense.walletId }, member = members.find { it.id == expense.memberId },
                            onClick = { onTransactionClick(expense) }
                        )
                    }
                }
            }
        }
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            wallets = wallets, categories = categories,
            onConfirm = { amt, note, w, c, t -> onAddExpense(amt, note, w, c, t); showAddExpenseDialog = false },
            onDismiss = { showAddExpenseDialog = false }
        )
    }
}
