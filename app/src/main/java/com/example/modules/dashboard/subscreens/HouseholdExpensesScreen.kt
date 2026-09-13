package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.modules.dashboard.primitives.HouseholdExpenseExportActions
import com.example.modules.dashboard.primitives.HouseholdExpenseFilterBar
import com.example.modules.dashboard.primitives.HouseholdExpenseItemCard
import com.example.shared.models.HouseholdExpense
import com.example.shared.utils.MathUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdExpensesScreen(
    expenses: List<HouseholdExpense>,
    onAddExpenseClick: () -> Unit,
    onDeleteExpense: (HouseholdExpense) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var needFilter by remember { mutableStateOf<Boolean?>(null) }
    val filtered = remember(expenses, needFilter) {
        if (needFilter == null) expenses else expenses.filter { it.isNeed == needFilter }
    }
    val totalExpense = remember(filtered) { filtered.sumOf { it.amount } }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengeluaran Rumah Tangga") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    HouseholdExpenseExportActions(context = context, expenses = filtered)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpenseClick) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Pos Belanja")
            }
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text("Total Terfilter", style = MaterialTheme.typography.labelMedium)
                        Text(MathUtils.formatRupiah(totalExpense), style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
            item {
                HouseholdExpenseFilterBar(selectedNeedFilter = needFilter, onSelectNeedFilter = { needFilter = it })
            }
            if (filtered.isEmpty()) {
                item {
                    Text(
                        text = "Belum ada catatan pengeluaran pada filter ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                }
            } else {
                items(filtered, key = { it.id }) { item ->
                    HouseholdExpenseItemCard(expense = item, onDelete = onDeleteExpense)
                }
            }
        }
    }
}
