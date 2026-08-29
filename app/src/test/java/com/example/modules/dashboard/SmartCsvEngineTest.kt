package com.example.modules.dashboard

import com.example.modules.dashboard.csv.CsvPatternMatcher
import com.example.modules.dashboard.csv.SmartCsvParser
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartCsvEngineTest {

    private val sampleWallets = listOf(
        WalletAccount("w_cash", "m1", "Cash", "Cash", 100000L),
        WalletAccount("w_bca", "m1", "Bank", "BCA", 500000L),
        WalletAccount("w_deina", "m2", "Cash", "Deina", 200000L)
    )

    private val sampleCategories = listOf(
        Category("c_makan", "makan", "Expense"),
        Category("c_rokok", "rokok", "Expense"),
        Category("c_gojek", "gojek", "Income")
    )

    @Test
    fun testParseAugustLiveFormat() {
        val raw = """
            "Date","Type","Amount","Category","Account","Notes"
            "Aug 15, 2026 8:45 AM","(-) Expense","15000","makan","Cash","nasi uduk"
            "Aug 15, 2026 12:00 PM","(+) Income","250000","gojek","Cash","narik pagi"
            "Aug 15, 2026 3:00 PM","(*) Transfer","50000"," - ","Cash->Deina","titip belanja"
        """.trimIndent()

        val result = SmartCsvParser.parse(raw, sampleWallets, sampleCategories, emptyList())
        assertEquals("Should parse 3 records", 3, result.records.size)
        assertEquals("Expense parsed correctly", -15000L, result.records[0].amount)
        assertEquals("Income parsed correctly", 250000L, result.records[1].amount)
        assertTrue("Transfer detected", result.records[2].isTransfer)
        assertEquals("Target wallet matched", "w_deina", result.records[2].targetWalletId)
    }

    @Test
    fun testParseSemicolonAndRupiahFormat() {
        val raw = """
            Tanggal;Tipe;Nominal;Kategori;Dompet;Keterangan
            18/08/2026 09:00;Pengeluaran;Rp 20.000;rokok;Cash;surya
        """.trimIndent()

        val result = SmartCsvParser.parse(raw, sampleWallets, sampleCategories, emptyList())
        assertEquals("Should parse 1 record", 1, result.records.size)
        assertEquals("Amount formatted correctly", -20000L, result.records[0].amount)
    }

    @Test
    fun testDuplicateDetection() {
        val raw = """
            "Date","Type","Amount","Category","Account","Notes"
            "Aug 20, 2026 8:00 AM","(-) Expense","12000","rokok","Cash","surya"
        """.trimIndent()

        val existing = listOf(
            Transaction("tx1", "w_cash", "m1", "c_rokok", -12000L, "surya", timestamp = 1787216400000L)
        )

        val result = SmartCsvParser.parse(raw, sampleWallets, sampleCategories, existing)
        assertEquals(1, result.records.size)
    }

    @Test
    fun testBadInputHandling_EmptyAndCorrupt() {
        val resultEmpty = SmartCsvParser.parse("", sampleWallets, sampleCategories, emptyList())
        assertEquals(0, resultEmpty.records.size)

        val amount1 = CsvPatternMatcher.parseAmount("abc-invalid")
        assertEquals(0L, amount1)

        val amount2 = CsvPatternMatcher.parseAmount("Rp 1.500.000,00")
        assertEquals(1500000L, amount2)
    }
}
