package com.example.modules.dashboard.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.modules.dashboard.AddTransactionModal
import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.WalletAccount
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w1080dp-h2400dp-xhdpi")
class DashboardUIInteractionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAddTransactionModalInteractions() {
        // Setup mock data
        val dummyWallets = listOf(
            WalletAccount(id = "w1", memberId = "m1", type = "Cash", name = "Cash Wallet", balance = 500000)
        )
        val dummyCategories = listOf(
            Category(id = "c1", name = "Food", type = "Expense", iconName = "fastfood")
        )
        val dummyMembers = listOf(
            Member(id = "m1", householdId = "h1", name = "Ardy", role = "Admin")
        )
        
        var isDismissed = false
        var savedAmount = 0L
        var savedNote = ""

        composeTestRule.setContent {
            AddTransactionModal(
                wallets = dummyWallets,
                categories = dummyCategories,
                onDismiss = { isDismissed = true },
                onSubmit = { amount, note, walletId, categoryId, isIncome, timestamp, goalId ->
                    savedAmount = amount
                    savedNote = note
                    isDismissed = true
                }
            )
        }
        
        composeTestRule.waitForIdle()

        // Interact with the numeric keypad
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText("0").performClick()
        composeTestRule.onNodeWithText("0").performClick()
        composeTestRule.onNodeWithText("0").performClick()
        composeTestRule.onNodeWithText("0").performClick()

        // Select Expense Toggle (Usually labeled "Pengeluaran")
        // composeTestRule.onNodeWithText("Pengeluaran").performClick() // Assuming it's default

        // Submit the transaction
        composeTestRule.onNodeWithTag("transaction_submit_button").performClick()

        composeTestRule.waitForIdle()
        
        // Check if the callback was invoked properly
        assert(isDismissed)
        assert(savedAmount == 50000L) // 50000 based on keypad input
    }
}
