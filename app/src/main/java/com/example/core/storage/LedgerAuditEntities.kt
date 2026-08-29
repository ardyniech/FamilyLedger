package com.example.core.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.security.MessageDigest

@Entity(tableName = "ledger_events")
data class LedgerEvent(
    @PrimaryKey val eventId: String,
    val householdId: String,
    val entityId: String = "",
    val actorId: String,
    val deviceId: String,
    val eventType: String, // INCOME, EXPENSE, TRANSFER_INTERNAL, ADJUSTMENT, REVERSAL
    val amount: Long, // Minor unit (Long IDR)
    val currency: String = "IDR",
    val reason: String = "",
    val referenceEntityId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val logicalClock: Long = 1L,
    val previousEventHash: String = "",
    val eventHash: String,
    val syncStatus: Int = 0, // 0 = Pending, 1 = Synced
    val trustState: String = "PENDING" // SYNCED, PENDING, LOCAL_ONLY, CONFLICT, INTEGRITY_ISSUE
) {
    companion object {
        fun computeGenesisHash(householdId: String): String {
            val raw = "FAMILYLEDGER:GENESIS:$householdId"
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(raw.toByteArray(Charsets.UTF_8))
            return "GENESIS_" + hashBytes.joinToString("") { "%02x".format(it) }
        }

        fun computeHash(
            previousHash: String,
            eventId: String,
            householdId: String,
            entityId: String,
            actorId: String,
            eventType: String,
            amount: Long,
            reason: String,
            timestamp: Long
        ): String {
            val raw = "$previousHash|$eventId|$householdId|$entityId|$actorId|$eventType|$amount|$reason|$timestamp"
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(raw.toByteArray(Charsets.UTF_8))
            return hashBytes.joinToString("") { "%02x".format(it) }
        }
    }
}

typealias LedgerEventEntity = LedgerEvent

@Entity(tableName = "internal_transfers")
data class TransferEventEntity(
    @PrimaryKey val id: String,
    val sourceWalletId: String,
    val destinationWalletId: String,
    val amount: Long,
    val initiatedBy: String,
    val confirmedBy: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SETTLED", // PENDING, ACCEPTED, SETTLED
    val relationshipAcknowledgment: String = "❤️",
    val syncStatus: Int = 0
)

