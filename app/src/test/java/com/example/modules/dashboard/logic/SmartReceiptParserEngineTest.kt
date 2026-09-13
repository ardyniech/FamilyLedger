package com.example.modules.dashboard.logic

import org.junit.Assert.*
import org.junit.Test

class SmartReceiptParserEngineTest {

    @Test
    fun testParseIndomaretReceipt() {
        val rawReceipt = """
            INDOMARET POINT
            JL. RAYA SUDIRMAN NO 12
            MINYAK GORENG 2L    34.000
            BERAS 5KG           72.500
            SABUN CUCI          14.000
            TOTAL = 120.500
            TUNAI = 150.000
            KEMBALI = 29.500
        """.trimIndent()

        val parsed = SmartReceiptParserEngine.parseReceiptText(rawReceipt)
        assertEquals("Indomaret", parsed.merchantName)
        assertEquals(120500L, parsed.totalAmount)
        assertTrue(parsed.isConfident)
        assertTrue(parsed.itemsSummary.contains("Minyak") || parsed.itemsSummary.contains("Beras"))
    }

    @Test
    fun testParsePasarTradisional() {
        val rawReceipt = """
            PASAR TRADISIONAL KEBAYORAN
            Bayam 2 ikat
            Tempe & tahu
            Total Belanja: 45.000
        """.trimIndent()

        val parsed = SmartReceiptParserEngine.parseReceiptText(rawReceipt)
        assertEquals("Pasar Tradisional", parsed.merchantName)
        assertEquals(45000L, parsed.totalAmount)
        assertEquals("Cash", parsed.suggestedWalletType)
        assertEquals("sayur", parsed.detectedCategoryKeyword)
    }

    @Test
    fun testMalformedOrEmptyReceiptTextFallback() {
        val emptyParsed = SmartReceiptParserEngine.parseReceiptText("")
        assertEquals(0L, emptyParsed.totalAmount)
        assertFalse(emptyParsed.isConfident)

        val gibberishParsed = SmartReceiptParserEngine.parseReceiptText("hallo ini cuma coretan tanpa angka")
        assertEquals(0L, gibberishParsed.totalAmount)
        assertFalse(gibberishParsed.isConfident)
        assertNotNull(gibberishParsed.merchantName)
    }
}
