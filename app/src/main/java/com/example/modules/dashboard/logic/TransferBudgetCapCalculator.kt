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
        transactions: List<Transaction>,
        cycleCutoffDay: Int = 1
    ): TransferCapEvaluation? {
        if (targetWallet == null || targetWallet.monthlyTransferCap <= 0L) return null

        val cap = targetWallet.monthlyTransferCap
        val (startMillis, endMillis) = computeCycleBounds(cycleCutoffDay)

        val currentMonthTransfers = transactions.filter { tx ->
            if (tx.walletId != targetWallet.id) return@filter false
            if (tx.amount <= 0L) return@filter false
            if (!tx.note.contains("transfer", ignoreCase = true) && !tx.categoryId.contains("tf", ignoreCase = true)) return@filter false
            tx.timestamp in startMillis..endMillis
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

    private fun computeCycleBounds(cutoffDay: Int): Pair<Long, Long> {
        val now = Calendar.getInstance()
        val currentDay = now.get(Calendar.DAY_OF_MONTH)
        val startCal = Calendar.getInstance()
        val endCal = Calendar.getInstance()

        if (cutoffDay <= 1) {
            startCal.set(Calendar.DAY_OF_MONTH, 1)
            startCal.set(Calendar.HOUR_OF_DAY, 0)
            startCal.set(Calendar.MINUTE, 0)
            startCal.set(Calendar.SECOND, 0)
            startCal.set(Calendar.MILLISECOND, 0)

            endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH))
            endCal.set(Calendar.HOUR_OF_DAY, 23)
            endCal.set(Calendar.MINUTE, 59)
            endCal.set(Calendar.SECOND, 59)
            endCal.set(Calendar.MILLISECOND, 999)
        } else {
            if (currentDay >= cutoffDay) {
                startCal.set(Calendar.DAY_OF_MONTH, cutoffDay)
                startCal.set(Calendar.HOUR_OF_DAY, 0)
                startCal.set(Calendar.MINUTE, 0)
                startCal.set(Calendar.SECOND, 0)
                startCal.set(Calendar.MILLISECOND, 0)

                endCal.add(Calendar.MONTH, 1)
                endCal.set(Calendar.DAY_OF_MONTH, cutoffDay - 1)
                endCal.set(Calendar.HOUR_OF_DAY, 23)
                endCal.set(Calendar.MINUTE, 59)
                endCal.set(Calendar.SECOND, 59)
                endCal.set(Calendar.MILLISECOND, 999)
            } else {
                startCal.add(Calendar.MONTH, -1)
                startCal.set(Calendar.DAY_OF_MONTH, cutoffDay)
                startCal.set(Calendar.HOUR_OF_DAY, 0)
                startCal.set(Calendar.MINUTE, 0)
                startCal.set(Calendar.SECOND, 0)
                startCal.set(Calendar.MILLISECOND, 0)

                endCal.set(Calendar.DAY_OF_MONTH, cutoffDay - 1)
                endCal.set(Calendar.HOUR_OF_DAY, 23)
                endCal.set(Calendar.MINUTE, 59)
                endCal.set(Calendar.SECOND, 59)
                endCal.set(Calendar.MILLISECOND, 999)
            }
        }
        return Pair(startCal.timeInMillis, endCal.timeInMillis)
    }
}
