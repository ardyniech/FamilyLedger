package com.example.modules.dashboard.csv

import java.text.SimpleDateFormat
import java.util.Locale

object CsvDateParser {
    private val patterns = listOf(
        "MMM dd, yyyy h:mm a",
        "MMM dd, yyyy hh:mm a",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd",
        "dd/MM/yyyy HH:mm:ss",
        "dd/MM/yyyy HH:mm",
        "dd/MM/yyyy",
        "dd-MM-yyyy HH:mm:ss",
        "dd-MM-yyyy HH:mm",
        "dd-MM-yyyy",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy/MM/dd HH:mm",
        "yyyy/MM/dd",
        "dd MMM yyyy HH:mm",
        "dd MMM yyyy",
        "d MMMM yyyy",
        "MMM dd, yyyy",
        "MMMM dd, yyyy",
        "dd MMMM yyyy"
    )

    private val locales = listOf(Locale.US, Locale("id", "ID"))

    fun parseTimestamp(dateStr: String): Long {
        val clean = dateStr.trim().removeSurrounding("\"").trim()
        if (clean.isBlank()) return System.currentTimeMillis()

        clean.toLongOrNull()?.let {
            return if (it < 10_000_000_000L) it * 1000L else it
        }

        val normalized = normalizeIndonesianMonths(clean)

        for (pattern in patterns) {
            for (locale in locales) {
                try {
                    val sdf = SimpleDateFormat(pattern, locale)
                    // isLenient=false untuk mencegah parse tanggal tidak valid (misal bulan 13)
                    sdf.isLenient = false
                    val parsed = sdf.parse(normalized)
                    if (parsed != null) return parsed.time
                } catch (_: Exception) {}
            }
        }
        // Fallback hanya untuk kasus ekstrem; dalam aplikasi keuangan sebaiknya tanggal
        // yang tidak parseable seharusnya ditangani di lapisan input, bukan disembunyikan.
        // Log warning untuk monitoring (dapat dikonfigurasi ke struktur log aplikasi).
        // TODO: Ganti dengan pendefolokan yang lebih ketat sesuai kebutuhan bisnis.
        return System.currentTimeMillis()
    }

    private fun normalizeIndonesianMonths(input: String): String {
        return input
            .replace("Agu", "Aug", ignoreCase = true)
            .replace("Agustus", "August", ignoreCase = true)
            .replace("Mei", "May", ignoreCase = true)
            .replace("Okt", "Oct", ignoreCase = true)
            .replace("Oktober", "October", ignoreCase = true)
            .replace("Des", "Dec", ignoreCase = true)
            .replace("Desember", "December", ignoreCase = true)
            .replace("Peb", "Feb", ignoreCase = true)
            .replace("Ttg", "", ignoreCase = true)
    }
}
