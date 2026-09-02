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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
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
    val categoryGroups by viewModel.categoryGroups.collectAsState()
    val cardOrder by viewModel.cardOrder.collectAsState()
    val hiddenCards by viewModel.hiddenCards.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    
    val activeMember = remember(members, activeMemberId) { members.find { it.id == activeMemberId } ?: members.firstOrNull() }
    val periodTransactions = remember(transactions, selectedPeriod) { PeriodFilterHelper.filterTransactions(transactions, selectedPeriod) }
    val periodSummary = remember(transactions, selectedPeriod, monthlyBudget) { PeriodFilterHelper.calculateSummary(transactions, selectedPeriod, monthlyBudget) }
    val groupedTransactions = remember(periodTransactions) { TransactionGroupingHelper.groupByDay(periodTransactions) }
    
    var currentDestination by remember { mutableStateOf<DashboardDestination>(DashboardDestination.Dashboard) }
    var showAddModal by remember { mutableStateOf(false) }
    var showQuickNav by remember { mutableStateOf(false) }
    var showPersonalizeDialog by remember { mutableStateOf(false) }
    var showAppReferenceDialog by remember { mutableStateOf(false) }
    var showCsvBottomSheet by remember { mutableStateOf(false) }
    var selectedTxForDetail by remember { mutableStateOf<Transaction?>(null) }
    var selectedTxForEdit by remember { mutableStateOf<Transaction?>(null) }
    var selectedTxForDelete by remember { mutableStateOf<Transaction?>(null) }
    var transferNotifForDialog by remember { mutableStateOf<com.example.shared.models.TransferNotification?>(null) }
    var showAddCategoryGroupDialog by remember { mutableStateOf(false) }
    var showEditMemberDialog by remember { mutableStateOf<com.example.shared.models.Member?>(null) }
    val updaterManager = remember { com.example.modules.updater.logic.UpdaterManager("ardyniech", "FamilyLedger", "1.0") }
    val updaterStatus by updaterManager.status.collectAsState()
    var showUpdateModal by remember { mutableStateOf(false) }

    val debts by viewModel.debts.collectAsState()
    val lastDeletedTx by viewModel.lastDeletedTx.collectAsState()
    val fabPosition by viewModel.fabPosition.collectAsState()
    var isAppLocked by remember { mutableStateOf(viewModel.appLockManager.isLockEnabled()) }
    var showFabSettingsDialog by remember { mutableStateOf(false) }

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
            DashboardDestinationsHost(
                destination = dest,
                viewModel = viewModel,
                activeMember = activeMember,
                syncState = syncState,
                totalBalance = totalBalance,
                wallets = wallets,
                members = members,
                financialGoals = financialGoals,
                recurringBills = recurringBills,
                transactions = transactions,
                groupedTransactions = groupedTransactions,
                categories = categories,
                selectedPeriod = selectedPeriod,
                periodSummary = periodSummary,
                activeTransferNotification = activeTransferNotification,
                budgetExceedances = budgetExceedances,
                cardOrder = cardOrder,
                hiddenCards = hiddenCards,
                updaterManager = updaterManager,
                onNavigate = { currentDestination = it },
                onShowAddModal = { showAddModal = it },
                onShowCsvBottomSheet = { showCsvBottomSheet = it },
                onShowQuickNav = { showQuickNav = it },
                onShowPersonalizeDialog = { showPersonalizeDialog = it },
                onShowAppReferenceDialog = { showAppReferenceDialog = it },
                onShowAddCategoryGroupDialog = { showAddCategoryGroupDialog = it },
                onSelectedTxForDetail = { selectedTxForDetail = it },
                onTransferNotifForDialog = { transferNotifForDialog = it },
                onShowEditMemberDialog = { showEditMemberDialog = it }
            )
        }

        if (currentDestination == DashboardDestination.Dashboard) {
            com.example.modules.dashboard.primitives.FloatingAddTransactionButton(
                fabPosition = fabPosition,
                onClick = { showAddModal = true }
            )
        }
    }

    DashboardDialogsHost(
        viewModel = viewModel, wallets = wallets, categories = categories, members = members, financialGoals = financialGoals,
        showAddModal = showAddModal, onDismissAddModal = { showAddModal = false },
        selectedTxForDetail = selectedTxForDetail, onDismissDetail = { selectedTxForDetail = null },
        onSelectEdit = { selectedTxForEdit = it }, onSelectDelete = { selectedTxForDelete = it },
        selectedTxForEdit = selectedTxForEdit, onDismissEdit = { selectedTxForEdit = null },
        selectedTxForDelete = selectedTxForDelete, onDismissDelete = { selectedTxForDelete = null },
        transferNotif = transferNotifForDialog, onDismissTransferNotif = { transferNotifForDialog = null }
    )

    if (showQuickNav) {
        com.example.modules.dashboard.primitives.QuickNavSideDrawer(
            onDismiss = { showQuickNav = false },
            onNavigateDashboard = { currentDestination = DashboardDestination.Dashboard },
            onNavigateWallets = { currentDestination = DashboardDestination.WalletManagement },
            onNavigateCategories = { currentDestination = DashboardDestination.CategoryManagement },
            onNavigateTransfer = { currentDestination = DashboardDestination.Transfer },
            onNavigateAnalytics = { currentDestination = DashboardDestination.Analytics },
            onNavigateGoals = { currentDestination = DashboardDestination.GoalsAndBudget },
            onNavigateRecurring = { currentDestination = DashboardDestination.RecurringBills },
            onNavigateFamily = { currentDestination = DashboardDestination.FamilyDashboard },
            onNavigateDebt = { currentDestination = DashboardDestination.DebtLoanTracker },
            onNavigateCsv = { showCsvBottomSheet = true },
            onNavigateSettings = { showAppReferenceDialog = true }
        )
    }

    if (showCsvBottomSheet) {
        com.example.modules.dashboard.dialogs.CsvImportBottomSheetDialog(
            wallets = wallets,
            categories = categories,
            transactions = transactions,
            onExecuteImport = { list, skip ->
                viewModel.importCsvTransactions(list, skip) {}
                showCsvBottomSheet = false
            },
            onDismiss = { showCsvBottomSheet = false }
        )
    }

    if (showPersonalizeDialog) {
        DashboardPersonalizationDialog(
            cardOrder = cardOrder,
            hiddenCards = hiddenCards,
            onMoveUp = { viewModel.dashboardLayoutManager.moveUp(it) },
            onMoveDown = { viewModel.dashboardLayoutManager.moveDown(it) },
            onToggleVisibility = { viewModel.dashboardLayoutManager.toggleVisibility(it) },
            onResetDefault = { viewModel.dashboardLayoutManager.resetToDefault() },
            onDismiss = { showPersonalizeDialog = false }
        )
    }

    if (showAppReferenceDialog) {
        AppSettingsAndReferenceDialog(
            selectedCurrency = selectedCurrency,
            onSelectCurrency = { viewModel.setSelectedCurrency(it) },
            onDismiss = { showAppReferenceDialog = false }
        )
    }

    if (showAddCategoryGroupDialog) {
        AddEditCategoryGroupDialog(onDismiss = { showAddCategoryGroupDialog = false }, onSave = { viewModel.saveCategoryGroup(it); showAddCategoryGroupDialog = false })
    }
    showEditMemberDialog?.let { m ->
        EditMemberRoleDialog(
            member = m,
            allMembers = members,
            onDismiss = { showEditMemberDialog = null },
            onSave = { viewModel.updateMemberRole(it); showEditMemberDialog = null },
            onOpenFabSettings = { showFabSettingsDialog = true }
        )
    }
    if (showFabSettingsDialog) {
        com.example.modules.dashboard.dialogs.FabPersonalizationDialog(
            currentPosition = fabPosition,
            onSelectPosition = { viewModel.fabPositionManager.setFabPosition(it) },
            onDismiss = { showFabSettingsDialog = false }
        )
    }
    if (showUpdateModal) {
        com.example.modules.updater.ui.UpdateProgressModal(updaterManager = updaterManager, onDismiss = { showUpdateModal = false; updaterManager.resetToIdle() })
    }

    if (isAppLocked) {
        LockScreenOverlay(
            onVerifyPin = { viewModel.appLockManager.verifyPin(it) },
            onSuccess = { isAppLocked = false }
        )
    }

    lastDeletedTx?.let { tx ->
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = { TextButton(onClick = { viewModel.undoDeleteTransaction() }) { Text("BATALKAN", color = DesignTokens.CobaltAccent, fontWeight = FontWeight.Bold) } }
        ) {
            Text("Transaksi '${tx.note.ifBlank { "Baru" }}' telah dihapus.")
        }
    }
}
