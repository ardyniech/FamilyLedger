package com.example.modules.dashboard.csv

data class ColumnMap(
    val dateCol: Int = 0,
    val typeCol: Int = 1,
    val amountCol: Int = 2,
    val categoryCol: Int = 3,
    val accountCol: Int = 4,
    val noteCol: Int = 5
)

object CsvColumnDetector {
    private val headerTokens = listOf(
        "date", "time", "amount", "category", "account", "note", "type",
        "tanggal", "waktu", "jumlah", "kategori", "dompet", "catatan", "jenis", "nominal"
    )

    fun isHeaderRow(firstRow: List<String>): Boolean {
        return firstRow.any { item -> headerTokens.any { tok -> item.lowercase().contains(tok) } }
    }

    fun detectFromHeader(header: List<String>): ColumnMap {
        var date = 0; var type = 1; var amount = 2; var cat = 3; var acc = 4; var note = 5
        header.forEachIndexed { i, col ->
            val lower = col.lowercase()
            when {
                lower.contains("date") || lower.contains("time") || lower.contains("waktu") || lower.contains("tanggal") -> date = i
                lower.contains("type") || lower.contains("jenis") || lower.contains("tipe") || lower.contains("flow") -> type = i
                lower.contains("amount") || lower.contains("nominal") || lower.contains("jumlah") || lower.contains("nilai") -> amount = i
                lower.contains("note") || lower.contains("desc") || lower.contains("memo") || lower.contains("ket") || lower.contains("catatan") -> note = i
                lower.contains("kategori") || lower.contains("pos") || lower == "category" || lower.startsWith("cat") -> cat = i
                lower.contains("acc") || lower.contains("wallet") || lower.contains("dompet") || lower.contains("rekening") -> acc = i
            }
        }
        return ColumnMap(date, type, amount, cat, acc, note)
    }

    fun detectHeuristically(rows: List<List<String>>): ColumnMap = ColumnMap(0, 1, 2, 3, 4, 5)
}
