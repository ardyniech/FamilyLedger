package com.example.core.sync.p2p

data class P2PImportResult(
    val success: Boolean,
    val importedTransactions: Int,
    val importedWallets: Int,
    val importedCategories: Int,
    val message: String
)
