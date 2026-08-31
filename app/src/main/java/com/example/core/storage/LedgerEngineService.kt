package com.example.core.storage

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class LedgerEngineService(
    private val auditDao: LedgerAuditDao,
    private val database: RoomDatabase? = null
) {
    private val householdLocks = ConcurrentHashMap<String, Mutex>()

    private fun getHouseholdMutex(householdId: String): Mutex =
        householdLocks.getOrPut(householdId) { Mutex() }

    suspend fun <T> withHouseholdLock(householdId: String, block: suspend () -> T): T {
        return getHouseholdMutex(householdId).withLock { block() }
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
            result.add(
                LedgerEventEntity(
                    eventId = eventId, householdId = householdId, entityId = spec.entityId, actorId = spec.actorId,
                    deviceId = spec.deviceId, eventType = spec.eventType, amount = spec.amount, currency = spec.currency,
                    reason = spec.reason, referenceEntityId = spec.referenceEntityId, createdAt = timestamp, logicalClock = clock,
                    previousEventHash = prevHash, eventHash = newHash, syncStatus = 0, trustState = "PENDING"
                )
            )
            prevHash = newHash
        }
        return result
    }

    suspend fun createEvents(householdId: String, specs: List<EventSpec>): List<LedgerEventEntity> = withHouseholdLock(householdId) {
        val db = database
        if (db != null) {
            db.withTransaction { executeReadModifyWrite(householdId, specs) }
        } else {
            executeReadModifyWrite(householdId, specs)
        }
    }

    suspend fun createEvent(
        householdId: String, entityId: String, actorId: String, deviceId: String,
        eventType: String, amount: Long, reason: String = "", referenceEntityId: String = "", currency: String = "IDR"
    ): LedgerEventEntity {
        return createEvents(householdId, listOf(EventSpec(entityId, actorId, deviceId, eventType, amount, reason, referenceEntityId, currency))).first()
    }

    suspend fun recordEvent(
        householdId: String, entityId: String, actorId: String, deviceId: String,
        eventType: String, amount: Long, reason: String = "", referenceEntityId: String = "", currency: String = "IDR"
    ): LedgerEventEntity = withHouseholdLock(householdId) {
        val specs = listOf(EventSpec(entityId, actorId, deviceId, eventType, amount, reason, referenceEntityId, currency))
        val events = if (database != null) {
            database.withTransaction {
                val list = executeReadModifyWrite(householdId, specs)
                auditDao.insertLedgerEvents(list)
                list
            }
        } else {
            val list = executeReadModifyWrite(householdId, specs)
            auditDao.insertLedgerEvents(list)
            list
        }
        events.first()
    }

    suspend fun recordReversal(
        householdId: String, targetEntityId: String, actorId: String, deviceId: String,
        originalAmount: Long, reason: String
    ): LedgerEventEntity {
        return recordEvent(
            householdId = householdId, entityId = targetEntityId, actorId = actorId, deviceId = deviceId,
            eventType = "REVERSAL", amount = -originalAmount, reason = reason, referenceEntityId = targetEntityId
        )
    }

    fun computeBalanceBreakdown(events: List<LedgerEventEntity>, opening: Long = 5000000L): BalanceBreakdown =
        LedgerVerifierAndAnalytics.computeBalanceBreakdown(events, opening)

    fun verifyHashChain(events: List<LedgerEventEntity>, householdId: String? = null): Boolean =
        LedgerVerifierAndAnalytics.verifyHashChain(events, householdId)
}
