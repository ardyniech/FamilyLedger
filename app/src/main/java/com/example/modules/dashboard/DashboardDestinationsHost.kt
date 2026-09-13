package com.example.modules.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.example.modules.dashboard.csv.SmartCsvImportScreen
import com.example.modules.dashboard.logic.*
import com.example.modules.dashboard.management.*
import com.example.modules.dashboard.primitives.DashboardHomeContent
import com.example.modules.dashboard.primitives.DashboardHomeData
import com.example.modules.dashboard.primitives.DashboardHomeActions
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
    onShowRolePersonalizationDialog: (Boolean) -> Unit = {},
    onShowAppReferenceDialog: (Boolean) -> Unit,
    onShowAddCategoryGroupDialog: (Boolean) -> Unit,
    onSelectedTxForDetail: (Transaction?) -> Unit,
    onTransferNotifForDialog: (TransferNotification?) -> Unit,
    onShowEditMemberDialog: (Member?) -> Unit
) {
    val debtsState = viewModel.debts.collectAsState()
    val integrityReportState = viewModel.financialIntegrityReport.collectAsState()
    val nafkahReportState = viewModel.nafkahAllocationReport.collectAsState()
    val cadenceState = viewModel.cashflowCadence.collectAsState()
    val monthlyBudgetState = viewModel.monthlyBudget.collectAsState()
    var showSafeToSpendDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    val safeToSpendReport = androidx.compose.runtime.remember(
        totalBalance, monthlyBudgetState.value, transactions, recurringBills, debtsState.value, financialGoals
    ) {
        SafeToSpendEngine.calculate(
            totalBalance = totalBalance,
            monthlyBudget = monthlyBudgetState.value,
            transactions = transactions,
            recurringBills = recurringBills,
            debts = debtsState.value,
            goals = financialGoals
        )
    }

    when (destination) {
        is DashboardDestination.Dashboard -> {
            val homeData = DashboardHomeData(
                activeMember, syncState, totalBalance, wallets, members, financialGoals, recurringBills, transactions,
                groupedTransactions, categories, selectedPeriod, periodSummary, integrityReportState.value, nafkahReportState.value,
                safeToSpendReport, cadenceState.value, activeTransferNotification, budgetExceedances, cardOrder, hiddenCards
            )
            val homeActions = DashboardHomeActions(
                onPeriodSelected = { viewModel.setSelectedPeriod(it) },
                onSelectCadence = { viewModel.setCashflowCadence(it) },
                onTransactionClick = onSelectedTxForDetail,
                onSyncBadgeClick = { onNavigate(DashboardDestination.Pairing) },
                onProfileClick = { onShowRolePersonalizationDialog(true) },
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
                onOpenRolePersonalize = { onShowRolePersonalizationDialog(true) },
                onOpenPersonalize = { onShowPersonalizeDialog(true) },
                onOpenAppReference = { onShowAppReferenceDialog(true) },
                onOpenSafeToSpendDetails = { showSafeToSpendDialog = true },
                onOpenWifeHub = { onNavigate(DashboardDestination.WifeHouseholdExpenseHub) }
            )
            DashboardHomeContent(data = homeData, actions = homeActions)

            if (showSafeToSpendDialog) {
                com.example.modules.dashboard.dialogs.SafeToSpendBreakdownDialog(
                    report = safeToSpendReport,
                    onDismiss = { showSafeToSpendDialog = false }
                )
            }
        }
        is DashboardDestination.WifeHouseholdExpenseHub -> {
            val report = com.example.modules.dashboard.logic.NafkahAllocationCalculator.calculate(
                activeMember = activeMember,
                members = members,
                wallets = wallets,
                transactions = transactions,
                categories = categories
            )
            com.example.modules.dashboard.subscreens.WifeHouseholdExpenseHubScreen(
                report = report,
                activeMember = activeMember,
                wallets = wallets,
                categories = categories,
                transactions = transactions,
                onQuickRecordPreset = { preset ->
                    val cat = categories.find { it.name.contains(preset.categoryKeyword, ignoreCase = true) } ?: categories.find { it.type == "Expense" }
                    val wal = wallets.find { it.type.equals(preset.defaultWalletType, ignoreCase = true) && (it.memberId == activeMember?.id || it.id.contains("deina") || it.id.contains("dapur")) } ?: wallets.firstOrNull()
                    if (wal != null && cat != null) {
                        viewModel.addTransaction(preset.defaultAmount, preset.defaultNote, wal.id, cat.id, false, System.currentTimeMillis())
                    } else {
                        onShowAddModal(true)
                    }
                },
                onCustomRecordPreset = { preset, amount, note ->
                    val cat = categories.find { it.name.contains(preset.categoryKeyword, ignoreCase = true) } ?: categories.find { it.type == "Expense" }
                    val wal = wallets.find { it.type.equals(preset.defaultWalletType, ignoreCase = true) && (it.memberId == activeMember?.id || it.id.contains("deina") || it.id.contains("dapur")) } ?: wallets.firstOrNull()
                    if (wal != null && cat != null) {
                        viewModel.addTransaction(amount, note, wal.id, cat.id, false, System.currentTimeMillis())
                    } else {
                        onShowAddModal(true)
                    }
                },
                onRecordParsedReceipt = { amount, note, categoryKeyword, walletType ->
                    val cat = categories.find { it.name.contains(categoryKeyword, ignoreCase = true) } ?: categories.find { it.type == "Expense" }
                    val wal = wallets.find { it.type.equals(walletType, ignoreCase = true) && (it.memberId == activeMember?.id || it.id.contains("deina") || it.id.contains("dapur")) } ?: wallets.firstOrNull()
                    if (wal != null && cat != null) {
                        viewModel.addTransaction(amount, note, wal.id, cat.id, false, System.currentTimeMillis())
                    } else {
                        onShowAddModal(true)
                    }
                },
                onManualRecordClick = { onShowAddModal(true) },
                onWalletClick = { onNavigate(DashboardDestination.WalletDetail(it)) },
                onTransactionClick = { txId ->
                    val tx = transactions.find { it.id == txId }
                    if (tx != null) onSelectedTxForDetail(tx)
                },
                onBack = { onNavigate(DashboardDestination.Dashboard) }
            )
        }
        is DashboardDestination.FamilyDashboard -> FamilyDashboardScreen(members = members, wallets = wallets, transactions = transactions, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.DebtLoanTracker -> DebtLoanTrackerScreen(
            debts = debtsState.value,
            report = integrityReportState.value,
            selectedCadence = cadenceState.value,
            onSelectCadence = { viewModel.setCashflowCadence(it) },
            onAddDebt = { viewModel.addDebt(it) },
            onPayDebt = { id, amt -> viewModel.payDebt(id, amt) },
            onDeleteDebt = { viewModel.deleteDebt(it) },
            onOpenEarlyPayoffSimulator = { debtId -> onNavigate(DashboardDestination.EarlyPayoffSimulator(debtId)) },
            onOpenKprSimulator = { onNavigate(DashboardDestination.KprSimulator(null)) },
            onBack = { onNavigate(DashboardDestination.Dashboard) }
        )
        is DashboardDestination.EarlyPayoffSimulator -> EarlyPayoffSimulatorScreen(
            debts = debtsState.value,
            initialDebtId = destination.initialDebtId,
            onBack = { onNavigate(DashboardDestination.DebtLoanTracker) }
        )
        is DashboardDestination.KprSimulator -> KprSimulatorScreen(
            onBack = { onNavigate(DashboardDestination.DebtLoanTracker) }
        )
        is DashboardDestination.HouseholdExpenses -> HouseholdExpensesScreen(
            expenses = viewModel.householdExpenses.collectAsState().value,
            onAddExpenseClick = { onShowAddModal(true) },
            onDeleteExpense = { viewModel.deleteHouseholdExpense(it) },
            onBack = { onNavigate(DashboardDestination.Dashboard) }
        )
        is DashboardDestination.CategoryGroupDashboard -> CategoryGroupDashboardScreen(transactions = transactions, categories = categories, groups = viewModel.categoryGroups.value, onBackClick = { onNavigate(DashboardDestination.Dashboard) }, onManageGroupsClick = { onShowAddCategoryGroupDialog(true) })
        is DashboardDestination.MonthlyTransactionHistory -> MonthlyTransactionHistoryScreen(transactions = transactions, wallets = wallets, categories = categories, members = members, onTransactionClick = onSelectedTxForDetail, onAddExpense = { a, n, w, c, t -> viewModel.addTransaction(a, n, w, c, false, t) }, onImportCsvClick = { onNavigate(DashboardDestination.SmartCsvImport) }, onBack = { onNavigate(DashboardDestination.Dashboard) })
        is DashboardDestination.SmartCsvImport -> SmartCsvImportScreen(wallets = wallets, categories = categories, transactions = transactions, onExecuteImport = { l, s, c -> viewModel.importCsvTransactions(l, s, c) { onNavigate(DashboardDestination.Dashboard) } }, onBack = { onNavigate(DashboardDestination.Dashboard) })
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
