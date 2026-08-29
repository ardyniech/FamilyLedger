package com.example.modules.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.modules.dashboard.csv.SmartCsvImportScreen
import com.example.modules.dashboard.dialogs.*
import com.example.modules.dashboard.logic.*
import com.example.modules.dashboard.management.*
import com.example.modules.dashboard.primitives.DashboardHomeContent
import com.example.modules.dashboard.subscreens.*
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens

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
    val budgetExceedances by viewModel.budgetExceedances.collectAsState()
    
    val activeMember = remember(members, activeMemberId) { members.find { it.id == activeMemberId } ?: members.firstOrNull() }
    val periodTransactions = remember(transactions, selectedPeriod) { PeriodFilterHelper.filterTransactions(transactions, selectedPeriod) }
    val periodSummary = remember(transactions, selectedPeriod, monthlyBudget) { PeriodFilterHelper.calculateSummary(transactions, selectedPeriod, monthlyBudget) }
    val groupedTransactions = remember(periodTransactions) { TransactionGroupingHelper.groupByDay(periodTransactions) }
    
    var currentDestination by remember { mutableStateOf<DashboardDestination>(DashboardDestination.Dashboard) }
    var showAddModal by remember { mutableStateOf(false) }
    var selectedTxForDetail by remember { mutableStateOf<Transaction?>(null) }
    var selectedTxForEdit by remember { mutableStateOf<Transaction?>(null) }
    var selectedTxForDelete by remember { mutableStateOf<Transaction?>(null) }
    var transferNotifForDialog by remember { mutableStateOf<com.example.shared.models.TransferNotification?>(null) }
    val updaterManager = remember { com.example.modules.updater.logic.UpdaterManager("ardyniech", "FamilyLedger", "1.0") }
    val updaterStatus by updaterManager.status.collectAsState()
    var showUpdateModal by remember { mutableStateOf(false) }

    LaunchedEffect(updaterStatus) {
        if (updaterStatus !is com.example.modules.updater.models.UpdateStatus.Idle && updaterStatus !is com.example.modules.updater.models.UpdateStatus.Checking) showUpdateModal = true
    }
    LaunchedEffect(Unit) { viewModel.initializeMockDataIfNeeded() }
    if (currentDestination != DashboardDestination.Dashboard) { BackHandler { currentDestination = DashboardDestination.Dashboard } }

    Box(modifier = Modifier.fillMaxSize().background(DesignTokens.BackgroundBottom).statusBarsPadding().navigationBarsPadding()) {
        com.example.shared.atoms.AnimatedMeshBackground(modifier = Modifier.fillMaxSize())

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
                    activeMember = activeMember, syncState = syncState, totalBalance = totalBalance, wallets = wallets, members = members, financialGoals = financialGoals, recurringBills = recurringBills, transactions = transactions, groupedTransactions = groupedTransactions, categories = categories, selectedPeriod = selectedPeriod, periodSummary = periodSummary, transferNotification = activeTransferNotification, budgetExceedances = budgetExceedances,
                    onPeriodSelected = { viewModel.setSelectedPeriod(it) }, onTransactionClick = { selectedTxForDetail = it }, onSyncBadgeClick = { currentDestination = DashboardDestination.Pairing }, onProfileClick = { val o = members.find { it.id != activeMemberId }; if (o != null) viewModel.setActiveMember(o.id) else currentDestination = DashboardDestination.Pairing }, onNetWorthClick = { currentDestination = DashboardDestination.NetWorthDetail }, onTransferClick = { currentDestination = DashboardDestination.Transfer }, onWalletsClick = { currentDestination = DashboardDestination.WalletManagement }, onCategoriesClick = { currentDestination = DashboardDestination.CategoryManagement }, onPairingClick = { currentDestination = DashboardDestination.Pairing }, onWalletClick = { currentDestination = DashboardDestination.WalletDetail(it) }, onMonthlyReportClick = { currentDestination = DashboardDestination.MonthlyReport }, onAnalyticsClick = { currentDestination = DashboardDestination.Analytics }, onGoalsClick = { currentDestination = DashboardDestination.GoalsAndBudget }, onRecurringBillsClick = { currentDestination = DashboardDestination.RecurringBills }, onQuickRecordClick = { showAddModal = true }, onViewAllExpensesClick = { currentDestination = DashboardDestination.MonthlyTransactionHistory }, onImportCsvClick = { currentDestination = DashboardDestination.SmartCsvImport }, onClickTransferNotification = { transferNotifForDialog = it }
                )
                is DashboardDestination.MonthlyTransactionHistory -> MonthlyTransactionHistoryScreen(transactions = transactions, wallets = wallets, categories = categories, members = members, onTransactionClick = { selectedTxForDetail = it }, onAddExpense = { a, n, w, c, t -> viewModel.addTransaction(a, n, w, c, false, t) }, onImportCsvClick = { currentDestination = DashboardDestination.SmartCsvImport }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.SmartCsvImport -> SmartCsvImportScreen(wallets = wallets, categories = categories, transactions = transactions, onExecuteImport = { l, s -> viewModel.importCsvTransactions(l, s) { currentDestination = DashboardDestination.Dashboard } }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.NetWorthDetail -> NetWorthDetailScreen(totalBalance = totalBalance, wallets = wallets, members = members, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.WalletDetail -> WalletDetailScreen(walletId = dest.walletId, wallets = wallets, members = members, transactions = transactions, categories = categories, onTransactionClick = { selectedTxForDetail = it }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.Analytics -> AnalyticsScreen(transactions = transactions, categories = categories, members = members, onTransactionClick = { selectedTxForDetail = it }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.MonthlyReport -> MonthlyReportScreen(transactions = transactions, categories = categories, members = members, wallets = wallets, budget = monthlyBudget, onUpdateBudget = { viewModel.updateMonthlyBudget(it) }, onTransactionClick = { selectedTxForDetail = it }, onWalletClick = { currentDestination = DashboardDestination.WalletDetail(it) }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.RecurringBills -> RecurringBillsManagementScreen(bills = recurringBills, wallets = wallets, categories = categories, onPayBill = { b, w -> viewModel.payRecurringBill(b, w) }, onAddBill = { n, a, d, c, ap, w, f -> viewModel.addRecurringBill(n, a, d, c, ap, w, f) }, onDeleteBill = { b -> viewModel.deleteRecurringBill(b) }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.ExpenseList -> ExpenseListScreen(transactions = transactions, wallets = wallets, categories = categories, members = members, onTransactionClick = { selectedTxForDetail = it }, onAddExpense = { a, n, w, c, t -> viewModel.addTransaction(a, n, w, c, false, t) }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.CategoryManagement -> CategoryManagementScreen(categories = categories, onSaveCategory = { id, n, t, b -> viewModel.saveCategory(id, n, t, budgetLimit = b) }, onDeleteCategory = { viewModel.deleteCategory(it) }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.WalletManagement -> WalletManagementScreen(wallets = wallets, members = members, onSaveWallet = { id, m, t, n, b -> viewModel.saveWalletAccount(id, m, t, n, b) }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.Transfer -> TransferScreen(wallets = wallets, members = members, onTransfer = { a, n, f, t -> viewModel.transferFunds(a, n, f, t) }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.Pairing -> PairingScreen(members = members, activeMemberId = activeMemberId, pairCode = householdPairCode, syncState = syncState, authState = authState, p2pManager = viewModel.p2pSyncManager, updaterManager = updaterManager, onSelectActiveMember = { viewModel.setActiveMember(it) }, onJoinHousehold = { viewModel.joinHousehold(it) }, onSignInLocal = { id, pass, ctx -> viewModel.signInLocal(id, pass, ctx) }, onCreateLocalAccount = { id, pass, ctx -> viewModel.createLocalAccount(id, pass, ctx) }, onSignOut = { viewModel.signOut(it) }, onClearAuthError = { viewModel.clearAuthError() }, onBack = { currentDestination = DashboardDestination.Dashboard })
                is DashboardDestination.GoalsAndBudget -> GoalsAndBudgetScreen(monthlyBudget = monthlyBudget, financialGoals = financialGoals, transactions = transactions, categories = categories, members = members, wallets = wallets, onUpdateBudget = { viewModel.updateMonthlyBudget(it) }, onAddGoal = { t, tgt, init, c, i -> viewModel.addFinancialGoal(t, tgt, init, c, i) }, onDepositToGoal = { g, a -> viewModel.depositToGoal(g, a) }, onBack = { currentDestination = DashboardDestination.Dashboard })
            }
        }
    }

    DashboardDialogsHost(
        viewModel = viewModel, wallets = wallets, categories = categories, members = members,
        showAddModal = showAddModal, onDismissAddModal = { showAddModal = false },
        selectedTxForDetail = selectedTxForDetail, onDismissDetail = { selectedTxForDetail = null },
        onSelectEdit = { selectedTxForEdit = it }, onSelectDelete = { selectedTxForDelete = it },
        selectedTxForEdit = selectedTxForEdit, onDismissEdit = { selectedTxForEdit = null },
        selectedTxForDelete = selectedTxForDelete, onDismissDelete = { selectedTxForDelete = null },
        transferNotif = transferNotifForDialog, onDismissTransferNotif = { transferNotifForDialog = null }
    )

    if (showUpdateModal) {
        com.example.modules.updater.ui.UpdateProgressModal(updaterManager = updaterManager, onDismiss = { showUpdateModal = false; updaterManager.resetToIdle() })
    }
}
