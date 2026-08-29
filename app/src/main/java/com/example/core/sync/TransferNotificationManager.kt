package com.example.core.sync

import com.example.shared.models.TransferNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class TransferNotificationManager {
    private val _notifications = MutableStateFlow<List<TransferNotification>>(emptyList())
    val notifications: StateFlow<List<TransferNotification>> = _notifications.asStateFlow()

    private val _activeBanner = MutableStateFlow<TransferNotification?>(null)
    val activeBanner: StateFlow<TransferNotification?> = _activeBanner.asStateFlow()

    fun createTransferNotification(
        senderId: String,
        senderName: String,
        senderRole: String,
        recipientId: String,
        recipientName: String,
        recipientRole: String,
        amount: Long,
        note: String,
        fromWalletName: String,
        toWalletName: String
    ): TransferNotification {
        val notif = TransferNotification(
            id = UUID.randomUUID().toString(),
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            recipientId = recipientId,
            recipientName = recipientName,
            recipientRole = recipientRole,
            amount = amount,
            note = note,
            fromWalletName = fromWalletName,
            toWalletName = toWalletName,
            timestamp = System.currentTimeMillis()
        )
        _notifications.value = listOf(notif) + _notifications.value
        _activeBanner.value = notif
        return notif
    }

    fun confirmTransfer(notificationId: String, emojiReaction: String) {
        _notifications.value = _notifications.value.map { item ->
            if (item.id == notificationId) {
                item.copy(
                    status = "CONFIRMED",
                    selectedEmoji = emojiReaction,
                    confirmedAt = System.currentTimeMillis()
                )
            } else {
                item
            }
        }
        val updated = _notifications.value.find { it.id == notificationId }
        _activeBanner.value = updated
    }

    fun dismissBanner() {
        _activeBanner.value = null
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
        if (_activeBanner.value?.id == id) {
            _activeBanner.value = null
        }
    }
}
