package com.example.modules.dashboard.logic

import com.example.shared.models.CashflowCadence
import java.util.Calendar

data class RequiredIncomeData(
    val cadence: CashflowCadence,
    val requiredAmount: Long,
    val unitLabel: String,
    val targetDays: Int,
    val totalObligation: Long,
    val liquidBalance: Long,
    val shortfall: Long,
    val debtServiceRatio: Float,
    val statusMessage: String,
    val urgencyLevel: Int // 0: Aman, 1: Waspada, 2: Kritis
)

object RequiredIncomeCalculator {

    fun calculate(
        cadence: CashflowCadence,
        liquidBalance: Long,
        clusters: List<BillCluster>,
        totalUpcomingMonthObligation: Long,
        totalIncomeThisMonth: Long,
        now: Long = System.currentTimeMillis()
    ): RequiredIncomeData {
        val cal = Calendar.getInstance().apply { timeInMillis = now }
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val daysRemainingInMonth = (maxDays - currentDay + 1).coerceAtLeast(1)

        val activeCluster = clusters.firstOrNull()
        val targetObligation = activeCluster?.totalAmount ?: totalUpcomingMonthObligation
        val daysToTarget = if (activeCluster != null) activeCluster.daysUntilStart.coerceAtLeast(1) else daysRemainingInMonth
        val shortfall = (targetObligation - liquidBalance).coerceAtLeast(0L)

        val requiredPerDay = if (daysToTarget > 0) shortfall / daysToTarget else shortfall
        val requiredPerWeek = requiredPerDay * 7
        val requiredPerMonth = if (liquidBalance < totalUpcomingMonthObligation) {
            totalUpcomingMonthObligation - liquidBalance
        } else totalUpcomingMonthObligation

        val dsr = if (totalIncomeThisMonth > 0L) {
            (totalUpcomingMonthObligation.toFloat() / totalIncomeThisMonth.toFloat()) * 100f
        } else {
            if (totalUpcomingMonthObligation > 0L) 40f else 0f
        }

        val (requiredAmount, unitLabel) = when (cadence) {
            CashflowCadence.DAILY -> Pair(requiredPerDay, "/ hari")
            CashflowCadence.WEEKLY -> Pair(requiredPerWeek, "/ minggu")
            CashflowCadence.MONTHLY -> Pair(requiredPerMonth, "/ bulan")
        }

        val urgencyLevel = when {
            shortfall > 0L && daysToTarget <= 5 -> 2 // Kritis: Saldo kurang & tagihan dekat!
            shortfall > 0L || (activeCluster != null && liquidBalance < activeCluster.totalAmount * 1.2) -> 1 // Waspada
            else -> 0 // Aman
        }

        val statusMessage = when (urgencyLevel) {
            2 -> "⚠️ Darurat: Saldo Anda kurang Rp %,d dari tagihan terdekat! Wajib raih Rp %,d%s dalam %d hari.".format(shortfall, requiredAmount, unitLabel, daysToTarget)
            1 -> "⚡ Persiapan: Ada cluster tagihan Rp %,d. Sisihkan rata-rata Rp %,d%s agar cashflow tetap stabil.".format(targetObligation, requiredAmount, unitLabel)
            else -> "✅ Aman: Saldo likuid mencukupi kewajiban cluster terdekat. Pertahankan ritme cashflow Anda."
        }

        return RequiredIncomeData(
            cadence = cadence,
            requiredAmount = requiredAmount,
            unitLabel = unitLabel,
            targetDays = daysToTarget,
            totalObligation = targetObligation,
            liquidBalance = liquidBalance,
            shortfall = shortfall,
            debtServiceRatio = dsr,
            statusMessage = statusMessage,
            urgencyLevel = urgencyLevel
        )
    }
}
