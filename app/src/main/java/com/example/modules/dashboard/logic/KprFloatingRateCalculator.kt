package com.example.modules.dashboard.logic

import kotlin.math.pow
import kotlin.math.roundToLong

data class KprFloatingRateAnalysis(
    val loanName: String,
    val remainingPrincipal: Long,
    val fixedInterestRate: Float,
    val floatingInterestRate: Float,
    val fixedMonthlyInstallment: Long,
    val floatingMonthlyInstallment: Long,
    val monthlyDifference: Long,
    val percentageSpike: Float,
    val monthsUntilFloating: Int,
    val severityLevel: KprFloatingSeverity,
    val recommendationText: String
)

enum class KprFloatingSeverity(val label: String, val colorHex: String) {
    SAFE("Masa Fixed Aman", "#10B981"),
    UPCOMING("Siaga Floating (H-12 bln)", "#F59E0B"),
    SHOCK_IMMINENT("Lonjakan Segera Datang (< 3 bln)", "#EF4444")
}

object KprFloatingRateCalculator {

    fun calculateAnnuity(principal: Long, annualRatePct: Float, tenorMonths: Int): Long {
        if (principal <= 0 || tenorMonths <= 0) return 0L
        val monthlyRate = (annualRatePct / 100f) / 12f
        if (monthlyRate <= 0f) return (principal / tenorMonths)
        val factor = (1f + monthlyRate).toDouble().pow(tenorMonths.toDouble())
        val installment = principal * (monthlyRate * factor) / (factor - 1.0)
        return installment.roundToLong()
    }

    fun analyze(
        loanName: String = "KPR Rumah Utama",
        remainingPrincipal: Long = 500_000_000L,
        fixedRatePct: Float = 5.5f,
        floatingRatePct: Float = 11.5f,
        remainingTenorMonths: Int = 180,
        monthsUntilFloating: Int = 8
    ): KprFloatingRateAnalysis {
        val fixedInstallment = calculateAnnuity(remainingPrincipal, fixedRatePct, remainingTenorMonths)
        val floatingInstallment = calculateAnnuity(remainingPrincipal, floatingRatePct, remainingTenorMonths)
        val diff = (floatingInstallment - fixedInstallment).coerceAtLeast(0L)
        val spike = if (fixedInstallment > 0) (diff.toFloat() / fixedInstallment.toFloat()) * 100f else 0f

        val severity = when {
            monthsUntilFloating <= 3 -> KprFloatingSeverity.SHOCK_IMMINENT
            monthsUntilFloating <= 12 -> KprFloatingSeverity.UPCOMING
            else -> KprFloatingSeverity.SAFE
        }

        val recommendation = when (severity) {
            KprFloatingSeverity.SHOCK_IMMINENT ->
                "Cicilan Anda diproyeksikan melonjak +Rp ${String.format("%,d", diff)}/bln (+${String.format("%.1f", spike)}%). Segera siapkan cadangan pos cashflow atau negosiasi refinancing bank."
            KprFloatingSeverity.UPCOMING ->
                "Sisa fixed tersisa $monthsUntilFloating bulan. Mulai sisihkan selisih Rp ${String.format("%,d", diff)}/bln ke dompet terpisah untuk adaptasi bertahap."
            KprFloatingSeverity.SAFE ->
                "Suku bunga fixed masih stabil ($monthsUntilFloating bulan tersisa). Tetap monitor suku bunga BI rate acuan."
        }

        return KprFloatingRateAnalysis(
            loanName = loanName,
            remainingPrincipal = remainingPrincipal,
            fixedInterestRate = fixedRatePct,
            floatingInterestRate = floatingRatePct,
            fixedMonthlyInstallment = fixedInstallment,
            floatingMonthlyInstallment = floatingInstallment,
            monthlyDifference = diff,
            percentageSpike = spike,
            monthsUntilFloating = monthsUntilFloating,
            severityLevel = severity,
            recommendationText = recommendation
        )
    }
}
