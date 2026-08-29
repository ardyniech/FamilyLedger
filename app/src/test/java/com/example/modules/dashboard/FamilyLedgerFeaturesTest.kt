package com.example.modules.dashboard

import com.example.modules.dashboard.logic.*
import com.example.shared.models.*
import org.junit.Assert.*
import org.junit.Test

class FamilyLedgerFeaturesTest {

    @Test
    fun testCategoryGroupCalculator() {
        val groups = listOf(
            CategoryGroup("g1", "Operasional Rumah Tangga", "#3B82F6", "🏠"),
            CategoryGroup("g2", "Kewajiban Tetap", "#EF4444", "📑")
        )
        val categories = listOf(
            Category("c1", "Listrik", "Expense", groupId = "g1"),
            Category("c2", "KPR", "Expense", groupId = "g2"),
            Category("c3", "Lain-lain", "Expense", groupId = null)
        )
        val transactions = listOf(
            Transaction("t1", "w1", "m1", "c1", -500_000L, "Listrik", 1000L),
            Transaction("t2", "w1", "m1", "c2", -2_000_000L, "Cicilan KPR", 1000L),
            Transaction("t3", "w1", "m1", "c3", -100_000L, "Snack", 1000L)
        )

        val summaries = CategoryGroupCalculator.calculate(transactions, categories, groups)
        assertEquals(3, summaries.size)
        val kprGroup = summaries.find { it.group.id == "g2" }
        assertNotNull(kprGroup)
        assertEquals(2_000_000L, kprGroup!!.totalExpense)
    }

    @Test
    fun testSavingsIntegrityCalculator() {
        val categories = listOf(
            Category("c_save", "Tabungan Haji", "Savings", isSavings = true)
        )
        val transactions = listOf(
            Transaction("t1", "w1", "m1", "c_save", 5_000_000L, "Setoran Tabungan", 1000L),
            Transaction("t2", "w1", "m1", "c_save", -1_000_000L, "Tarik pakai tabungan", 2000L)
        )

        val report = SavingsIntegrityCalculator.calculate(transactions, categories)
        assertEquals(5_000_000L, report.totalSavingsAllocated)
        assertEquals(1_000_000L, report.totalSavingsUsed)
        assertEquals(4_000_000L, report.netSavingsRetained)
        assertEquals(80.0, report.overallIntegrityRate, 0.01)
        assertEquals(1, report.compromisedCount)
    }

    @Test
    fun testDebtLedgerCalculator() {
        val members = listOf(
            Member("m1", "h1", "Suami", "Budi"),
            Member("m2", "h1", "Istri", "Ani")
        )
        val wallets = listOf(
            WalletAccount("w_partner", "m2", "Gopay", "Gopay Istri", 0L)
        )
        val transactions = listOf(
            Transaction("t1", "w_partner", "m2", "cat_tf", 1_000_000L, "Transfer uang belanja", 1000L),
            Transaction("t2", "w_partner", "m2", "cat_exp", -300_000L, "Belanja Pasar", 2000L)
        )

        val ledger = DebtLedgerCalculator.calculate(wallets, members, transactions)
        val item = ledger.first()
        // Running Balance = (1.000.000) - (0) - (300.000) = 700.000 (Partner masih pegang uangmu)
        assertEquals(700_000L, item.netDebtBalance)
        assertEquals("Partner Pegang Uangmu", item.statusText)
    }

    @Test
    fun testTransferBudgetCap() {
        val targetWallet = WalletAccount("w2", "m2", "Bank", "BCA Istri", 0L, monthlyTransferCap = 5_000_000L)
        val transactions = listOf(
            Transaction("t1", "w2", "m2", "tf", 4_500_000L, "Transfer rutin", System.currentTimeMillis())
        )

        // Attempting to transfer 1.000.000 more (Total = 5.500.000 > 5.000.000 cap)
        val eval = TransferBudgetCapCalculator.evaluate(targetWallet, 1_000_000L, transactions)
        assertNotNull(eval)
        assertEquals(TransferCapStatus.EXCEEDED, eval!!.status)
    }

    @Test
    fun testCashflowHealthCalculator() {
        val transactions = listOf(
            Transaction("t1", "w1", "m1", "c_inc", 10_000_000L, "Gaji", System.currentTimeMillis()),
            Transaction("t2", "w1", "m1", "c_exp", -6_000_000L, "Pengeluaran", System.currentTimeMillis())
        )
        val summary = CashflowHealthCalculator.calculate(transactions, transactions)
        assertEquals(4_000_000L, summary.netCashflow)
        assertTrue(summary.isSurplus)
        assertEquals(40.0, summary.savingsRate, 0.01)
    }
}
