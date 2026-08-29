package com.example.core.storage

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class BalanceBreakdown(
    val openingBalance: Long,
    val income: Long,
    val expenses: Long,
    val internalTransfers: Long,
    val adjustments: Long,
    val currentBalance: Long
)

data class EventSpec(
    val entityId: String,
    val actorId: String,
    val deviceId: String = "DEVICE_LOCAL",
    val eventType: String,
    val amount: Long,
    val reason: String = "",
    val referenceEntityId: String = "",
    val currency: String = "IDR"
)

class LedgerEngineService(
    private val auditDao: LedgerAuditDao,
    private val database: RoomDatabase? = null
) {

    private val householdLocks = ConcurrentHashMap<String, Mutex>()

    private fun getHouseholdMutex(householdId: String): Mutex =
        householdLocks.getOrPut(householdId) { Mutex() }

    suspend fun <T> withHouseholdLock(householdId: String, block: suspend () -> T): T {
        return getHouseholdMutex(householdId).withLock {
            block()
        }
    }

    private suspend fun executeReadModifyWrite(householdId: String, specs: List<EventSpec>): List<LedgerEventEntity> {
        val latest = auditDao.getLatestLedgerEvent(householdId)
        var prevHash = latest?.eventHash ?: LedgerEventEntity.computeGenesisHash(householdId)
        var clock = (latest?.logicalClock ?: 0L)
        val result = mutableListOf<LedgerEventEntity>()

        for (spec in specs) {
            clock += 1L
            val eventId = UUID.randomUUID().toString()
            val timestamp = System.currentTimeMillis()
            val newHash = LedgerEventEntity.computeHash(
                prevHash, eventId, householdId, spec.entityId, spec.actorId, spec.eventType, spec.amount, spec.reason, timestamp
            )
            val event = LedgerEventEntity(
                eventId = eventId,
                householdId = householdId,
                entityId = spec.entityId,
                actorId = spec.actorId,
                deviceId = spec.deviceId,
                eventType = spec.eventType,
                amount = spec.amount,
                currency = spec.currency,
                reason = spec.reason,
                referenceEntityId = spec.referenceEntityId,
                createdAt = timestamp,
                logicalClock = clock,
                previousEventHash = prevHash,
                eventHash = newHash,
                syncStatus = 0,
                trustState = "PENDING"
            )
            result.add(event)
            prevHash = newHash
        }
        return result
    }

    suspend fun createEvents(
        householdId: String,
        specs: List<EventSpec>
    ): List<LedgerEventEntity> = withHouseholdLock(householdId) {
        val db = database
        if (db != null) {
            db.withTransaction {
                executeReadModifyWrite(householdId, specs)
            }
        } else {
            executeReadModifyWrite(householdId, specs)
        }
    }

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
        return createEvents(
            householdId,
            listOf(EventSpec(entityId, actorId, deviceId, eventType, amount, reason, referenceEntityId, currency))
        ).first()
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
    ): LedgerEventEntity = withHouseholdLock(householdId) {
        val event = createEvent(
            householdId, entityId, actorId, deviceId, eventType, amount, reason, referenceEntityId, currency
        )
        auditDao.insertLedgerEvent(event)
        event
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
        var income = 0L; var expenses = 0L; var internalTransfers = 0L; var adjustments = 0L
        for (e in events) {
            when (e.eventType) {
                "INCOME" -> income += e.amount
                "EXPENSE" -> expenses += kotlin.math.abs(e.amount)
                "TRANSFER_INTERNAL" -> internalTransfers += e.amount
                "ADJUSTMENT", "REVERSAL" -> adjustments += e.amount
            }
        }
        return BalanceBreakdown(opening, income, expenses, internalTransfers, adjustments, opening + income - expenses + adjustments)
    }

    fun verifyHashChain(events: List<LedgerEventEntity>, householdId: String? = null): Boolean {
        val filtered = if (householdId != null) events.filter { it.householdId == householdId } else events
        val sorted = filtered.sortedBy { it.logicalClock }
        var expectedPrev = if (householdId != null) LedgerEventEntity.computeGenesisHash(householdId) else "GENESIS_HASH"

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

