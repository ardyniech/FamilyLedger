package com.example.core.storage

data class BalanceBreakdown(
    val openingBalance: Long,
    val income: Long,
    val expenses: Long,
    val internalTransfers: Long,
    val adjustments: Long,
    val currentBalance: Long
)

object LedgerVerifierAndAnalytics {
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
        return BalanceBreakdown(
            openingBalance = opening,
            income = income,
            expenses = expenses,
            internalTransfers = internalTransfers,
            adjustments = adjustments,
            currentBalance = opening + income - expenses + adjustments
        )
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
