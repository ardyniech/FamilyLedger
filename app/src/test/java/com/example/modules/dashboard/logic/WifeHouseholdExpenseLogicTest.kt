package com.example.modules.dashboard.logic

import com.example.modules.dashboard.ai.CategorySuggestionEngine
import com.example.shared.models.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WifeHouseholdExpenseLogicTest {

    @Test
    fun testWifeDefaultWalletsCreation() {
        val memberId = "m_istri_test"
        val wallets = WifeDefaultWallets.createDefaultWallets(memberId)

        assertEquals(4, wallets.size)
        assertTrue(wallets.any { it.type == "Cash" && it.name.contains("Dapur") })
        assertTrue(wallets.any { it.type == "Bank" && it.name.contains("BCA") })
        assertTrue(wallets.any { it.type == "E-Wallet" && it.name.contains("ShopeePay") })
        assertTrue(wallets.any { it.type == "Vault" && it.name.contains("Cadangan") })
        wallets.forEach { assertEquals(memberId, it.memberId) }
    }

    @Test
    fun testWifeExpensePresetsIntegrity() {
        val presets = WifeExpensePresetDefaults.items
        assertTrue(presets.size >= 8)

        // Verify key daily kitchen expense presets exist
        assertNotNull(presets.find { it.categoryKeyword == "sayur" })
        assertNotNull(presets.find { it.categoryKeyword == "sembako" })
        assertNotNull(presets.find { it.categoryKeyword == "gas" })
        assertNotNull(presets.find { it.categoryKeyword == "anak" })

        presets.forEach { preset ->
            assertTrue(preset.defaultAmount > 0)
            assertTrue(preset.title.isNotBlank())
            assertTrue(preset.emoji.isNotBlank())
            assertTrue(preset.defaultNote.isNotBlank())
        }
    }

    @Test
    fun testCategorySuggestionForKitchenExpenses() {
        val categories = listOf(
            Category("c1", "Sayur Mayur & Lauk Pasar", "Expense"),
            Category("c2", "Beras & Sembako Bulanan", "Expense"),
            Category("c3", "Gas Elpiji & Bumbu Dapur", "Expense"),
            Category("c4", "Susu & Keperluan Anak", "Expense"),
            Category("c5", "Transportation", "Expense")
        )

        val sayurSuggestions = CategorySuggestionEngine.suggestCategory("beli sayur bayam dan tempe", categories)
        assertTrue(sayurSuggestions.any { it.name.contains("Sayur", ignoreCase = true) })

        val berasSuggestions = CategorySuggestionEngine.suggestCategory("beli beras pandan wangi", categories)
        assertTrue(berasSuggestions.any { it.name.contains("Beras", ignoreCase = true) })

        val gasSuggestions = CategorySuggestionEngine.suggestCategory("isi ulang gas tabung", categories)
        assertTrue(gasSuggestions.any { it.name.contains("Gas", ignoreCase = true) })

        val emptySuggestions = CategorySuggestionEngine.suggestCategory("", categories)
        assertTrue(emptySuggestions.isEmpty())
    }

    @Test
    fun testAdversarialUnknownKeywordsFallback() {
        val categories = listOf(
            Category("c1", "Sayur Mayur & Lauk Pasar", "Expense"),
            Category("c2", "Transportation", "Expense")
        )

        // Note with random gibberish
        val unknownSuggestions = CategorySuggestionEngine.suggestCategory("xyzabc999randomtext", categories)
        // Fallback returns top categories instead of crashing
        assertFalse(unknownSuggestions.isEmpty())
    }
}
