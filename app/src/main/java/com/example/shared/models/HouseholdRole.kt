package com.example.shared.models

enum class HouseholdRole(
    val code: String,
    val title: String,
    val shortTitle: String,
    val subtitle: String,
    val emoji: String,
    val description: String,
    val themeDescription: String,
    val tipAdvice: String,
    val templateFeedback: String
) {
    SUAMI(
        code = "Suami",
        title = "Suami (Kepala Rumah Tangga)",
        shortTitle = "Kepala Keluarga",
        subtitle = "Fokus: Nafkah Keluarga, Tanggungan Cicilan/KPR & Tagihan Pokok",
        emoji = "👨",
        description = "Tema profesional Cobalt & Slate Blue. Mengatur alokasi nafkah ke istri, cicilan KPR, listrik, pendidikan & dana darurat.",
        themeDescription = "Tema: Midnight Cobalt & Slate Blue",
        tipAdvice = "Kewajiban nafkah istri & cicilan KPR otomatis dipantau di dashboard suami.",
        templateFeedback = "Template Kepala Rumah Tangga & Tema Cobalt aktif! Menambahkan %d pos tanggung jawab (KPR, Nafkah, Listrik, Pendidikan)."
    ),
    ISTRI(
        code = "Istri",
        title = "Istri (Manajer Keuangan Rumah Tangga)",
        shortTitle = "Manajer Dapur",
        subtitle = "Fokus: Pengelolaan Uang Belanja dari Suami & Pos Dapur/Harian",
        emoji = "👩",
        description = "Tema cerah Blossom Pink & Peach. Khusus mengelola transfer nafkah belanja dari suami untuk pos kebutuhan rumah tangga, sembako & anak.",
        themeDescription = "Tema: Pink Blossom & Peach Cerah",
        tipAdvice = "Uang yang ditransfer suami otomatis menjadi alokasi anggaran belanja rumah tangga Anda.",
        templateFeedback = "Template Pengelola Rumah Tangga & Tema Pink aktif! Menambahkan %d pos belanja (Dapur, Sembako, Anak, Kebutuhan Rumah)."
    );

    companion object {
        fun fromString(value: String): HouseholdRole {
            return when (value.trim().lowercase()) {
                "istri", "wife", "ibu", "mama", "partner b", "pasangan 2" -> ISTRI
                else -> SUAMI
            }
        }
    }
}
