package com.example.shared.models

enum class LoanType(val label: String, val badgeColor: String) {
    KPR_MORTGAGE("KPR Rumah", "#4F46E5"),
    BANK_LOAN("Kredit Bank / KUR", "#0D9488"),
    VEHICLE_LOAN("Cicilan Kendaraan", "#F59E0B"),
    PAYLATER_CC("Paylater / Kartu Kredit", "#EC4899"),
    PERSONAL_DEBT("Hutang Personal", "#64748B");

    companion object {
        fun fromString(value: String): LoanType {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
                ?: PERSONAL_DEBT
        }
    }
}
