package com.example.core.storage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

data class BalanceBreakdown(
    val openingBalance: Double,
    val income: Double,
    val expenses: Double,
    val internalTransfers: Double,
    val adjustments: Double,
    val currentBalance: Double
)

class LedgerEngineService(private val auditDao: LedgerAuditDao) {

    suspend fun recordEvent(
        householdId: String,
        entityId: String,
        actorId: String,
        deviceId: String,
        eventType: String,
        amount: Double,
        currency: String = "IDR"
    ): LedgerEventEntity {
        val latest = auditDao.getLatestLedgerEvent()
        val prevHash = latest?.eventHash ?: "GENESIS_HASH"
        val logicalClock = (latest?.logicalClock ?: 0L) + 1L
        val eventId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val newHash = LedgerEventEntity.computeHash(
            prevHash, eventId, entityId, actorId, eventType, amount, timestamp
        )

        val event = LedgerEventEntity(
            eventId = eventId,
            householdId = householdId,
            entityId = entityId,
            actorId = actorId,
            deviceId = deviceId,
            eventType = eventType,
            amount = amount,
            currency = currency,
            createdAt = timestamp,
            logicalClock = logicalClock,
            previousEventHash = prevHash,
            eventHash = newHash,
            syncStatus = 0,
            trustState = "PENDING"
        )

        auditDao.insertLedgerEvent(event)
        return event
    }

    suspend fun recordReversal(
        householdId: String,
        targetEntityId: String,
        actorId: String,
        deviceId: String,
        originalAmount: Double,
        reason: String
    ) {
        // Reversal event negates the original amount
        recordEvent(
            householdId = householdId,
            entityId = targetEntityId,
            actorId = actorId,
            deviceId = deviceId,
            eventType = "REVERSAL",
            amount = -originalAmount
        )
    }

    fun computeBalanceBreakdown(events: List<LedgerEventEntity>, opening: Double = 5000000.0): BalanceBreakdown {
        var income = 0.0
        var expenses = 0.0
        var internalTransfers = 0.0
        var adjustments = 0.0

        for (e in events) {
            when (e.eventType) {
                "INCOME" -> income += e.amount
                "EXPENSE" -> expenses += kotlin.math.abs(e.amount)
                "TRANSFER_INTERNAL" -> internalTransfers += e.amount
                "ADJUSTMENT" -> adjustments += e.amount
                "REVERSAL" -> adjustments += e.amount // Reversal affects adjustment / balance correction
            }
        }

        val current = opening + income - expenses + adjustments
        return BalanceBreakdown(
            openingBalance = opening,
            income = income,
            expenses = expenses,
            internalTransfers = internalTransfers,
            adjustments = adjustments,
            currentBalance = current
        )
    }

    fun verifyHashChain(events: List<LedgerEventEntity>): Boolean {
        // Ordered ascending by logicalClock
        val sorted = events.sortedBy { it.logicalClock }
        var expectedPrev = "GENESIS_HASH"

        for (e in sorted) {
            if (e.previousEventHash != expectedPrev) return false
            val computed = LedgerEventEntity.computeHash(
                e.previousEventHash, e.eventId, e.entityId, e.actorId, e.eventType, e.amount, e.createdAt
            )
            if (computed != e.eventHash) return false
            expectedPrev = e.eventHash
        }
        return true
    }
}
