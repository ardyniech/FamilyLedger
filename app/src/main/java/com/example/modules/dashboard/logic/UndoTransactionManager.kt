package com.example.modules.dashboard.logic

import com.example.shared.models.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UndoTransactionManager {
    private val _lastDeletedTx = MutableStateFlow<Transaction?>(null)
    val lastDeletedTx: StateFlow<Transaction?> = _lastDeletedTx.asStateFlow()

    fun setDeletedTransaction(tx: Transaction) {
        _lastDeletedTx.value = tx
    }

    fun clearLastDeleted() {
        _lastDeletedTx.value = null
    }
}
