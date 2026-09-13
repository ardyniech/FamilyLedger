package com.example.modules.dashboard.primitives

import com.example.core.sync.SyncState
import com.example.modules.dashboard.logic.*
import com.example.shared.models.*

data class DashboardHomeData(
    val activeMember: Member?,
    val syncState: SyncState,
    val totalBalance: Long,
    val wallets: List<WalletAccount>,
    val members: List<Member>,
    val financialGoals: List<FinancialGoal>,
    val recurringBills: List<RecurringBill>,
    val transactions: List<Transaction>,
    val groupedTransactions: List<DailyTransactionGroup>,
    val categories: List<Category>,
    val selectedPeriod: DashboardPeriod,
    val periodSummary: PeriodSummary,
    val financialIntegrityReport: FinancialIntegrityReport? = null,
    val nafkahAllocationReport: NafkahAllocationReport? = null,
    val safeToSpendReport: SafeToSpendReport? = null,
    val cashflowCadence: CashflowCadence = CashflowCadence.DAILY,
    val transferNotification: TransferNotification? = null,
    val budgetExceedances: List<CategoryExceedance> = emptyList(),
    val cardOrder: List<DashboardCardType> = DashboardCardType.getDefaultList(),
    val hiddenCards: Set<DashboardCardType> = emptySet()
)

data class DashboardHomeActions(
    val onPeriodSelected: (DashboardPeriod) -> Unit,
    val onSelectCadence: (CashflowCadence) -> Unit = {},
    val onTransactionClick: (Transaction) -> Unit,
    val onSyncBadgeClick: () -> Unit,
    val onProfileClick: () -> Unit,
    val onNetWorthClick: () -> Unit,
    val onTransferClick: () -> Unit,
    val onWalletsClick: () -> Unit,
    val onCategoriesClick: () -> Unit,
    val onPairingClick: () -> Unit,
    val onWalletClick: (String) -> Unit,
    val onMonthlyReportClick: () -> Unit,
    val onAnalyticsClick: () -> Unit,
    val onCategoryGroupsClick: () -> Unit = {},
    val onGoalsClick: () -> Unit,
    val onRecurringBillsClick: () -> Unit,
    val onQuickRecordClick: () -> Unit,
    val onSelectQuickPreset: ((QuickExpensePreset) -> Unit)? = null,
    val onViewAllExpensesClick: () -> Unit,
    val onImportCsvClick: () -> Unit,
    val onFamilyDashboardClick: () -> Unit = {},
    val onDebtTrackerClick: () -> Unit = {},
    val onClickTransferNotification: (TransferNotification) -> Unit = {},
    val onOpenQuickNav: () -> Unit = {},
    val onOpenRolePersonalize: () -> Unit = {},
    val onOpenPersonalize: () -> Unit = {},
    val onOpenAppReference: () -> Unit = {},
    val onOpenSafeToSpendDetails: () -> Unit = {},
    val onOpenWifeHub: () -> Unit = {}
)
