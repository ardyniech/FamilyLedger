package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.modules.dashboard.DashboardDestination
import com.example.modules.dashboard.DashboardViewModel
import com.example.modules.dashboard.logic.DashboardCardType
import com.example.modules.dashboard.logic.FabPosition
import com.example.modules.dashboard.management.AddEditCategoryGroupDialog
import com.example.modules.dashboard.primitives.QuickNavSideDrawer
import com.example.modules.updater.logic.UpdaterManager
import com.example.shared.models.*
import com.example.shared.theme.DesignTokens

@Composable
fun DashboardSecondaryDialogsHost(
    viewModel: DashboardViewModel,
    wallets: List<WalletAccount>,
    categories: List<Category>,
    members: List<Member>,
    transactions: List<Transaction>,
    activeRole: HouseholdRole,
    cardOrder: List<DashboardCardType>,
    hiddenCards: Set<DashboardCardType>,
    selectedCurrency: com.example.shared.utils.MultiCurrencyHelper.Currency,
    fabPosition: FabPosition,
    updaterManager: UpdaterManager,
    lastDeletedTx: Transaction?,
    isAppLocked: Boolean,
    showQuickNav: Boolean,
    showPersonalizeDialog: Boolean,
    showRolePersonalizationDialog: Boolean,
    showAppReferenceDialog: Boolean,
    showCsvBottomSheet: Boolean,
    showAddCategoryGroupDialog: Boolean,
    showEditMemberDialog: Member?,
    showFabSettingsDialog: Boolean,
    showUpdateModal: Boolean,
    onDismissQuickNav: () -> Unit,
    onNavigate: (DashboardDestination) -> Unit,
    onShowCsvBottomSheet: (Boolean) -> Unit,
    onShowAppReferenceDialog: (Boolean) -> Unit,
    onDismissPersonalize: () -> Unit,
    onDismissRolePersonalization: () -> Unit,
    onDismissAddCategoryGroup: () -> Unit,
    onDismissEditMember: () -> Unit,
    onDismissFabSettings: () -> Unit,
    onOpenFabSettings: () -> Unit,
    onDismissUpdateModal: () -> Unit,
    onUnlockApp: () -> Unit
) {
    if (showQuickNav) {
        QuickNavSideDrawer(
            onDismiss = onDismissQuickNav,
            onNavigateDashboard = { onNavigate(DashboardDestination.Dashboard) },
            onNavigateWallets = { onNavigate(DashboardDestination.WalletManagement) },
            onNavigateCategories = { onNavigate(DashboardDestination.CategoryManagement) },
            onNavigateTransfer = { onNavigate(DashboardDestination.Transfer) },
            onNavigateAnalytics = { onNavigate(DashboardDestination.Analytics) },
            onNavigateGoals = { onNavigate(DashboardDestination.GoalsAndBudget) },
            onNavigateRecurring = { onNavigate(DashboardDestination.RecurringBills) },
            onNavigateExpenses = { onNavigate(DashboardDestination.HouseholdExpenses) },
            onNavigateWifeHub = { onNavigate(DashboardDestination.WifeHouseholdExpenseHub) },
            onNavigateFamily = { onNavigate(DashboardDestination.FamilyDashboard) },
            onNavigateDebt = { onNavigate(DashboardDestination.DebtLoanTracker) },
            onNavigateEarlyPayoff = { onNavigate(DashboardDestination.EarlyPayoffSimulator()) },
            onNavigateCsv = { onShowCsvBottomSheet(true) },
            onNavigateSettings = { onShowAppReferenceDialog(true) },
            onClearDatabase = { viewModel.clearDatabase() }
        )
    }

    if (showCsvBottomSheet) {
        CsvImportBottomSheetDialog(
            wallets = wallets, categories = categories, transactions = transactions,
            onExecuteImport = { list, skip ->
                viewModel.importCsvTransactions(list, skip) {}
                onShowCsvBottomSheet(false)
            },
            onDismiss = { onShowCsvBottomSheet(false) }
        )
    }

    if (showPersonalizeDialog) {
        DashboardPersonalizationDialog(
            cardOrder = cardOrder, hiddenCards = hiddenCards,
            onMoveUp = { viewModel.dashboardLayoutManager.moveUp(it) },
            onMoveDown = { viewModel.dashboardLayoutManager.moveDown(it) },
            onToggleVisibility = { viewModel.dashboardLayoutManager.toggleVisibility(it) },
            onResetDefault = { viewModel.dashboardLayoutManager.resetToDefault() },
            onDismiss = onDismissPersonalize
        )
    }

    if (showRolePersonalizationDialog) {
        RolePersonalizationDialog(
            currentRole = activeRole,
            onApplyRole = { role -> viewModel.applyHouseholdRole(role) },
            onDismiss = onDismissRolePersonalization
        )
    }

    if (showAppReferenceDialog) {
        AppSettingsAndReferenceDialog(
            selectedCurrency = selectedCurrency,
            appLockManager = viewModel.appLockManager,
            onSelectCurrency = { viewModel.setSelectedCurrency(it) },
            onClearDatabase = { viewModel.clearDatabase() },
            onDismiss = { onShowAppReferenceDialog(false) }
        )
    }

    if (showAddCategoryGroupDialog) {
        AddEditCategoryGroupDialog(onDismiss = onDismissAddCategoryGroup, onSave = { viewModel.saveCategoryGroup(it); onDismissAddCategoryGroup() })
    }

    showEditMemberDialog?.let { m ->
        EditMemberRoleDialog(
            member = m, allMembers = members, onDismiss = onDismissEditMember,
            onSave = { viewModel.updateMemberRole(it); onDismissEditMember() },
            onOpenFabSettings = onOpenFabSettings
        )
    }

    if (showFabSettingsDialog) {
        FabPersonalizationDialog(currentPosition = fabPosition, onSelectPosition = { viewModel.fabPositionManager.setFabPosition(it) }, onDismiss = onDismissFabSettings)
    }

    if (showUpdateModal) {
        com.example.modules.updater.ui.UpdateProgressModal(updaterManager = updaterManager, onDismiss = onDismissUpdateModal)
    }

    if (isAppLocked) {
        LockScreenOverlay(
            isBiometricAllowed = viewModel.appLockManager.isBiometricEnabled(),
            onVerifyPin = { viewModel.appLockManager.verifyPin(it) },
            onSuccess = onUnlockApp
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
