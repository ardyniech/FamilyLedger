package com.example.modules.dashboard

sealed class TransferState {
    object Idle : TransferState()
    object Loading : TransferState()
    object Success : TransferState()
    data class Error(val message: String) : TransferState()
}

sealed class TransactionState {
    object Idle : TransactionState()
    object Loading : TransactionState()
    object Success : TransactionState()
    data class Error(val message: String) : TransactionState()
}
