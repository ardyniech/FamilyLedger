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
    fun testToCsv_escapesFormulaInjectionChars() {
        val expenses = listOf(
            HouseholdExpense(
                id = "exp-formula",
                title = "=SUM(A1:A100)",
                amount = 50_000L,
                categoryId = "cat-1",
                categoryName = "+FormulaCat",
                walletId = "wal-1",
                walletName = "@WalletName",
                memberId = "mem-1",
                memberName = "-MemberName",
                isNeed = true,
                notes = "=cmd|' /C calc'!A0"
            )
        )

        val csv = HouseholdExpenseCsvExporter.toCsv(expenses)
        assertTrue(csv.contains("'=SUM(A1:A100)"))
        assertTrue(csv.contains("'+FormulaCat"))
        assertTrue(csv.contains("'@WalletName"))
        assertTrue(csv.contains("'-MemberName"))
        assertTrue(csv.contains("'=cmd|' /C calc'!A0"))
    }

    @Test
    fun testToCsv_emptyList_returnsHeaderOnly() {
        val csv = HouseholdExpenseCsvExporter.toCsv(emptyList())
        assertEquals("ID,Tanggal,Judul,Kategori,Tipe,Anggota,Dompet,Nominal,Catatan\n", csv)
    }
}
