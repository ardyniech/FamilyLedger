package com.example.shared.models

enum class CashflowCadence(val label: String, val description: String) {
    DAILY("Harian", "Pedagang, driver, freelancer harian"),
    WEEKLY("Mingguan", "Pekerja proyek, usaha mingguan"),
    MONTHLY("Bulanan", "Gaji tetap karyawan, payroll");

    companion object {
        fun fromString(name: String): CashflowCadence {
            return entries.find { it.name.equals(name, ignoreCase = true) || it.label.equals(name, ignoreCase = true) }
                ?: MONTHLY
        }
    }
}
