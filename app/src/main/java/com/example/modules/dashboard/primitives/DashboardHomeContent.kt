package com.example.modules.dashboard.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.sync.SyncState
import com.example.modules.dashboard.logic.DailyTransactionGroup
import com.example.modules.dashboard.logic.DashboardPeriod
import com.example.modules.dashboard.logic.PeriodSummary
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
    onViewAllExpensesClick: () -> Unit,
    onImportCsvClick: () -> Unit,
    onClickTransferNotification: (TransferNotification) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(DesignTokens.PaddingMedium),
        verticalArrangement = Arrangement.spacedBy(DesignTokens.PaddingMedium)
    ) {
        item { DashboardHeaderRow(activeMember = activeMember, syncState = syncState, onSyncBadgeClick = onSyncBadgeClick, onProfileClick = onProfileClick) }
        item {
            TransferNotificationBanner(
                notification = transferNotification,
                activeMember = activeMember,
                onClickBanner = onClickTransferNotification,
                onDismiss = {}
            )
        }
        item { BudgetExceedancesBanner(exceedances = budgetExceedances) }
        item { HeroCard(totalBalance = totalBalance, wallets = wallets, members = members, onClick = onNetWorthClick) }
        item { CashflowHealthWidget(filteredTransactions = transactions, allTransactions = transactions) }
        item { DashboardActionRow(onTransferClick = onTransferClick, onWalletsClick = onWalletsClick, onCategoriesClick = onCategoriesClick, onPairingClick = onPairingClick) }
        item {
            com.example.modules.dashboard.primitives.CategoryGroupBanner(onClick = onCategoryGroupsClick)
        }
        item { SavingsIntegrityCard(transactions = transactions, categories = categories) }
        item { WalletDebtLedgerCard(wallets = wallets, members = members, transactions = transactions) }
        item { SmartCsvImportBanner(onClick = onImportCsvClick) }
        item {
            Spacer(modifier = Modifier.height(DesignTokens.PaddingSmall))
            PeriodSelectorRow(selectedPeriod = selectedPeriod, onPeriodSelected = onPeriodSelected)
        }
        item { PeriodOverviewCard(summary = periodSummary, onClick = onMonthlyReportClick) }
        item {
            TransparencyHealthCard(
                totalIncome = periodSummary.totalInflow,
                totalExpense = periodSummary.totalOutflow,
                transactionCount = transactions.size,
                onClick = onAnalyticsClick
            )
        }
        item { WalletCarousel(wallets = wallets, members = members, onWalletClick = onWalletClick) }
        item { ExpenseBreakdownCard(transactions = transactions, categories = categories, onClick = onAnalyticsClick) }
        item { GoalsBannerCard(goalCount = financialGoals.size, onClick = onGoalsClick) }
        item { UpcomingRecurringBillsCard(bills = recurringBills, onClick = onRecurringBillsClick) }
        item { QuickRecordButton(onClick = onQuickRecordClick) }
        item {
            Spacer(modifier = Modifier.height(DesignTokens.PaddingSmall))
            RecentTransactionsHeader(onClick = onViewAllExpensesClick)
        }
        item {
            GroupedTransactionsSection(
                groups = groupedTransactions,
                members = members,
                categories = categories,
                onTransactionClick = onTransactionClick,
                maxGroups = 5
            )
        }
    }
}
