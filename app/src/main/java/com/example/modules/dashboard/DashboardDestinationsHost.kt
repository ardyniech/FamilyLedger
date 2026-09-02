package com.example.modules.dashboard

import androidx.compose.runtime.Composable
import com.example.modules.dashboard.csv.SmartCsvImportScreen
import com.example.modules.dashboard.logic.*
import com.example.modules.dashboard.management.*
import com.example.modules.dashboard.primitives.DashboardHomeContent
import com.example.modules.dashboard.subscreens.*
import com.example.shared.models.*
import com.example.core.sync.SyncState
import com.example.modules.updater.logic.UpdaterManager

@Composable
fun DashboardDestinationsHost(
    destination: DashboardDestination,
    viewModel: DashboardViewModel,
    activeMember: Member?,
    syncState: SyncState,
    totalBalance: Long,
    wallets: List<WalletAccount>,
    members: List<Member>,
    financialGoals: List<FinancialGoal>,
    recurringBills: List<RecurringBill>,
    transactions: List<Transaction>,
    groupedTransactions: List<DailyTransactionGroup>,
    categories: List<Category>,
    selectedPeriod: DashboardPeriod,
    periodSummary: PeriodSummary,
    activeTransferNotification: TransferNotification?,
    budgetExceedances: List<CategoryExceedance>,
    cardOrder: List<DashboardCardType>,
    hiddenCards: Set<DashboardCardType>,
    updaterManager: UpdaterManager,
    onNavigate: (DashboardDestination) -> Unit,
    onShowAddModal: (Boolean) -> Unit,
    onShowCsvBottomSheet: (Boolean) -> Unit,
    onShowQuickNav: (Boolean) -> Unit,
    onShowPersonalizeDialog: (Boolean) -> Unit,
    onShowAppReferenceDialog: (Boolean) -> Unit,
    onShowAddCategoryGroupDialog: (Boolean) -> Unit,
    onSelectedTxForDetail: (Transaction?) -> Unit,
    onTransferNotifForDialog: (TransferNotification?) -> Unit,
    onShowEditMemberDialog: (Member?) -> Unit
) {
    when (destination) {
        is DashboardDestination.Dashboard -> DashboardHomeContent(
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
            transferNotification = activeTransferNotification,
            budgetExceedances = budgetExceedances,
            cardOrder = cardOrder,
            hiddenCards = hiddenCards,
            onPeriodSelected = { viewModel.setSelectedPeriod(it) },
            onTransactionClick = onSelectedTxForDetail,
            onSyncBadgeClick = { onNavigate(DashboardDestination.Pairing) },
            onProfileClick = { onShowEditMemberDialog(activeMember) },
            onNetWorthClick = { onNavigate(DashboardDestination.NetWorthDetail) },
            onTransferClick = { onNavigate(DashboardDestination.Transfer) },
            onWalletsClick = { onNavigate(DashboardDestination.WalletManagement) },
            onCategoriesClick = { onNavigate(DashboardDestination.CategoryManagement) },
            onPairingClick = { onNavigate(DashboardDestination.Pairing) },
            onWalletClick = { onNavigate(DashboardDestination.WalletDetail(it)) },
            onMonthlyReportClick = { onNavigate(DashboardDestination.MonthlyReport) },
            onAnalyticsClick = { onNavigate(DashboardDestination.Analytics) },
            onCategoryGroupsClick = { onNavigate(DashboardDestination.CategoryGroupDashboard) },
            onGoalsClick = { onNavigate(DashboardDestination.GoalsAndBudget) },
            onRecurringBillsClick = { onNavigate(DashboardDestination.RecurringBills) },
            onQuickRecordClick = { onShowAddModal(true) },
            onSelectQuickPreset = { p ->
                val cat = categories.find { it.name.contains(p.categoryName, ignoreCase = true) } ?: categories.firstOrNull { it.type == "Expense" }
                val wal = wallets.firstOrNull()
                if (wal != null && cat != null) viewModel.addTransaction(p.amount, p.note, wal.id, cat.id, false, System.currentTimeMillis()) else onShowAddModal(true)
            },
            onViewAllExpensesClick = { onNavigate(DashboardDestination.MonthlyTransactionHistory) },
            onImportCsvClick = { onShowCsvBottomSheet(true) },
            onFamilyDashboardClick = { onNavigate(DashboardDestination.FamilyDashboard) },
            onDebtTrackerClick = { onNavigate(DashboardDestination.DebtLoanTracker) },
            onClickTransferNotification = onTransferNotifForDialog,
            onOpenQuickNav = { onShowQuickNav(true) },
            onOpenPersonalize = { onShowPersonalizeDialog(true) },
            onOpenAppReference = { onShowAppReferenceDialog(true) }
        )
        is DashboardDestination.FamilyDashboard -> FamilyDashboardScreen(members = members, wallets = wallets, transactions = transactions, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.DebtLoanTracker -> DebtLoanTrackerScreen(debts = viewModel.debts.value, onAddDebt = { viewModel.addDebt(it) }, onPayDebt = { id, amt -> viewModel.payDebt(id, amt) }, onDeleteDebt = { viewModel.deleteDebt(it) }, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.CategoryGroupDashboard -> CategoryGroupDashboardScreen(transactions = transactions, categories = categories, groups = viewModel.categoryGroups.value, onBackClick = { onNavigate(DashboardDestination.Dashboard) }, onManageGroupsClick = { onShowAddCategoryGroupDialog(true) })
        is DashboardDestination.MonthlyTransactionHistory -> MonthlyTransactionHistoryScreen(transactions = transactions, wallets = wallets, categories = categories, members = members, onTransactionClick = onSelectedTxForDetail, onAddExpense = { a, n, w, c, t -> viewModel.addTransaction(a, n, w, c, false, t) }, onImportCsvClick = { onNavigate(DashboardDestination.SmartCsvImport) }, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.SmartCsvImport -> SmartCsvImportScreen(wallets = wallets, categories = categories, transactions = transactions, onExecuteImport = { l, s -> viewModel.importCsvTransactions(l, s) { onNavigate(DashboardDestination.Dashboard) } }, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.NetWorthDetail -> NetWorthDetailScreen(totalBalance = totalBalance, wallets = wallets, members = members, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.WalletDetail -> WalletDetailScreen(walletId = destination.walletId, wallets = wallets, members = members, transactions = transactions, categories = categories, onTransactionClick = onSelectedTxForDetail, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.Analytics -> AnalyticsScreen(transactions = transactions, categories = categories, members = members, onTransactionClick = onSelectedTxForDetail, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.MonthlyReport -> MonthlyReportScreen(transactions = transactions, categories = categories, members = members, wallets = wallets, budget = viewModel.monthlyBudget.value, onUpdateBudget = { viewModel.updateMonthlyBudget(it) }, onTransactionClick = onSelectedTxForDetail, onWalletClick = { onNavigate(DashboardDestination.WalletDetail(it)) }, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.RecurringBills -> RecurringBillsManagementScreen(bills = recurringBills, wallets = wallets, categories = categories, onPayBill = { b, w -> viewModel.payRecurringBill(b, w) }, onAddBill = { n, a, d, c, ap, w, f -> viewModel.addRecurringBill(n, a, d, c, ap, w, f) }, onDeleteBill = { b -> viewModel.deleteRecurringBill(b) }, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.ExpenseList -> ExpenseListScreen(transactions = transactions, wallets = wallets, categories = categories, members = members, onTransactionClick = onSelectedTxForDetail, onAddExpense = { a, n, w, c, t -> viewModel.addTransaction(a, n, w, c, false, t) }, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.CategoryManagement -> CategoryManagementScreen(categories = categories, onSaveCategory = { id, n, t, b -> viewModel.saveCategory(id, n, t, budgetLimit = b) }, onDeleteCategory = { viewModel.deleteCategory(it) }, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.WalletManagement -> WalletManagementScreen(wallets = wallets, members = members, transactions = transactions, onSaveWallet = { id, m, t, n, b, cap -> viewModel.saveWalletAccount(id, m, t, n, b, cap) }, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.Transfer -> TransferScreen(wallets = wallets, members = members, transactions = transactions, transferState = viewModel.transferState.value, onTransfer = { a, n, f, t -> viewModel.transferFunds(a, n, f, t) }, onResetState = { viewModel.resetTransferState() }, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.Pairing -> PairingScreen(members = members, activeMemberId = viewModel.activeMemberId.value, pairCode = viewModel.householdPairCode.value, syncState = syncState, authState = viewModel.authState.value, p2pManager = viewModel.p2pSyncManager, updaterManager = updaterManager, onSelectActiveMember = { viewModel.setActiveMember(it) }, onJoinHousehold = { viewModel.joinHousehold(it) }, onSignInLocal = { id, pass, ctx -> viewModel.signInLocal(id, pass, ctx) }, onCreateLocalAccount = { id, pass, ctx -> viewModel.createLocalAccount(id, pass, ctx) }, onSignOut = { viewModel.signOut(it) }, onClearAuthError = { viewModel.clearAuthError() }, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.GoalsAndBudget -> GoalsAndBudgetScreen(monthlyBudget = viewModel.monthlyBudget.value, financialGoals = financialGoals, transactions = transactions, categories = categories, members = members, wallets = wallets, onUpdateBudget = { viewModel.updateMonthlyBudget(it) }, onAddGoal = { t, tgt, init, c, i, d, ts, hex -> viewModel.addFinancialGoal(t, tgt, init, c, i, d, ts, hex) }, onDepositToGoal = { g, a -> viewModel.depositToGoal(g, a) }, onDeleteGoal = { viewModel.deleteFinancialGoal(it) }, onBack = { onNavigate(DashboardDestination.Dashboard) })
    }
}
