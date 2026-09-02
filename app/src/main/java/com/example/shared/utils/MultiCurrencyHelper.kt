package com.example.shared.utils

import java.text.NumberFormat
import java.util.Locale

object MultiCurrencyHelper {
    enum class Currency(val code: String, val symbol: String, val rateToIdr: Double) {
        IDR("IDR", "Rp", 1.0),
        USD("USD", "$", 15800.0),
        EUR("EUR", "€", 17100.0),
        SGD("SGD", "S$", 11700.0),
        MYR("MYR", "RM", 3550.0),
        JPY("JPY", "¥", 105.0)
    }

    fun formatAmount(amountInIdr: Long, targetCurrency: Currency = Currency.IDR): String {
        if (targetCurrency == Currency.IDR) {
            val nf = NumberFormat.getInstance(Locale("id", "ID"))
            return "Rp ${nf.format(amountInIdr)}"
        }
        val converted = amountInIdr / targetCurrency.rateToIdr
        return "${targetCurrency.symbol} ${String.format(Locale.US, "%.2f", converted)}"
    }

    fun convertToIdr(amount: Double, sourceCurrency: Currency): Long {
        return (amount * sourceCurrency.rateToIdr).toLong()
    }
}
