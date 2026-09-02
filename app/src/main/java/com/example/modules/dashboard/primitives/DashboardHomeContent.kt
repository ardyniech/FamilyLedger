package com.example.modules.dashboard.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.sync.SyncState
import com.example.modules.dashboard.logic.*
import com.example.shared.models.*
import com.example.shared.theme.DesignTokens

@Composable
fun DashboardHomeContent(
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
    transferNotification: TransferNotification? = null,
    budgetExceedances: List<CategoryExceedance> = emptyList(),
    cardOrder: List<DashboardCardType> = DashboardCardType.getDefaultList(),
    hiddenCards: Set<DashboardCardType> = emptySet(),
    onPeriodSelected: (DashboardPeriod) -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onSyncBadgeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNetWorthClick: () -> Unit,
    onTransferClick: () -> Unit,
    onWalletsClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onPairingClick: () -> Unit,
    onWalletClick: (String) -> Unit,
    onMonthlyReportClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onCategoryGroupsClick: () -> Unit = {},
    onGoalsClick: () -> Unit,
    onRecurringBillsClick: () -> Unit,
    onQuickRecordClick: () -> Unit,
    onSelectQuickPreset: ((QuickExpensePreset) -> Unit)? = null,
    onViewAllExpensesClick: () -> Unit,
    onImportCsvClick: () -> Unit,
    onFamilyDashboardClick: () -> Unit = {},
    onDebtTrackerClick: () -> Unit = {},
    onClickTransferNotification: (TransferNotification) -> Unit = {},
    onOpenQuickNav: () -> Unit = {},
    onOpenPersonalize: () -> Unit = {},
    onOpenAppReference: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(DesignTokens.PaddingMedium),
        verticalArrangement = Arrangement.spacedBy(DesignTokens.PaddingMedium)
    ) {
        item {
            SmartHeaderWithQuickNav(
                activeMember = activeMember,
                syncState = syncState,
                onOpenQuickNav = onOpenQuickNav,
                onSyncBadgeClick = onSyncBadgeClick,
                onProfileClick = onProfileClick,
                onOpenPersonalize = onOpenPersonalize,
                onOpenAppReference = onOpenAppReference
            )
        }

        items(cardOrder.filter { !hiddenCards.contains(it) }, key = { it.name }) { cardType ->
            when (cardType) {
                DashboardCardType.HERO_BALANCE -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ActivePeriodStatusBanner(selectedPeriod = selectedPeriod, transactionCount = transactions.size, onPeriodClick = { onAnalyticsClick() })
                        HeroCard(totalBalance = totalBalance, wallets = wallets, members = members, onClick = onNetWorthClick)
                    }
                }
                DashboardCardType.QUICK_ACTIONS -> {
                    DashboardActionRow(onTransferClick = onTransferClick, onWalletsClick = onWalletsClick, onCategoriesClick = onCategoriesClick, onPairingClick = onPairingClick)
                }
                DashboardCardType.FINANCIAL_HEALTH -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        PeriodSelectorRow(selectedPeriod = selectedPeriod, onPeriodSelected = onPeriodSelected)
                        PeriodOverviewCard(summary = periodSummary, onClick = onMonthlyReportClick)
                        FinancialCriticismActionCard(
                            summary = periodSummary,
                            transactions = transactions,
                            categories = categories,
                            onAnalyticsClick = onAnalyticsClick,
                            onGoalsClick = onGoalsClick,
                            onViewAllExpensesClick = onViewAllExpensesClick
                        )
                    }
                }
                DashboardCardType.WALLETS_CAROUSEL -> {
                    WalletCarousel(wallets = wallets, members = members, onWalletClick = onWalletClick)
                }
                DashboardCardType.ACTIVE_BANNERS -> {
                    if (transferNotification != null || budgetExceedances.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            TransferNotificationBanner(notification = transferNotification, activeMember = activeMember, onClickBanner = onClickTransferNotification, onDismiss = {})
                            BudgetExceedancesBanner(exceedances = budgetExceedances)
                        }
                    }
                }
                DashboardCardType.RECENT_TRANSACTIONS -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        RecentTransactionsHeader(onClick = onViewAllExpensesClick)
                        GroupedTransactionsSection(groups = groupedTransactions, members = members, categories = categories, onTransactionClick = onTransactionClick, maxGroups = 5)
                    }
                }
            }
        }
    }
}
