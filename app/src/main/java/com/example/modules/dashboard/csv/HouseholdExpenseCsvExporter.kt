package com.example.modules.dashboard.csv

import com.example.shared.models.HouseholdExpense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object HouseholdExpenseCsvExporter {

    fun toCsv(expenses: List<HouseholdExpense>): String {
        val sb = StringBuilder()
        sb.append("ID,Tanggal,Judul,Kategori,Tipe,Anggota,Dompet,Nominal,Catatan\n")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        for (item in expenses) {
            val dateStr = dateFormat.format(Date(item.expenseDate))
            val typeStr = if (item.isNeed) "Kebutuhan" else "Keinginan"
            val safeTitle = escapeCsv(item.title)
            val safeCategory = escapeCsv(item.categoryName)
            val safeMember = escapeCsv(item.memberName)
            val safeWallet = escapeCsv(item.walletName)
            val safeNotes = escapeCsv(item.notes)

            sb.append("${item.id},$dateStr,$safeTitle,$safeCategory,$typeStr,$safeMember,$safeWallet,${item.amount},$safeNotes\n")
        }
        return sb.toString()
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}
