package com.example.modules.dashboard.logic

import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import java.util.Calendar

enum class TransferCapStatus {
    SAFE,
    NEAR_LIMIT, // >= 80%
    EXCEEDED    // > 100%
}

data class TransferCapEvaluation(
    val monthlyCap: Long,
    val currentMonthTransfers: Long,
    val remainingCap: Long,
    val percentageUsed: Double,
    val status: TransferCapStatus,
    val warningMessage: String?
)

object TransferBudgetCapCalculator {
    fun evaluate(
        targetWallet: WalletAccount?,
        newTransferAmount: Long,
        transactions: List<Transaction>
    ): TransferCapEvaluation? {
        if (targetWallet == null || targetWallet.monthlyTransferCap <= 0L) return null

        val cap = targetWallet.monthlyTransferCap
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        val currentMonthTransfers = transactions.filter { tx ->
            if (tx.walletId != targetWallet.id) return@filter false
            if (tx.amount <= 0L) return@filter false
            if (!tx.note.contains("transfer", ignoreCase = true) && !tx.categoryId.contains("tf", ignoreCase = true)) return@filter false
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
            txCal.get(Calendar.MONTH) == currentMonth && txCal.get(Calendar.YEAR) == currentYear
        }.sumOf { it.amount }

        val projectedTotal = currentMonthTransfers + newTransferAmount
        val pct = (projectedTotal.toDouble() / cap.toDouble()) * 100.0
        val remaining = (cap - currentMonthTransfers).coerceAtLeast(0L)

        val status = when {
            projectedTotal > cap -> TransferCapStatus.EXCEEDED
            pct >= 80.0 -> TransferCapStatus.NEAR_LIMIT
            else -> TransferCapStatus.SAFE
        }

        val message = when (status) {
            TransferCapStatus.EXCEEDED -> "Transfer ini melebihi Plafon Bulanan dompet ${targetWallet.name} (${String.format("%.0f", pct)}% terpakai)!"
            TransferCapStatus.NEAR_LIMIT -> "Peringatan: Total transfer bulan ini mendekati Plafon (${String.format("%.0f", pct)}% terpakai)."
            TransferCapStatus.SAFE -> null
        }

        return TransferCapEvaluation(
            monthlyCap = cap,
            currentMonthTransfers = currentMonthTransfers,
            remainingCap = remaining,
            percentageUsed = pct,
            status = status,
            warningMessage = message
        )
    }
}

