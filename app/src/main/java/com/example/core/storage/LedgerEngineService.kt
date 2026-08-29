package com.example.core.storage

import java.util.UUID

data class BalanceBreakdown(
    val openingBalance: Long,
    val income: Long,
    val expenses: Long,
    val internalTransfers: Long,
    val adjustments: Long,
    val currentBalance: Long
)

class LedgerEngineService(private val auditDao: LedgerAuditDao) {

    suspend fun createEvent(
        householdId: String,
        entityId: String,
        actorId: String,
        deviceId: String,
        eventType: String,
        amount: Long,
        reason: String = "",
        referenceEntityId: String = "",
        currency: String = "IDR"
    ): LedgerEventEntity {
        val latest = auditDao.getLatestLedgerEvent(householdId)
        val prevHash = latest?.eventHash ?: "GENESIS_HOUSEHOLD_${householdId.hashCode()}"
        val logicalClock = (latest?.logicalClock ?: 0L) + 1L
        val eventId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val newHash = LedgerEventEntity.computeHash(
            prevHash, eventId, householdId, entityId, actorId, eventType, amount, reason, timestamp
        )

        return LedgerEventEntity(
            eventId = eventId,
            householdId = householdId,
            entityId = entityId,
            actorId = actorId,
            deviceId = deviceId,
            eventType = eventType,
            amount = amount,
            currency = currency,
            reason = reason,
            referenceEntityId = referenceEntityId,
            createdAt = timestamp,
            logicalClock = logicalClock,
            previousEventHash = prevHash,
            eventHash = newHash,
            syncStatus = 0,
            trustState = "PENDING"
        )
    }

    suspend fun recordEvent(
        householdId: String,
        entityId: String,
        actorId: String,
        deviceId: String,
        eventType: String,
        amount: Long,
        reason: String = "",
        referenceEntityId: String = "",
        currency: String = "IDR"
    ): LedgerEventEntity {
        val event = createEvent(
            householdId, entityId, actorId, deviceId, eventType, amount, reason, referenceEntityId, currency
        )
        auditDao.insertLedgerEvent(event)
        return event
    }

    suspend fun recordReversal(
        householdId: String,
        targetEntityId: String,
        actorId: String,
        deviceId: String,
        originalAmount: Long,
        reason: String
    ): LedgerEventEntity {
        return recordEvent(
            householdId = householdId,
            entityId = targetEntityId,
            actorId = actorId,
            deviceId = deviceId,
            eventType = "REVERSAL",
            amount = -originalAmount,
            reason = reason,
            referenceEntityId = targetEntityId
        )
    }

    fun computeBalanceBreakdown(events: List<LedgerEventEntity>, opening: Long = 5000000L): BalanceBreakdown {
        var income = 0L
        var expenses = 0L
        var internalTransfers = 0L
        var adjustments = 0L

        for (e in events) {
            when (e.eventType) {
                "INCOME" -> income += e.amount
                "EXPENSE" -> expenses += kotlin.math.abs(e.amount)
                "TRANSFER_INTERNAL" -> internalTransfers += e.amount
                "ADJUSTMENT", "REVERSAL" -> adjustments += e.amount
            }
        }

        val current = opening + income - expenses + adjustments
        return BalanceBreakdown(opening, income, expenses, internalTransfers, adjustments, current)
    }

    fun verifyHashChain(events: List<LedgerEventEntity>, householdId: String? = null): Boolean {
        val filtered = if (householdId != null) events.filter { it.householdId == householdId } else events
        val sorted = filtered.sortedBy { it.logicalClock }
        var expectedPrev = if (householdId != null) "GENESIS_HOUSEHOLD_${householdId.hashCode()}" else "GENESIS_HASH"

        for (e in sorted) {
            if (e.logicalClock == 1L && e.previousEventHash.startsWith("GENESIS_")) {
                expectedPrev = e.previousEventHash
            }
            if (e.previousEventHash != expectedPrev) return false
            val computed = LedgerEventEntity.computeHash(
                e.previousEventHash, e.eventId, e.householdId, e.entityId, e.actorId, e.eventType, e.amount, e.reason, e.createdAt
            )
            if (computed != e.eventHash) return false
            expectedPrev = e.eventHash
        }
        return true
    }
}

