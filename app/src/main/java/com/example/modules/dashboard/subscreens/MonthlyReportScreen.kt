package com.example.modules.dashboard.subscreens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.modules.dashboard.dialogs.CategoryTransactionsDialog
import com.example.modules.dashboard.dialogs.EditBudgetDialog
import com.example.modules.dashboard.dialogs.MemberTransactionsDialog
import com.example.modules.dashboard.logic.MonthFilterHelper
import com.example.modules.dashboard.primitives.*
import com.example.shared.models.*
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyReportScreen(
    transactions: List<Transaction>,
    categories: List<Category>,
    members: List<Member>,
    wallets: List<WalletAccount> = emptyList(),
    budget: Double = 5000000.0,
    onUpdateBudget: (Double) -> Unit,
    onTransactionClick: (Transaction) -> Unit = {},
    onWalletClick: (String) -> Unit = {},
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    var monthOffset by remember { mutableIntStateOf(0) }
    var dragAccumulator by remember { mutableFloatStateOf(0f) }
    val monthData = remember(transactions, monthOffset) { MonthFilterHelper.filterForMonth(transactions, monthOffset) }
    val totalExpenses = monthData.totalExpense
    val totalIncome = monthData.totalIncome
    val remainingBudget = (budget - totalExpenses).coerceAtLeast(0.0)
    val progress = if (budget > 0) (totalExpenses / budget).toFloat().coerceIn(0f, 1f) else 0f
    var showEditBudgetDialog by remember { mutableStateOf(false) }
    var selectedCategoryForDetail by remember { mutableStateOf<Category?>(null) }
    var selectedMemberForDetail by remember { mutableStateOf<Member?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Transparan Bulanan", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DesignTokens.TextPrimary) } },
                actions = {
                    IconButton(onClick = {
                        val husband = members.find { it.role == "Husband" }
                        val wife = members.find { it.role == "Wife" }
                        val hExp = monthData.transactions.filter { it.memberId == (husband?.id ?: "") && it.amount < 0 }.sumOf { -it.amount }
                        val wExp = monthData.transactions.filter { it.memberId == (wife?.id ?: "") && it.amount < 0 }.sumOf { -it.amount }
                        val summaryText = "📊 *LAPORAN KEUANGAN KELUARGA TRANSPARAN*\nPemasukan: ${formatter.format(totalIncome)}\nTotal Pengeluaran: ${formatter.format(totalExpenses)}\nSisa Anggaran: ${formatter.format(remainingBudget)}\n• ${husband?.name ?: "Suami"}: ${formatter.format(hExp)}\n• ${wife?.name ?: "Istri"}: ${formatter.format(wExp)}\n\n_Semua mutasi tercatat & transparan (Family Ledgers)_"
                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cb.setPrimaryClip(ClipData.newPlainText("Laporan Transparan", summaryText))
                        Toast.makeText(context, "Laporan lengkap disalin ke papan klip! 📋", Toast.LENGTH_SHORT).show()
                    }) { Icon(Icons.Default.Share, contentDescription = "Bagikan", tint = DesignTokens.CobaltAccent) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(DesignTokens.PaddingMedium)
                .draggable(orientation = Orientation.Horizontal, state = rememberDraggableState { delta ->
                    dragAccumulator += delta
                    if (dragAccumulator > 120f) { monthOffset--; dragAccumulator = 0f }
                    else if (dragAccumulator < -120f) { if (monthOffset < 12) monthOffset++; dragAccumulator = 0f }
                }),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.PaddingMedium)
        ) {
            item { MonthNavigationBar(currentMonthOffset = monthOffset, onPreviousMonth = { monthOffset-- }, onNextMonth = { if (monthOffset < 12) monthOffset++ }, onResetToCurrent = { monthOffset = 0 }) }
            item { TransparencyHealthCard(totalIncome = totalIncome, totalExpense = totalExpenses, transactionCount = monthData.transactions.size) }
            item { MonthlyBudgetSummaryCard(remainingBudget = remainingBudget, totalExpenses = totalExpenses, budget = budget, progress = progress, wallets = wallets, onEditBudget = { showEditBudgetDialog = true }) }
            item { SpendingByMemberCard(members = members, transactions = monthData.transactions, totalExpenses = totalExpenses, onMemberClick = { selectedMemberForDetail = it }) }
            item { CategoryBreakdownReportCard(transactions = monthData.transactions, categories = categories, totalExpenses = totalExpenses, onCategoryClick = { selectedCategoryForDetail = it }) }
            if (wallets.isNotEmpty()) {
                item { WalletFlowReportCard(wallets = wallets, members = members, transactions = monthData.transactions, onWalletClick = onWalletClick) }
            }
            item { TopExpensesReportCard(transactions = monthData.transactions, categories = categories, members = members, onTransactionClick = onTransactionClick) }
            item { MonthlyAdviceCard(progress = progress) }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    selectedCategoryForDetail?.let { cat ->
        CategoryTransactionsDialog(category = cat, transactions = monthData.transactions, members = members, onTransactionClick = onTransactionClick, onDismiss = { selectedCategoryForDetail = null })
    }
    selectedMemberForDetail?.let { mem ->
        MemberTransactionsDialog(member = mem, transactions = monthData.transactions, categories = categories, onTransactionClick = onTransactionClick, onDismiss = { selectedMemberForDetail = null })
    }
    if (showEditBudgetDialog) {
        EditBudgetDialog(monthlyBudget = budget, onConfirm = { onUpdateBudget(it); showEditBudgetDialog = false }, onDismiss = { showEditBudgetDialog = false })
    }
}
