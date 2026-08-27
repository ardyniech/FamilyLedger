package com.example.modules.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.modules.dashboard.csv.SmartCsvImportScreen
import com.example.modules.dashboard.dialogs.*
import com.example.modules.dashboard.logic.*
import com.example.modules.dashboard.management.*
import com.example.modules.dashboard.primitives.DashboardHomeContent
import com.example.modules.dashboard.subscreens.*
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens

sealed class DashboardDestination {
    object Dashboard : DashboardDestination()
    object NetWorthDetail : DashboardDestination()
    data class WalletDetail(val walletId: String) : DashboardDestination()
    object Analytics : DashboardDestination()
    object MonthlyReport : DashboardDestination()
    object RecurringBills : DashboardDestination()
    object ExpenseList : DashboardDestination()
    object MonthlyTransactionHistory : DashboardDestination()
    object CategoryManagement : DashboardDestination()
    object WalletManagement : DashboardDestination()
    object Transfer : DashboardDestination()
    object Pairing : DashboardDestination()
    object GoalsAndBudget : DashboardDestination()
    object SmartCsvImport : DashboardDestination()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val totalBalance by viewModel.totalBalance.collectAsState()
    val members by viewModel.members.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val recurringBills by viewModel.recurringBills.collectAsState()
    val monthlyBudget by viewModel.monthlyBudget.collectAsState()
    val financialGoals by viewModel.financialGoals.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val activeMemberId by viewModel.activeMemberId.collectAsState()
    val householdPairCode by viewModel.householdPairCode.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val activeTransferNotification by viewModel.transferActiveBanner.collectAsState()
    
    val activeMember = remember(members, activeMemberId) { members.find { it.id == activeMemberId } ?: members.firstOrNull() }
    val periodTransactions = remember(transactions, selectedPeriod) { PeriodFilterHelper.filterTransactions(transactions, selectedPeriod) }
    val periodSummary = remember(transactions, selectedPeriod, monthlyBudget) { PeriodFilterHelper.calculateSummary(transactions, selectedPeriod, monthlyBudget) }
    val groupedTransactions = remember(periodTransactions) { TransactionGroupingHelper.groupByDay(periodTransactions) }
    val totalExpenses = remember(transactions, categories) { transactions.filter { t -> t.amount < 0 && categories.find { it.id == t.categoryId }?.type == "Expense" }.sumOf { -it.amount } }
    
    var currentDestination by remember { mutableStateOf<DashboardDestination>(DashboardDestination.Dashboard) }
    var showAddModal by remember { mutableStateOf(false) }
    var selectedTxForDetail by remember { mutableStateOf<Transaction?>(null) }
    var selectedTxForEdit by remember { mutableStateOf<Transaction?>(null) }
    var selectedTxForDelete by remember { mutableStateOf<Transaction?>(null) }
    var transferNotifForDialog by remember { mutableStateOf<com.example.shared.models.TransferNotification?>(null) }

    val updaterManager = remember {
        com.example.modules.updater.logic.UpdaterManager(
            owner = "ardy-syafii",
            repo = "family-ledgers",
            currentVersionName = "1.0"
        )
    }
    val updaterStatus by updaterManager.status.collectAsState()
    var showUpdateModal by remember { mutableStateOf(false) }

    LaunchedEffect(updaterStatus) {
        if (updaterStatus is com.example.modules.updater.models.UpdateStatus.UpdateAvailable ||
            updaterStatus is com.example.modules.updater.models.UpdateStatus.Downloading ||
            updaterStatus is com.example.modules.updater.models.UpdateStatus.Verifying ||
            updaterStatus is com.example.modules.updater.models.UpdateStatus.ReadyToInstall ||
            updaterStatus is com.example.modules.updater.models.UpdateStatus.Failed
        ) {
            showUpdateModal = true
        }
    }

    LaunchedEffect(Unit) { viewModel.initializeMockDataIfNeeded() }
    if (currentDestination != DashboardDestination.Dashboard) { BackHandler { currentDestination = DashboardDestination.Dashboard } }

