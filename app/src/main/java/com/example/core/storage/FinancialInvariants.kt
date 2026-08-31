package com.example.core.storage

import com.example.shared.models.Transaction

object FinancialInvariants {
    class InvariantViolationException(message: String) : IllegalArgumentException(message)

    fun validateTransfer(debitTx: Transaction, creditTx: Transaction) {
        if (debitTx.walletId == creditTx.walletId) {
            throw InvariantViolationException("Transfer invariant failed: Debit wallet (${debitTx.walletId}) and credit wallet (${creditTx.walletId}) must be different")
        }
        if (debitTx.amount >= 0L) {
            throw InvariantViolationException("Transfer invariant failed: Debit transaction amount must be negative, got ${debitTx.amount}")
        }
        if (creditTx.amount <= 0L) {
            throw InvariantViolationException("Transfer invariant failed: Credit transaction amount must be positive, got ${creditTx.amount}")
        }
        if (debitTx.amount != -creditTx.amount) {
            throw InvariantViolationException("Transfer invariant failed: Debit amount (${debitTx.amount}) must equal negative credit amount (-${creditTx.amount})")
        }
    }
}
