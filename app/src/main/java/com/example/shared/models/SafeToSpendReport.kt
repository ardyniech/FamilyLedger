package com.example.shared.models

enum class BurnStatus(val label: String, val badgeDesc: String) {
    SAFE("Aman & Terkendali", "Pengeluaran hari ini jauh di bawah batas"),
    MODERATE("Perlu Waspada", "Mendekati batas aman belanja harian"),
    CRITICAL("Melampaui Batas", "Disarankan rem pengeluaran non-primer")
}

data class SafeToSpendReport(
    val dailySafeToSpend: Long,
    val spentToday: Long,
    val remainingSafeToday: Long,
    val monthlySafeToSpendRemaining: Long,
    val daysRemainingInMonth: Int,
    val committedBillsSum: Long,
    val committedLoansSum: Long,
    val goalsReservationSum: Long,
    val burnStatus: BurnStatus,
    val dailyBurnRatio: Float,
    val actionAdvice: String
)
