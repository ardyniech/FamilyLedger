package com.example.modules.dashboard

import com.example.modules.dashboard.logic.*
import com.example.shared.models.*
import org.junit.Assert.*
import org.junit.Test

class RolePersonalizationTest {

    @Test
    fun testRoleFallbackAndParsing() {
        assertEquals(HouseholdRole.SUAMI, HouseholdRole.fromString("suami"))
        assertEquals(HouseholdRole.SUAMI, HouseholdRole.fromString("Suami"))
        assertEquals(HouseholdRole.ISTRI, HouseholdRole.fromString("istri"))
        assertEquals(HouseholdRole.ISTRI, HouseholdRole.fromString("Istri"))
        assertEquals(HouseholdRole.SUAMI, HouseholdRole.fromString(""))
        assertEquals(HouseholdRole.SUAMI, HouseholdRole.fromString("other_role"))
    }

    @Test
    fun testRoleTemplateDefinitionAndHighlights() {
        for (role in HouseholdRole.entries) {
            val template = RoleTemplateCategoryData.getTemplate(role)
            assertEquals(role, template.role)
            assertTrue(template.groups.isNotEmpty())
            assertTrue(template.categories.isNotEmpty())
            assertTrue(template.sampleHighlights.isNotEmpty())
            assertTrue(role.shortTitle.isNotBlank())
            assertTrue(role.themeDescription.isNotBlank())
            assertTrue(role.tipAdvice.isNotBlank())
            assertTrue(role.templateFeedback.isNotBlank())
        }
    }

    @Test
    fun testHusbandAndWifeTemplateCategories() {
        val husbandCategories = RoleTemplateCategoryData.getCategoriesForRole(HouseholdRole.SUAMI)
        assertTrue(husbandCategories.any { it.name.contains("KPR") })
        assertTrue(husbandCategories.any { it.name.contains("Nafkah") })

        val wifeCategories = RoleTemplateCategoryData.getCategoriesForRole(HouseholdRole.ISTRI)
        assertTrue(wifeCategories.any { it.name.contains("Sayur") || it.name.contains("Sembako") })
        assertTrue(wifeCategories.any { it.name.contains("Anak") })

        val husbandGroups = RoleTemplateCategoryData.getGroupsForRole(HouseholdRole.SUAMI)
        assertTrue(husbandGroups.isNotEmpty())
        val wifeGroups = RoleTemplateCategoryData.getGroupsForRole(HouseholdRole.ISTRI)
        assertTrue(wifeGroups.isNotEmpty())
    }

    @Test
    fun testNafkahAllocationCalculator_WifePerspective() {
        val husband = Member(id = "m1", householdId = "h1", role = "Suami", name = "Budi")
        val wife = Member(id = "m2", householdId = "h1", role = "Istri", name = "Siti")
        val walletHusband = WalletAccount(id = "w1", memberId = "m1", type = "Bank", name = "Rekening Suami", balance = 10_000_000)
        val walletWife = WalletAccount(id = "w2", memberId = "m2", type = "Bank", name = "Rekening Istri", balance = 5_000_000)

        val catDapur = Category(id = "c1", name = "Belanja Dapur & Sayur", type = "Expense")
        val catListrik = Category(id = "c2", name = "Listrik & Air Rumah", type = "Expense")

        val transferTx = Transaction(
            id = "tx1", walletId = "w1", memberId = "m1", categoryId = "",
            amount = -4_000_000, note = "Transfer Uang Belanja ke Rekening Istri", timestamp = System.currentTimeMillis()
        )
        val expenseTx = Transaction(
            id = "tx2", walletId = "w2", memberId = "m2", categoryId = "c1",
            amount = -1_500_000, note = "Belanja Mingguan Pasar", timestamp = System.currentTimeMillis()
        )

        val report = NafkahAllocationCalculator.calculate(
            activeMember = wife,
            members = listOf(husband, wife),
            wallets = listOf(walletHusband, walletWife),
            transactions = listOf(transferTx, expenseTx),
            categories = listOf(catDapur, catListrik)
        )

        assertTrue(report.isWifeRole)
        assertEquals("Budi", report.husbandName)
        assertEquals("Siti", report.wifeName)
        assertEquals(4_000_000L, report.totalReceivedFromHusband)
        assertEquals(1_500_000L, report.totalHouseholdExpensesSpent)
        assertEquals(2_500_000L, report.remainingBudget)
        assertEquals(0.375f, report.spentPercentage, 0.01f)
    }

    @Test
    fun testNafkahAllocationCalculator_ZeroOrNegativeScenarios() {
        val husband = Member(id = "m1", householdId = "h1", role = "Suami", name = "Budi")
        val wife = Member(id = "m2", householdId = "h1", role = "Istri", name = "Siti")
        val wallets = listOf(
            WalletAccount(id = "w1", memberId = "m1", type = "Bank", name = "Rekening Suami", balance = 0),
            WalletAccount(id = "w2", memberId = "m2", type = "Bank", name = "Rekening Istri", balance = 0)
        )
        val catDapur = Category(id = "c1", name = "Belanja Dapur", type = "Expense")

        val expenseTx = Transaction(
            id = "tx1", walletId = "w2", memberId = "m2", categoryId = "c1",
            amount = -500_000, note = "Beli Beras", timestamp = System.currentTimeMillis()
        )

        val report = NafkahAllocationCalculator.calculate(
            activeMember = wife,
            members = listOf(husband, wife),
            wallets = wallets,
            transactions = listOf(expenseTx),
            categories = listOf(catDapur)
        )

        assertEquals(0L, report.totalReceivedFromHusband)
        assertEquals(500_000L, report.totalHouseholdExpensesSpent)
        assertEquals(-500_000L, report.remainingBudget)
        assertEquals(1.0f, report.spentPercentage, 0.01f)
    }

    @Test
    fun testRoleTheme_StrictlyLuminousLightPalette() {
        val suamiPalette = com.example.shared.theme.RoleThemePalette.suami()
        val istriPalette = com.example.shared.theme.RoleThemePalette.istri()

        assertEquals(androidx.compose.ui.graphics.Color(0xFFFFFFFF), suamiPalette.surfaceElevated)
        assertEquals(androidx.compose.ui.graphics.Color(0xFFFFFFFF), istriPalette.surfaceElevated)
        assertFalse(suamiPalette.isWifePink)
        assertTrue(istriPalette.isWifePink)
    }
}
