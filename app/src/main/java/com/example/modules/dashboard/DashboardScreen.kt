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
import com.example.modules.dashboard.dialogs.*
import com.example.modules.dashboard.logic.*
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
    val activeMemberId by viewModel.activeMemberId.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val activeTransferNotification by viewModel.transferActiveBanner.collectAsState()
    val budgetExceedances by viewModel.budgetExceedances.collectAsState()
    val cardOrder by viewModel.cardOrder.collectAsState()
    val hiddenCards by viewModel.hiddenCards.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()

    val activeMember = remember(members, activeMemberId) { members.find { it.id == activeMemberId } ?: members.firstOrNull() }
    val activeRole = remember(activeMember) { com.example.shared.models.HouseholdRole.fromString(activeMember?.role ?: "Suami") }
    val roleThemePalette = remember(activeRole) { com.example.shared.theme.RoleThemePalette.forRole(activeRole) }

    val periodTransactions = remember(transactions, selectedPeriod) { PeriodFilterHelper.filterTransactions(transactions, selectedPeriod) }
    val periodSummary = remember(transactions, selectedPeriod, monthlyBudget) { PeriodFilterHelper.calculateSummary(transactions, selectedPeriod, monthlyBudget) }
    val groupedTransactions = remember(periodTransactions) { TransactionGroupingHelper.groupByDay(periodTransactions) }

    var currentDestination by remember { mutableStateOf<DashboardDestination>(DashboardDestination.Dashboard) }
    var showAddModal by remember { mutableStateOf(false) }
    var showQuickNav by remember { mutableStateOf(false) }
    var showPersonalizeDialog by remember { mutableStateOf(false) }
    var showRolePersonalizationDialog by remember { mutableStateOf(false) }
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

    val lastDeletedTx by viewModel.lastDeletedTx.collectAsState()
    val fabPosition by viewModel.fabPosition.collectAsState()
    var isAppLocked by remember { mutableStateOf(viewModel.appLockManager.isLockEnabled()) }
    var showFabSettingsDialog by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(updaterStatus) {
        if (updaterStatus !is com.example.modules.updater.models.UpdateStatus.Idle && updaterStatus !is com.example.modules.updater.models.UpdateStatus.Checking) showUpdateModal = true
    }
    LaunchedEffect(Unit) {
        viewModel.initializeMockDataIfNeeded()
        val activity = context as? androidx.activity.ComponentActivity
        if (activity?.intent?.getStringExtra("NAVIGATE_TO") == "DEBT_TRACKER") {
            currentDestination = DashboardDestination.DebtLoanTracker
        }
    }
    if (currentDestination != DashboardDestination.Dashboard) { BackHandler { currentDestination = DashboardDestination.Dashboard } }

    CompositionLocalProvider(com.example.shared.theme.LocalRoleTheme provides roleThemePalette) {
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
                    destination = dest, viewModel = viewModel, activeMember = activeMember, syncState = syncState, totalBalance = totalBalance,
                    wallets = wallets, members = members, financialGoals = financialGoals, recurringBills = recurringBills, transactions = transactions,
                    groupedTransactions = groupedTransactions, categories = categories, selectedPeriod = selectedPeriod, periodSummary = periodSummary,
                    activeTransferNotification = activeTransferNotification, budgetExceedances = budgetExceedances, cardOrder = cardOrder,
                    hiddenCards = hiddenCards, updaterManager = updaterManager, onNavigate = { currentDestination = it }, onShowAddModal = { showAddModal = it },
                    onShowCsvBottomSheet = { showCsvBottomSheet = it }, onShowQuickNav = { showQuickNav = it }, onShowPersonalizeDialog = { showPersonalizeDialog = it },
                    onShowRolePersonalizationDialog = { showRolePersonalizationDialog = it }, onShowAppReferenceDialog = { showAppReferenceDialog = it },
                    onShowAddCategoryGroupDialog = { showAddCategoryGroupDialog = it }, onSelectedTxForDetail = { selectedTxForDetail = it },
                    onTransferNotifForDialog = { transferNotifForDialog = it }, onShowEditMemberDialog = { showEditMemberDialog = it }
                )
            }

            AnimatedVisibility(
                visible = currentDestination == DashboardDestination.Dashboard,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                com.example.modules.dashboard.primitives.FloatingAddTransactionButton(fabPosition = fabPosition, onClick = { showAddModal = true })
            }
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

    DashboardSecondaryDialogsHost(
        viewModel = viewModel, wallets = wallets, categories = categories, members = members, transactions = transactions,
        activeRole = activeRole, cardOrder = cardOrder, hiddenCards = hiddenCards, selectedCurrency = selectedCurrency,
        fabPosition = fabPosition, updaterManager = updaterManager, lastDeletedTx = lastDeletedTx, isAppLocked = isAppLocked,
        showQuickNav = showQuickNav, showPersonalizeDialog = showPersonalizeDialog, showRolePersonalizationDialog = showRolePersonalizationDialog,
        showAppReferenceDialog = showAppReferenceDialog, showCsvBottomSheet = showCsvBottomSheet,
        showAddCategoryGroupDialog = showAddCategoryGroupDialog, showEditMemberDialog = showEditMemberDialog,
        showFabSettingsDialog = showFabSettingsDialog, showUpdateModal = showUpdateModal,
        onDismissQuickNav = { showQuickNav = false }, onNavigate = { currentDestination = it },
        onShowCsvBottomSheet = { showCsvBottomSheet = it }, onShowAppReferenceDialog = { showAppReferenceDialog = it },
        onDismissPersonalize = { showPersonalizeDialog = false }, onDismissRolePersonalization = { showRolePersonalizationDialog = false },
        onDismissAddCategoryGroup = { showAddCategoryGroupDialog = false }, onDismissEditMember = { showEditMemberDialog = null },
        onDismissFabSettings = { showFabSettingsDialog = false }, onOpenFabSettings = { showFabSettingsDialog = true },
        onDismissUpdateModal = { showUpdateModal = false; updaterManager.resetToIdle() }, onUnlockApp = { isAppLocked = false }
    )
}
