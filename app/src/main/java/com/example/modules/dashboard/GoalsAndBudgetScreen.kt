package com.example.modules.dashboard

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.dialogs.*
import com.example.modules.dashboard.logic.MonthFilterHelper
import com.example.modules.dashboard.primitives.*
import com.example.shared.models.*
import com.example.shared.theme.DesignTokens

@Composable
fun GoalsAndBudgetScreen(
    monthlyBudget: Long,
    financialGoals: List<FinancialGoal>,
    transactions: List<Transaction>,
    categories: List<Category>,
    members: List<Member>,
    wallets: List<WalletAccount> = emptyList(),
    onUpdateBudget: (Long) -> Unit,
    onAddGoal: (title: String, targetAmount: Long, initialAmount: Long, category: String, iconEmoji: String) -> Unit,
    onDepositToGoal: (goalId: String, amount: Long) -> Unit,
    onTransactionClick: (Transaction) -> Unit = {},
    onBack: () -> Unit
) {
    var monthOffset by remember { mutableIntStateOf(0) }
    var dragAccumulator by remember { mutableFloatStateOf(0f) }
    val monthData = remember(transactions, monthOffset) { MonthFilterHelper.filterForMonth(transactions, monthOffset) }
    val totalExpenses = remember(monthData.transactions, categories) {
        monthData.transactions.filter { t -> t.amount < 0 && categories.find { it.id == t.categoryId }?.type == "Expense" }.sumOf { -it.amount }
    }

    var showEditBudgetDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var depositGoalTarget by remember { mutableStateOf<FinancialGoal?>(null) }
    var selectedCategoryForDetail by remember { mutableStateOf<Category?>(null) }
    var selectedMemberForDetail by remember { mutableStateOf<Member?>(null) }

    Scaffold(
        topBar = { GoalsAndBudgetTopBar(members = members, monthlyBudget = monthlyBudget, totalExpenses = totalExpenses, onBack = onBack) },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    dragAccumulator += delta
                    if (dragAccumulator > 120f) { monthOffset--; dragAccumulator = 0f }
                    else if (dragAccumulator < -120f) { if (monthOffset < 12) monthOffset++; dragAccumulator = 0f }
                }
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { MonthNavigationBar(currentMonthOffset = monthOffset, onPreviousMonth = { monthOffset-- }, onNextMonth = { if (monthOffset < 12) monthOffset++ }, onResetToCurrent = { monthOffset = 0 }) }
            item { BudgetOverviewCard(monthlyBudget = monthlyBudget, totalExpenses = totalExpenses, wallets = wallets, onEditBudget = { showEditBudgetDialog = true }) }
            item { BudgetPacingIndicatorCard(monthlyBudget = monthlyBudget, totalExpenses = totalExpenses, goalCount = financialGoals.size) }
            item { CategoryBudgetBreakdownCard(transactions = monthData.transactions, categories = categories, monthlyBudget = monthlyBudget, onCategoryClick = { selectedCategoryForDetail = it }) }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Impian & Tabungan Keluarga", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DesignTokens.TextPrimary)
                    TextButton(onClick = { showAddGoalDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = DesignTokens.CobaltAccent)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah Impian", fontSize = 12.sp, color = DesignTokens.CobaltAccent, fontWeight = FontWeight.Bold)
                    }
                }
            }
            items(financialGoals) { goal -> GoalItemCard(goal = goal, onDepositClick = { depositGoalTarget = goal }) }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    if (showEditBudgetDialog) EditBudgetDialog(monthlyBudget = monthlyBudget, onConfirm = { onUpdateBudget(it); showEditBudgetDialog = false }, onDismiss = { showEditBudgetDialog = false })
    if (showAddGoalDialog) AddGoalDialog(onConfirm = { t, tgt, init, cat, ico -> onAddGoal(t, tgt, init, cat, ico); showAddGoalDialog = false }, onDismiss = { showAddGoalDialog = false })
    depositGoalTarget?.let { target -> DepositGoalDialog(goal = target, onConfirm = { onDepositToGoal(target.id, it); depositGoalTarget = null }, onDismiss = { depositGoalTarget = null }) }
    selectedCategoryForDetail?.let { cat ->
        CategoryTransactionsDialog(category = cat, transactions = monthData.transactions, members = members, onTransactionClick = onTransactionClick, onDismiss = { selectedCategoryForDetail = null })
    }
    selectedMemberForDetail?.let { mem ->
        MemberTransactionsDialog(member = mem, transactions = monthData.transactions, categories = categories, onTransactionClick = onTransactionClick, onDismiss = { selectedMemberForDetail = null })
    }
}
