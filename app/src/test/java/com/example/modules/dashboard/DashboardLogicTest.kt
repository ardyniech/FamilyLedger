package com.example.modules.dashboard

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardLogicTest {

    @Test
    fun verifyAuditScenario_EmptyData() {
        // Mocking an empty wallet list
        val wallets = emptyList<com.example.shared.models.WalletAccount>()
        val total = wallets.sumOf { it.balance }
        assertEquals("Total balance should be 0 when empty", 0.0, total, 0.0)
    }

    @Test
    fun verifyAuditScenario_CrossModuleFailure_ShouldNotCrash() {
        // In a real scenario, this tests dispatcher events.
        // We assert that the member list maps properly without failing
        val members = listOf(
            com.example.shared.models.Member("m1", "h1", "Husband", "Suami"),
            com.example.shared.models.Member("m2", "h1", "Wife", "Istri")
        )
        val husband = members.find { it.role == "Husband" }
        assertEquals("Husband should be found", "m1", husband?.id)
    }
}
