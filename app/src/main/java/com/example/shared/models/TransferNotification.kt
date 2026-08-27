package com.example.shared.models

data class TransferNotification(
    val id: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String, // "Husband" or "Wife"
    val recipientId: String,
    val recipientName: String,
    val recipientRole: String, // "Wife" or "Husband"
    val amount: Double,
    val note: String,
    val fromWalletName: String,
    val toWalletName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING_CONFIRMATION", // PENDING_CONFIRMATION, CONFIRMED
    val selectedEmoji: String = "❤️",
    val confirmedAt: Long? = null
)
