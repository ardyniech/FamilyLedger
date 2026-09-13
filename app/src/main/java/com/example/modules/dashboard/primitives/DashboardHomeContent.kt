package com.example.modules.dashboard.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.modules.dashboard.logic.*
import com.example.shared.theme.DesignTokens

@Composable
fun DashboardHomeContent(
    data: DashboardHomeData,
    actions: DashboardHomeActions
) {
    val onboardingStatus = remember(data.transactions, data.wallets) {
        HouseholdOnboardingLevelEngine.evaluate(data.transactions, data.wallets)
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(DesignTokens.PaddingMedium), verticalArrangement = Arrangement.spacedBy(DesignTokens.PaddingMedium)) {
        item {
            SmartHeaderWithQuickNav(
                activeMember = data.activeMember, syncState = data.syncState,
                onOpenQuickNav = actions.onOpenQuickNav, onSyncBadgeClick = actions.onSyncBadgeClick,
                onProfileClick = actions.onProfileClick, onOpenRolePersonalize = actions.onOpenRolePersonalize,
                onOpenPersonalize = actions.onOpenPersonalize, onOpenAppReference = actions.onOpenAppReference
            )
        }
        if (data.activeMember?.role.equals("Istri", ignoreCase = true)) {
            item {
                WifeExpenseOnboardingBanner(
                    onOpenWifeHub = actions.onOpenWifeHub,
                    onQuickAddExpense = actions.onQuickRecordClick
                )
            }
        }
        if (onboardingStatus.level == OnboardingMaturityLevel.LEVEL_1_FRESH || onboardingStatus.shouldShowGuidedPrompt) {
            item {
                HouseholdOnboardingLevelCard(
                    status = onboardingStatus,
                    onQuickAddTransaction = actions.onQuickRecordClick,
                    onAddWallet = actions.onWalletsClick
                )
            }
        }
        items(data.cardOrder.filter { !data.hiddenCards.contains(it) }, key = { it.name }) { cardType ->
            when (cardType) {
                DashboardCardType.HERO_BALANCE -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActivePeriodStatusBanner(selectedPeriod = data.selectedPeriod, transactionCount = data.transactions.size, onPeriodClick = { actions.onAnalyticsClick() })
                    HeroCard(totalBalance = data.totalBalance, wallets = data.wallets, members = data.members, onClick = actions.onNetWorthClick)
                }
                DashboardCardType.SAFE_TO_SPEND -> data.safeToSpendReport?.let { report ->
                    SafeToSpendCard(report = report, onClickDetails = actions.onOpenSafeToSpendDetails)
                }
                DashboardCardType.NAFKAH_ALLOCATION -> data.nafkahAllocationReport?.let { report ->
                    WifeNafkahAllocationCard(report = report, onRecordHouseholdExpense = actions.onQuickRecordClick, onTransferNafkah = actions.onTransferClick)
                }
                DashboardCardType.FINANCIAL_INTEGRITY -> data.financialIntegrityReport?.let { report ->
                    FinancialIntegrityCard(report = report, selectedCadence = data.cashflowCadence, onSelectCadence = actions.onSelectCadence, onOpenLoans = actions.onDebtTrackerClick)
                }
                DashboardCardType.QUICK_ACTIONS -> DashboardActionRow(onTransferClick = actions.onTransferClick, onWalletsClick = actions.onWalletsClick, onCategoriesClick = actions.onCategoriesClick, onPairingClick = actions.onPairingClick)
                DashboardCardType.FINANCIAL_HEALTH -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PeriodSelectorRow(selectedPeriod = data.selectedPeriod, onPeriodSelected = actions.onPeriodSelected)
                    PeriodOverviewCard(summary = data.periodSummary, onClick = actions.onMonthlyReportClick)
                    FinancialCriticismActionCard(summary = data.periodSummary, transactions = data.transactions, categories = data.categories, onAnalyticsClick = actions.onAnalyticsClick, onGoalsClick = actions.onGoalsClick, onViewAllExpensesClick = actions.onViewAllExpensesClick)
                }
                DashboardCardType.WALLETS_CAROUSEL -> WalletCarousel(wallets = data.wallets, members = data.members, onWalletClick = actions.onWalletClick)
                DashboardCardType.ACTIVE_BANNERS -> if (data.transferNotification != null || data.budgetExceedances.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        TransferNotificationBanner(notification = data.transferNotification, activeMember = data.activeMember, onClickBanner = actions.onClickTransferNotification, onDismiss = {})
                        BudgetExceedancesBanner(exceedances = data.budgetExceedances)
                    }
                }
                DashboardCardType.RECENT_TRANSACTIONS -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    RecentTransactionsHeader(onClick = actions.onViewAllExpensesClick)
                    GroupedTransactionsSection(groups = data.groupedTransactions, members = data.members, categories = data.categories, onTransactionClick = actions.onTransactionClick, maxGroups = 5)
                }
            }
        }
    }
}
