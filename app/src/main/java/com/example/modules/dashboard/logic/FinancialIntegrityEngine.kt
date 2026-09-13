package com.example.modules.dashboard.logic

import com.example.shared.models.CashflowCadence
import com.example.shared.models.DebtRecord
import com.example.shared.models.RecurringBill

data class FinancialIntegrityReport(
    val clusters: List<BillCluster>,
    val activeCluster: BillCluster?,
    val requiredIncomeData: RequiredIncomeData,
    val recommendations: List<String>,
    val totalDebtHutang: Long,
    val totalMonthlyInstallments: Long
)

object FinancialIntegrityEngine {

    fun generateReport(
        bills: List<RecurringBill>,
        debts: List<DebtRecord>,
        liquidBalance: Long,
        cadence: CashflowCadence,
        totalIncomeThisMonth: Long = 0L,
        now: Long = System.currentTimeMillis()
    ): FinancialIntegrityReport {
        val clusters = BillClusterDetector.detectClusters(bills, debts, now)
        val activeCluster = clusters.firstOrNull()

        val totalHutang = debts.filter { it.isHutang && !it.isSettled }.sumOf { it.remainingAmount }
        val monthlyInstallments = debts.filter { it.isHutang && !it.isSettled }.sumOf {
            if (it.monthlyInstallment > 0L) it.monthlyInstallment else it.remainingAmount
        } + bills.filter { !it.isPaid }.sumOf { it.amount }

        val incomeData = RequiredIncomeCalculator.calculate(
            cadence = cadence,
            liquidBalance = liquidBalance,
            clusters = clusters,
            totalUpcomingMonthObligation = monthlyInstallments,
            totalIncomeThisMonth = totalIncomeThisMonth,
            now = now
        )

        val recommendations = mutableListOf<String>()

        if (activeCluster != null) {
            val hasKpr = activeCluster.items.any { it.title.contains("KPR", ignoreCase = true) }
            if (hasKpr) {
                recommendations.add("Prioritaskan pembayaran KPR Bank agar riwayat SLIK OJK tetap skor Kol 1 dan bebas denda.")
            }
            if (incomeData.shortfall > 0L) {
                recommendations.add("Kunci pos hiburan & belanja non-primer sampai tagihan tgl ${activeCluster.endDay} lunas.")
                recommendations.add("Gunakan fitur transfer internal jika ada cadangan di rekening lain.")
            } else {
                recommendations.add("Alokasikan saldo ke pos auto-debet sekarang agar tidak terpakai untuk pengeluaran impulsif.")
            }
        } else {
            recommendations.add("Jadwal tagihan tersebar merata tanpa penumpukan kritis bulan ini.")
            if (incomeData.debtServiceRatio > 35f) {
                recommendations.add("Porsi cicilan (DSR) mencapai ${incomeData.debtServiceRatio.toInt()}%. Hindari mengambil kredit baru.")
            }
        }

        return FinancialIntegrityReport(
            clusters = clusters,
            activeCluster = activeCluster,
            requiredIncomeData = incomeData,
            recommendations = recommendations,
            totalDebtHutang = totalHutang,
            totalMonthlyInstallments = monthlyInstallments
        )
    }
}
