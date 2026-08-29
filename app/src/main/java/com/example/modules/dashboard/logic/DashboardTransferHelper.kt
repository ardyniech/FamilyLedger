package com.example.modules.dashboard.logic

import com.example.core.storage.HouseholdRepository
import com.example.core.storage.TransferEventEntity
import com.example.core.sync.TransferNotificationManager
import com.example.shared.models.*
import java.util.UUID

object DashboardTransferHelper {
    suspend fun executeTransfer(
        amount: Long,
        note: String,
        fromWallet: WalletAccount,
        toWallet: WalletAccount,
        categories: List<Category>,
        members: List<Member>,
        repository: HouseholdRepository,
        notifManager: TransferNotificationManager
    ) {
        val isCross = fromWallet.memberId != toWallet.memberId
        val outType = if (isCross) "Expense" else "Transfer"
        val inType = if (isCross) "Income" else "Transfer"
        val outCat = categories.find { it.name == "Transfer Out" && it.type == outType } ?: Category(UUID.randomUUID().toString(), "Transfer Out", outType).also { repository.addCategory(it) }
        val inCat = categories.find { it.name == "Transfer In" && it.type == inType } ?: Category(UUID.randomUUID().toString(), "Transfer In", inType).also { repository.addCategory(it) }

        val debitTx = Transaction(UUID.randomUUID().toString(), fromWallet.id, fromWallet.memberId, outCat.id, -amount, note)
        val creditTx = Transaction(UUID.randomUUID().toString(), toWallet.id, toWallet.memberId, inCat.id, amount, note)

        val transferEvent = TransferEventEntity(
            id = UUID.randomUUID().toString(),
            sourceWalletId = fromWallet.id,
            destinationWalletId = toWallet.id,
            amount = amount,
            initiatedBy = fromWallet.memberId,
            confirmedBy = toWallet.memberId
        )

        repository.executeTransfer(debitTx, creditTx, transferEvent)

        val fromMember = members.find { it.id == fromWallet.memberId }
        val toMember = members.find { it.id == toWallet.memberId }
        if (fromMember != null && toMember != null && isCross) {
            notifManager.createTransferNotification(
                senderId = fromMember.id, senderName = fromMember.name, senderRole = fromMember.role,
                recipientId = toMember.id, recipientName = toMember.name, recipientRole = toMember.role,
                amount = amount, note = if (note.isBlank()) "Transfer Dana" else note,
                fromWalletName = fromWallet.name, toWalletName = toWallet.name
            )
        }
    }
}

