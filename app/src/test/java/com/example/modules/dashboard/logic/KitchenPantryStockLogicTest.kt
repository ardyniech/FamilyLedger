package com.example.modules.dashboard.logic

import org.junit.Assert.*
import org.junit.Test

class KitchenPantryStockLogicTest {

    @Test
    fun testPantryDefaultsIntegrity() {
        val items = KitchenPantryDefaults.getInitialKitchenItems()
        assertTrue(items.isNotEmpty())
        assertTrue(items.any { it.name.contains("Beras") })
        assertTrue(items.any { it.name.contains("Minyak") })
        assertTrue(items.any { it.name.contains("Gas") })

        val lowStockItems = items.filter { it.isRestockNeeded }
        assertTrue(lowStockItems.isNotEmpty())
    }

    @Test
    fun testRestockTransitionIntegrity() {
        val original = KitchenPantryItem("p_test", "Gas Elpiji 3kg", "🔥", "Bumbu & Gas", PantryStockLevel.LOW, 2, 22000L, isRestockNeeded = true)
        assertTrue(original.isRestockNeeded)

        val restocked = original.copy(stockLevel = PantryStockLevel.FULL, isRestockNeeded = false, estimatedDaysLeft = 14)
        assertFalse(restocked.isRestockNeeded)
        assertEquals(PantryStockLevel.FULL, restocked.stockLevel)
    }
}