    Box(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DesignTokens.BackgroundTop, DesignTokens.BackgroundBottom))).statusBarsPadding().navigationBarsPadding()
    ) {
        Box(modifier = Modifier.offset((-50).dp, (-50).dp).size(250.dp).background(DesignTokens.CobaltAccent.copy(alpha = 0.4f), CircleShape).blur(80.dp, BlurredEdgeTreatment.Unbounded))
        Box(modifier = Modifier.align(Alignment.BottomEnd).offset(50.dp, 100.dp).size(300.dp).background(DesignTokens.AmberAccent.copy(alpha = 0.35f), CircleShape).blur(100.dp, BlurredEdgeTreatment.Unbounded))
        Box(modifier = Modifier.align(Alignment.CenterStart).offset((-100).dp, 50.dp).size(200.dp).background(DesignTokens.EmeraldGlow.copy(alpha = 0.3f), CircleShape).blur(60.dp, BlurredEdgeTreatment.Unbounded))

        AnimatedContent(
            targetState = currentDestination,
            transitionSpec = {
                val enter = slideInHorizontally(initialOffsetX = { if (targetState != DashboardDestination.Dashboard) it else -it }, animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow)) + fadeIn()
                val exit = slideOutHorizontally(targetOffsetX = { if (targetState != DashboardDestination.Dashboard) -it else it }, animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessLow)) + fadeOut()
                enter togetherWith exit
            },
            label = "dashboard_nav"
        ) { dest ->
            when (dest) {
                is DashboardDestination.Dashboard -> DashboardHomeContent(
                    activeMember = activeMember, syncState = syncState, totalBalance = totalBalance, wallets = wallets, members = members,
                    financialGoals = financialGoals, recurringBills = recurringBills, transactions = transactions,
                    groupedTransactions = groupedTransactions, categories = categories, selectedPeriod = selectedPeriod,
                    periodSummary = periodSummary, transferNotification = activeTransferNotification,
                    onPeriodSelected = { viewModel.setSelectedPeriod(it) },
                    onTransactionClick = { selectedTxForDetail = it }, onSyncBadgeClick = { currentDestination = DashboardDestination.Pairing },
                    onProfileClick = { val other = members.find { it.id != activeMemberId }; if (other != null) viewModel.setActiveMember(other.id) else currentDestination = DashboardDestination.Pairing },
                    onNetWorthClick = { currentDestination = DashboardDestination.NetWorthDetail }, onTransferClick = { currentDestination = DashboardDestination.Transfer },
                    onWalletsClick = { currentDestination = DashboardDestination.WalletManagement }, onCategoriesClick = { currentDestination = DashboardDestination.CategoryManagement },
                    onPairingClick = { currentDestination = DashboardDestination.Pairing }, onWalletClick = { currentDestination = DashboardDestination.WalletDetail(it) },
                    onMonthlyReportClick = { currentDestination = DashboardDestination.MonthlyReport }, onAnalyticsClick = { currentDestination = DashboardDestination.Analytics },
                    onGoalsClick = { currentDestination = DashboardDestination.GoalsAndBudget }, onRecurringBillsClick = { currentDestination = DashboardDestination.RecurringBills },
                    onQuickRecordClick = { showAddModal = true }, onViewAllExpensesClick = { currentDestination = DashboardDestination.MonthlyTransactionHistory },
                    onImportCsvClick = { currentDestination = DashboardDestination.SmartCsvImport },
                    onClickTransferNotification = { transferNotifForDialog = it }
                )
                is DashboardDestination.MonthlyTransactionHistory -> MonthlyTransactionHistoryScreen(
                    transactions = transactions, wallets = wallets, categories = categories, members = members,
                    onTransactionClick = { selectedTxForDetail = it },
                    onAddExpense = { a, n, w, c, t -> viewModel.addTransaction(a, n, w, c, false, t) },
                    onImportCsvClick = { currentDestination = DashboardDestination.SmartCsvImport },
                    onBack = { currentDestination = DashboardDestination.Dashboard }
                )
                is DashboardDestination.SmartCsvImport -> SmartCsvImportScreen(
                    wallets = wallets, categories = categories, transactions = transactions,
                    onExecuteImport = { parsedList, skipDupes -> viewModel.importCsvTransactions(parsedList, skipDupes) { currentDestination = DashboardDestination.Dashboard } },
                    onBack = { currentDestination = DashboardDestination.Dashboard }
                )
                is DashboardDestination.NetWorthDetail -> NetWorthDetailScreen(totalBalance = totalBalance, wallets = wallets, members = members, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.WalletDetail -> WalletDetailScreen(walletId = dest.walletId, wallets = wallets, members = members, transactions = transactions, categories = categories, onTransactionClick = { selectedTxForDetail = it }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.Analytics -> AnalyticsScreen(transactions = transactions, categories = categories, members = members, onTransactionClick = { selectedTxForDetail = it }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.MonthlyReport -> MonthlyReportScreen(transactions = transactions, categories = categories, members = members, wallets = wallets, budget = monthlyBudget, onUpdateBudget = { viewModel.updateMonthlyBudget(it) }, onTransactionClick = { selectedTxForDetail = it }, onWalletClick = { currentDestination = DashboardDestination.WalletDetail(it) }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.RecurringBills -> RecurringBillsManagementScreen(
                    bills = recurringBills,
                    wallets = wallets,
                    categories = categories,
                    onPayBill = { b, w -> viewModel.payRecurringBill(b, w) },
                    onAddBill = { n, a, d, c, ap, w, f -> viewModel.addRecurringBill(n, a, d, c, ap, w, f) },
                    onDeleteBill = { b -> viewModel.deleteRecurringBill(b) },
                    onBack = { currentDestination = DashboardDestination.Dashboard }
                )
                is DashboardDestination.ExpenseList -> ExpenseListScreen(transactions = transactions, wallets = wallets, categories = categories, members = members, onTransactionClick = { selectedTxForDetail = it }, onAddExpense = { a, n, w, c, t -> viewModel.addTransaction(a, n, w, c, false, t) }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.CategoryManagement -> CategoryManagementScreen(categories = categories, onSaveCategory = { id, n, t -> viewModel.saveCategory(id, n, t) }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.WalletManagement -> WalletManagementScreen(wallets = wallets, members = members, onSaveWallet = { id, m, t, n, b -> viewModel.saveWalletAccount(id, m, t, n, b) }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.Transfer -> TransferScreen(wallets = wallets, members = members, onTransfer = { a, n, f, t -> viewModel.transferFunds(a, n, f, t) }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.Pairing -> PairingScreen(members = members, activeMemberId = activeMemberId, pairCode = householdPairCode, syncState = syncState, authState = authState, p2pManager = viewModel.p2pSyncManager, updaterManager = updaterManager, onSelectActiveMember = { viewModel.setActiveMember(it) }, onJoinHousehold = { viewModel.joinHousehold(it) }, onSignInWithGoogle = { viewModel.signInWithGoogle(it) }, onSignOut = { viewModel.signOut(it) }, onClearAuthError = { viewModel.clearAuthError() }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.GoalsAndBudget -> GoalsAndBudgetScreen(monthlyBudget = monthlyBudget, financialGoals = financialGoals, transactions = transactions, categories = categories, members = members, wallets = wallets, onUpdateBudget = { viewModel.updateMonthlyBudget(it) }, onAddGoal = { t, tgt, init, c, i -> viewModel.addFinancialGoal(t, tgt, init, c, i) }, onDepositToGoal = { g, a -> viewModel.depositToGoal(g, a) }, onBack = { currentDestination = DashboardDestination.Dashboard })
            }
        }
    }

    if (showAddModal) {
        AddTransactionModal(wallets = wallets, categories = categories, onDismiss = { showAddModal = false }, onSubmit = { a, n, w, c, i, t -> viewModel.addTransaction(a, n, w, c, i, t) })
    }
    selectedTxForDetail?.let { tx ->
        TransactionDetailDialog(
            transaction = tx,
            wallet = wallets.find { it.id == tx.walletId },
            category = categories.find { it.id == tx.categoryId },
            member = members.find { it.id == tx.memberId },
            onEditClick = { selectedTxForEdit = tx; selectedTxForDetail = null },
            onDeleteClick = { selectedTxForDelete = tx; selectedTxForDetail = null },
            onDismiss = { selectedTxForDetail = null }
        )
    }
    selectedTxForEdit?.let { tx ->
        EditTransactionDialog(transaction = tx, wallets = wallets, categories = categories, onSave = { viewModel.updateTransaction(tx, it); selectedTxForEdit = null }, onDismiss = { selectedTxForEdit = null })
    }
    selectedTxForDelete?.let { tx ->
        DeleteTransactionConfirmDialog(transaction = tx, onConfirm = { viewModel.deleteTransaction(tx); selectedTxForDelete = null }, onDismiss = { selectedTxForDelete = null })
    }
    transferNotifForDialog?.let { notif ->
        if (notif.status == "PENDING_CONFIRMATION") {
            IncomingTransferNotificationDialog(
                notification = notif,
                onConfirm = { notifId, emoji ->
                    viewModel.confirmTransferNotification(notifId, emoji)
                    transferNotifForDialog = null
                },
                onDismiss = { transferNotifForDialog = null }
            )
        } else if (notif.status == "CONFIRMED") {
            TransferConfirmedNotificationDialog(
                notification = notif,
                onDismiss = {
                    viewModel.dismissTransferBanner()
                    transferNotifForDialog = null
                }
            )
        }
    }

    if (showUpdateModal) {
        com.example.modules.updater.ui.UpdateProgressModal(
            updaterManager = updaterManager,
            onDismiss = {
                showUpdateModal = false
                updaterManager.resetToIdle()
            }
        )
    }
}
