package com.example.modules.dashboard.csv

object CsvPatternMatcher {
    fun parseAmount(amountStr: String): Double {
        val clean = amountStr.trim().removeSurrounding("\"")
            .replace("Rp", "", ignoreCase = true)
            .replace("IDR", "", ignoreCase = true)
            .replace(" ", "")

        val isNegative = clean.startsWith("-") || clean.contains("(-)")
        val digitsOnly = clean.replace("(-)", "").replace("(+)", "").replace("(*)", "").replace("-", "")

        return try {
            val normalized = if (digitsOnly.contains(".") && digitsOnly.contains(",")) {
                if (digitsOnly.lastIndexOf(',') > digitsOnly.lastIndexOf('.')) {
                    digitsOnly.replace(".", "").replace(",", ".")
                } else {
                    digitsOnly.replace(",", "")
                }
            } else if (digitsOnly.count { it == '.' } > 1) {
                digitsOnly.replace(".", "")
            } else if (digitsOnly.count { it == ',' } > 1) {
                digitsOnly.replace(",", "")
            } else if (digitsOnly.contains(".") && digitsOnly.substringAfter(".").length == 3) {
                digitsOnly.replace(".", "")
            } else if (digitsOnly.contains(",") && digitsOnly.substringAfter(",").length == 3) {
                digitsOnly.replace(",", "")
            } else if (digitsOnly.contains(",") && digitsOnly.substringAfter(",").length <= 2) {
                digitsOnly.replace(",", ".")
            } else {
                digitsOnly
            }
            val num = normalized.toDoubleOrNull() ?: 0.0
            if (isNegative) -kotlin.math.abs(num) else kotlin.math.abs(num)
        } catch (_: Exception) {
            0.0
        }
    }

    fun detectType(typeStr: String, amount: Double, note: String): String {
        val lower = "$typeStr $note".lowercase()
        return when {
            lower.contains("transfer") || lower.contains("tf") || lower.contains("->") -> "Transfer"
            lower.contains("income") || lower.contains("(+)") || lower.contains("masuk") || lower.contains("pemasukan") || lower.contains("gaji") || lower.contains("bonus") -> "Income"
            lower.contains("expense") || lower.contains("(-)") || lower.contains("keluar") || lower.contains("pengeluaran") || amount < 0 -> "Expense"
            amount > 0 -> "Income"
            else -> "Expense"
        }
    }

    fun matchWallet(name: String, availableWallets: Map<String, String>): String {
        val clean = name.trim().lowercase()
        return availableWallets.entries.firstOrNull { (k, v) ->
            clean.contains(v.lowercase()) || v.lowercase().contains(clean)
        }?.key ?: (if (clean.contains("deina")) "w_deina" else "w_cash")
    }

    fun matchCategory(name: String, availableCategories: Map<String, String>): String {
        val clean = name.trim().lowercase()
        return availableCategories.entries.firstOrNull { (k, v) ->
            clean.contains(v.lowercase()) || v.lowercase().contains(clean)
        }?.key ?: "c_makan"
    }
}
