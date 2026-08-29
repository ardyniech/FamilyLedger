package com.example.modules.dashboard.dialogs

import androidx.compose.runtime.Composable
import com.example.modules.dashboard.AddTransactionModal
import com.example.modules.dashboard.DashboardViewModel
import com.example.shared.models.*

@Composable
fun DashboardDialogsHost(
    viewModel: DashboardViewModel,
    wallets: List<WalletAccount>,
    categories: List<Category>,
    members: List<Member>,
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
    if (showAddModal) {
        AddTransactionModal(
            wallets = wallets, categories = categories,
            onDismiss = onDismissAddModal,
            onSubmit = { a, n, w, c, i, t -> viewModel.addTransaction(a, n, w, c, i, t) }
        )
    }
    selectedTxForDetail?.let { tx ->
        TransactionDetailDialog(
            transaction = tx,
            wallet = wallets.find { it.id == tx.walletId },
            category = categories.find { it.id == tx.categoryId },
            member = members.find { it.id == tx.memberId },
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
