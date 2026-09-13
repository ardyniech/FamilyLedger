package com.example.modules.dashboard

import com.example.modules.dashboard.logic.HouseholdOnboardingLevelEngine
import com.example.modules.dashboard.logic.OnboardingMaturityLevel
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HouseholdOnboardingLevelEngineTest {

    @Test
    fun testLevel1FreshStart() {
        val wallets = listOf(WalletAccount(id = "w1", name = "Cash", balance = 100000L, type = "CASH", memberId = "m1"))
        val txs = listOf(
            Transaction(id = "t1", walletId = "w1", memberId = "m1", categoryId = "c1", amount = 50000L, note = "Lunch")
        )

        val status = HouseholdOnboardingLevelEngine.evaluate(txs, wallets)

        assertEquals(OnboardingMaturityLevel.LEVEL_1_FRESH, status.level)
        assertTrue(status.shouldShowGuidedPrompt)
        assertEquals(1, status.transactionCount)
    }

    @Test
    fun testLevel2EssentialNafkah() {
        val wallets = listOf(WalletAccount(id = "w1", name = "Cash", balance = 100000L, type = "CASH", memberId = "m1"))
        val txs = (1..5).map {
            Transaction(id = "t$it", walletId = "w1", memberId = "m1", categoryId = "c1", amount = 10000L, note = "Note $it")
        }

        val status = HouseholdOnboardingLevelEngine.evaluate(txs, wallets)

        assertEquals(OnboardingMaturityLevel.LEVEL_2_ESSENTIAL, status.level)
        assertEquals(5, status.transactionCount)
    }

    @Test
    fun testLevel3AdvancedHouseholdWealth() {
        val wallets = listOf(WalletAccount(id = "w1", name = "Cash", balance = 100000L, type = "CASH", memberId = "m1"))
        val txs = (1..12).map {
            Transaction(id = "t$it", walletId = "w1", memberId = "m1", categoryId = "c1", amount = 10000L, note = "Note $it")
        }

        val status = HouseholdOnboardingLevelEngine.evaluate(txs, wallets)

        assertEquals(OnboardingMaturityLevel.LEVEL_3_ADVANCED, status.level)
        assertEquals(12, status.transactionCount)
    }
}
