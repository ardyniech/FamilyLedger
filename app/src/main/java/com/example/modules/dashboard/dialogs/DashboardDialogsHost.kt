package com.example.modules.dashboard.dialogs

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.modules.dashboard.AddTransactionModal
import com.example.modules.dashboard.DashboardViewModel
import com.example.shared.models.*

@Composable
fun DashboardDialogsHost(
    viewModel: DashboardViewModel,
    wallets: List<WalletAccount>,
    categories: List<Category>,
    members: List<Member>,
    financialGoals: List<FinancialGoal> = emptyList(),
    showAddModal: Boolean,
    onDismissAddModal: () -> Unit,
    selectedTxForDetail: Transaction?,
    onDismissDetail: () -> Unit,
    onSelectEdit: (Transaction) -> Unit,
    onSelectDelete: (Transaction) -> Unit,
    selectedTxForEdit: Transaction?,
    onDismissEdit: () -> Unit,
    selectedTxForDelete: Transaction?,
    onDismissDelete: () -> Unit,
    transferNotif: TransferNotification?,
    onDismissTransferNotif: () -> Unit
) {
    AnimatedVisibility(
        visible = showAddModal,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = spring(stiffness = Spring.StiffnessMedium)
        )
    ) {
        AddTransactionModal(
            wallets = wallets,
            categories = categories,
            goals = financialGoals,
            transactionState = viewModel.transactionState.collectAsState().value,
            onDismiss = onDismissAddModal,
            onSubmit = { a, n, w, c, i, t, gId -> viewModel.addTransaction(a, n, w, c, i, t, gId) },
            onResetState = { viewModel.resetTransactionState() }
        )
    }
    selectedTxForDetail?.let { tx ->
        TransactionDetailDialog(
            transaction = tx,
            wallet = wallets.find { it.id == tx.walletId },
            category = categories.find { it.id == tx.categoryId },
            member = members.find { it.id == tx.memberId },
            financialGoal = financialGoals.find { it.id == tx.goalId },
            onEditClick = { onSelectEdit(tx); onDismissDetail() },
            onDeleteClick = { onSelectDelete(tx); onDismissDetail() },
            onDismiss = onDismissDetail
        )
    }
    selectedTxForEdit?.let { tx ->
        EditTransactionDialog(
            transaction = tx, wallets = wallets, categories = categories,
            onSave = { viewModel.updateTransaction(tx, it); onDismissEdit() },
            onDismiss = onDismissEdit
        )
    }
    selectedTxForDelete?.let { tx ->
        DeleteTransactionConfirmDialog(
            transaction = tx,
            onConfirm = { viewModel.deleteTransaction(tx); onDismissDelete() },
            onDismiss = onDismissDelete
        )
    }
    transferNotif?.let { notif ->
        if (notif.status == "PENDING_CONFIRMATION") {
            IncomingTransferNotificationDialog(
                notification = notif,
                onConfirm = { notifId, emoji -> viewModel.confirmTransferNotification(notifId, emoji); onDismissTransferNotif() },
                onDismiss = onDismissTransferNotif
            )
        } else if (notif.status == "CONFIRMED") {
            TransferConfirmedNotificationDialog(
                notification = notif,
                onDismiss = { viewModel.dismissTransferBanner(); onDismissTransferNotif() }
            )
        }
    }
}
