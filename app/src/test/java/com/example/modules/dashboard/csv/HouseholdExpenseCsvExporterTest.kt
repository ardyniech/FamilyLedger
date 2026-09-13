package com.example.modules.dashboard.csv

import com.example.shared.models.HouseholdExpense
import org.junit.Assert.*
import org.junit.Test

class HouseholdExpenseCsvExporterTest {

    @Test
    fun testToCsv_formatsCorrectly_withEscapedChars() {
        val expenses = listOf(
            HouseholdExpense(
                id = "exp-1",
                title = "Beras & Minyak, Gula",
                amount = 120_000L,
                categoryId = "cat-groceries",
                categoryName = "Groceries, Food",
                walletId = "wal-bca",
                walletName = "BCA Rekening Bersama",
                memberId = "mem-ayah",
                memberName = "Ayah \"Kepala RT\"",
                isNeed = true,
                notes = "Belanja bulanan"
            )
        )

        val csv = HouseholdExpenseCsvExporter.toCsv(expenses)
        assertTrue(csv.contains("ID,Tanggal,Judul,Kategori,Tipe,Anggota,Dompet,Nominal,Catatan"))
        assertTrue(csv.contains("\"Beras & Minyak, Gula\""))
        assertTrue(csv.contains("\"Groceries, Food\""))
        assertTrue(csv.contains("\"Ayah \"\"Kepala RT\"\"\""))
        assertTrue(csv.contains("Kebutuhan"))
        assertTrue(csv.contains("120000"))
    }

    @Test
    fun testToCsv_emptyList_returnsHeaderOnly() {
        val csv = HouseholdExpenseCsvExporter.toCsv(emptyList())
        assertEquals("ID,Tanggal,Judul,Kategori,Tipe,Anggota,Dompet,Nominal,Catatan\n", csv)
    }
}
