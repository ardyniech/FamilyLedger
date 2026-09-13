package com.example.modules.dashboard.logic

enum class PantryStockLevel(val label: String, val emoji: String) {
    FULL("Stok Aman", "🟢"),
    MEDIUM("Cukup", "🟡"),
    LOW("Hampir Habis", "🔴")
}

data class KitchenPantryItem(
    val id: String,
    val name: String,
    val emoji: String,
    val category: String, // e.g. "Bahan Pokok", "Bumbu & Gas", "Kebersihan", "Anak"
    val stockLevel: PantryStockLevel,
    val estimatedDaysLeft: Int,
    val defaultRestockCost: Long,
    val isRestockNeeded: Boolean = stockLevel == PantryStockLevel.LOW
)

object KitchenPantryDefaults {
    fun getInitialKitchenItems(): List<KitchenPantryItem> {
        return listOf(
            KitchenPantryItem("p_beras", "Beras Ramos 5kg", "🍚", "Bahan Pokok", PantryStockLevel.MEDIUM, 8, 75000L),
            KitchenPantryItem("p_minyak", "Minyak Goreng 2L", "🍳", "Bahan Pokok", PantryStockLevel.LOW, 2, 34000L, isRestockNeeded = true),
            KitchenPantryItem("p_gas", "Gas Elpiji 3kg", "🔥", "Bumbu & Gas", PantryStockLevel.LOW, 3, 22000L, isRestockNeeded = true),
            KitchenPantryItem("p_telur", "Telur Ayam 1kg", "🥚", "Bahan Pokok", PantryStockLevel.MEDIUM, 5, 28000L),
            KitchenPantryItem("p_galon", "Air Galon Isi Ulang", "💧", "Bumbu & Gas", PantryStockLevel.FULL, 6, 7000L),
            KitchenPantryItem("p_sabun", "Deterjen & Cuci Piring", "🧼", "Kebersihan", PantryStockLevel.MEDIUM, 10, 35000L),
            KitchenPantryItem("p_bumbu", "Bawang & Cabai", "🧄", "Bumbu & Gas", PantryStockLevel.LOW, 2, 20000L, isRestockNeeded = true),
            KitchenPantryItem("p_susu", "Susu Anak / Balita", "🍼", "Anak", PantryStockLevel.FULL, 14, 120000L)
        )
    }
}
