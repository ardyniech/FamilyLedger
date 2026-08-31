package com.example.core.storage

import com.example.shared.models.Transaction

class TransactionAuditWriter(
    private val dao: HouseholdDao,
    private val ledgerEngine: LedgerEngineService
) {
    suspend fun addTransaction(transaction: Transaction, householdId: String, actorId: String) {
        val eventType = if (transaction.amount >= 0) "INCOME" else "EXPENSE"
        ledgerEngine.withHouseholdLock(householdId) {
            val ledgerEvent = ledgerEngine.createEvent(
                householdId = householdId,
                entityId = transaction.id,
                actorId = if (actorId.isNotEmpty()) actorId else transaction.memberId,
                deviceId = "DEVICE_LOCAL",
                eventType = eventType,
                amount = transaction.amount,
                reason = transaction.note
            )
            dao.addTransactionWithAudit(transaction, ledgerEvent)
        }
    }

    suspend fun deleteTransaction(transaction: Transaction, householdId: String, actorId: String, reason: String) {
        ledgerEngine.withHouseholdLock(householdId) {
            val ledgerEvent = ledgerEngine.createEvent(
                householdId = householdId,
                entityId = transaction.id,
                actorId = if (actorId.isNotEmpty()) actorId else transaction.memberId,
                deviceId = "DEVICE_LOCAL",
                eventType = "REVERSAL",
                amount = -transaction.amount,
                reason = reason,
                referenceEntityId = transaction.id
            )
            dao.deleteTransactionWithAudit(transaction, ledgerEvent)
        }
    }

    suspend fun updateTransaction(oldTx: Transaction, newTx: Transaction, householdId: String, actorId: String) {
        val eventType = if (newTx.amount >= 0) "INCOME" else "EXPENSE"
        ledgerEngine.withHouseholdLock(householdId) {
            val ledgerEvent = ledgerEngine.createEvent(
                householdId = householdId,
                entityId = newTx.id,
                actorId = if (actorId.isNotEmpty()) actorId else newTx.memberId,
                deviceId = "DEVICE_LOCAL",
                eventType = eventType,
                amount = newTx.amount,
                reason = "Update: ${newTx.note}",
                referenceEntityId = oldTx.id
            )
            dao.updateTransactionWithAudit(oldTx, newTx, ledgerEvent)
        }
    }

    suspend fun executeTransfer(debitTx: Transaction, creditTx: Transaction, transferEvent: TransferEventEntity, householdId: String) {
        FinancialInvariants.validateTransfer(debitTx, creditTx)
        ledgerEngine.withHouseholdLock(householdId) {
            val events = ledgerEngine.createEvents(
                householdId = householdId,
                specs = listOf(
                    EventSpec(
                        entityId = debitTx.id,
                        actorId = debitTx.memberId,
                        deviceId = "DEVICE_LOCAL",
                        eventType = "TRANSFER_INTERNAL",
                        amount = debitTx.amount,
                        reason = "Transfer to ${creditTx.walletId}"
                    ),
                    EventSpec(
                        entityId = creditTx.id,
                        actorId = creditTx.memberId,
                        deviceId = "DEVICE_LOCAL",
                        eventType = "TRANSFER_INTERNAL",
                        amount = creditTx.amount,
                        reason = "Transfer from ${debitTx.walletId}"
                    )
                )
            )
            dao.executeAtomicTransferWithAudit(debitTx, creditTx, transferEvent, events[0], events[1])
        }
    }
}
