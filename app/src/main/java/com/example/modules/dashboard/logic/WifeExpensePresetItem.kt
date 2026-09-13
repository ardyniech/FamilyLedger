package com.example.modules.dashboard.logic

data class WifeExpensePresetItem(
    val id: String,
    val emoji: String,
    val title: String,
    val defaultAmount: Long,
    val categoryKeyword: String,
    val defaultWalletType: String, // "Cash", "Bank", "E-Wallet"
    val defaultNote: String
)

object WifeExpensePresetDefaults {
    val items = listOf(
        WifeExpensePresetItem("wp_sayur", "🥬", "Sayur & Lauk", 35000L, "sayur", "Cash", "Belanja sayur & lauk pasar harian"),
        WifeExpensePresetItem("wp_sembako", "🍚", "Beras & Sembako", 150000L, "sembako", "Bank", "Beras & sembako bulanan"),
        WifeExpensePresetItem("wp_gas", "🔥", "Gas & Galon", 25000L, "gas", "Cash", "Isi ulang gas elpiji & air galon"),
        WifeExpensePresetItem("wp_anak", "🍼", "Susu & Anak", 120000L, "anak", "Bank", "Susu formula & perlengkapan anak"),
        WifeExpensePresetItem("wp_kebersihan", "🧼", "Sabun & Cuci", 45000L, "kebersihan", "E-Wallet", "Deterjen & sabun rumah tangga"),
        WifeExpensePresetItem("wp_jajan", "🧁", "Jajan Anak", 25000L, "jajan", "Cash", "Jajan santai bareng anak"),
        WifeExpensePresetItem("wp_skincare", "💄", "Self-Care Istri", 75000L, "skincare", "Bank", "Skincare & perawatan diri istri"),
        WifeExpensePresetItem("wp_arisan", "👥", "Arisan RT", 50000L, "sosial", "Cash", "Setoran arisan lingkungan / RT")
    )
}
