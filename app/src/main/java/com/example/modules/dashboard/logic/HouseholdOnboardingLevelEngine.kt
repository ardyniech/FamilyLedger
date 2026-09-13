package com.example.modules.dashboard.logic

import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount

enum class OnboardingMaturityLevel(
    val levelNumber: Int,
    val title: String,
    val description: String,
    val unlockedBadge: String
) {
    LEVEL_1_FRESH(
        1,
        "Pondasi Awal",
        "Buat dompet & catat transaksi pertama Anda bersama pasangan.",
        "🌱 Langkah Pertama"
    ),
    LEVEL_2_ESSENTIAL(
        2,
        "Kebutuhan & Nafkah",
        "Fitur Alokasi Nafkah & Safe to Spend kini aktif.",
        "✨ Nafkah & Anggaran"
    ),
    LEVEL_3_ADVANCED(
        3,
        "Kekayaan & Integritas Finansial",
        "Radar Integritas, KPR Progress Tracker, & Simulasi aktif penuh.",
        "👑 Kekayaan Keluarga"
    )
}

data class HouseholdMaturityStatus(
    val level: OnboardingMaturityLevel,
    val transactionCount: Int,
    val walletCount: Int,
    val nextLevelRequirementText: String,
    val progressToNextLevel: Float,
    val shouldShowGuidedPrompt: Boolean
)

object HouseholdOnboardingLevelEngine {

    fun evaluate(
        transactions: List<Transaction>,
        wallets: List<WalletAccount>,
        hasBankLoanOrKpr: Boolean = false
    ): HouseholdMaturityStatus {
        val nonDeletedTxs = transactions.filter { !it.isDeleted }
        val activeWallets = wallets.filter { !it.isDeleted }
        val txCount = nonDeletedTxs.size
        val walletCount = activeWallets.size

        return when {
            walletCount == 0 || txCount < 3 -> {
                val progress = ((txCount.toFloat() / 3f) * 0.8f + (if (walletCount > 0) 0.2f else 0f)).coerceIn(0f, 1f)
                HouseholdMaturityStatus(
                    level = OnboardingMaturityLevel.LEVEL_1_FRESH,
                    transactionCount = txCount,
                    walletCount = walletCount,
                    nextLevelRequirementText = "Catat ${3 - txCount} transaksi lagi untuk membuka Alokasi Nafkah & Safe-to-Spend",
                    progressToNextLevel = progress,
                    shouldShowGuidedPrompt = true
                )
            }
            txCount in 3..9 && !hasBankLoanOrKpr -> {
                val progress = ((txCount - 3).toFloat() / 7f).coerceIn(0f, 1f)
                HouseholdMaturityStatus(
                    level = OnboardingMaturityLevel.LEVEL_2_ESSENTIAL,
                    transactionCount = txCount,
                    walletCount = walletCount,
                    nextLevelRequirementText = "Catat ${10 - txCount} transaksi lagi untuk membuka Radar Integritas & KPR Tracker",
                    progressToNextLevel = progress,
                    shouldShowGuidedPrompt = false
                )
            }
            else -> {
                HouseholdMaturityStatus(
                    level = OnboardingMaturityLevel.LEVEL_3_ADVANCED,
                    transactionCount = txCount,
                    walletCount = walletCount,
                    nextLevelRequirementText = "Seluruh modul analisis keluarga telah aktif penuh.",
                    progressToNextLevel = 1.0f,
                    shouldShowGuidedPrompt = false
                )
            }
        }
    }
}
