package com.example.modules.dashboard.ai

import com.example.shared.models.Category

object CategorySuggestionEngine {
    private val keywordMap = mapOf(
        "kopi" to listOf("Makanan & Minuman", "Jajan", "Makan"),
        "makan" to listOf("Makanan & Minuman", "Jajan", "Makan Siang"),
        "bensin" to listOf("Transportasi", "Kendaraan", "BBM"),
        "listrik" to listOf("Tagihan & Utilitas", "Listrik", "Rumah"),
        "gaji" to listOf("Gaji", "Pemasukan Utama"),
        "shopee" to listOf("Belanja", "E-Commerce"),
        "tokopedia" to listOf("Belanja", "E-Commerce"),
        "saham" to listOf("Investasi", "Tabungan"),
        "obat" to listOf("Kesehatan", "Apotek"),
        "pulsa" to listOf("Tagihan & Utilitas", "Pulsa & Paket Data")
    )

    fun suggestCategory(note: String, categories: List<Category>): List<Category> {
        if (note.isBlank()) return emptyList()
        val lowerNote = note.lowercase()
        val matchedNames = mutableListOf<String>()

        for ((key, names) in keywordMap) {
            if (lowerNote.contains(key)) {
                matchedNames.addAll(names)
            }
        }

        val directMatches = categories.filter { cat ->
            matchedNames.any { m -> cat.name.contains(m, ignoreCase = true) } ||
            lowerNote.contains(cat.name.lowercase())
        }

        return if (directMatches.isNotEmpty()) directMatches.distinct() else categories.take(2)
    }
}
