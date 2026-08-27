package com.example.modules.dashboard.logic

import com.example.shared.models.Transaction
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

object CsvDataConverter {
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.US)

    private val categoryMap = mapOf(
        "gojek" to "c_gojek",
        "maxim" to "c_maxim",
        "grab" to "c_grab",
        "offline" to "c_offline",
        "makan" to "c_makan",
        "jajan" to "c_jajan",
        "jajan bareng" to "c_jajan_bareng",
        "ngopi" to "c_ngopi",
        "rokok" to "c_rokok",
        "PLN" to "c_pln",
        "PDAM" to "c_pdam",
        "sewa Batrei polytron" to "c_sewa_batre",
        "perawatan motor" to "c_perawatan_motor",
        "Transportation" to "c_transport",
        "maxim saldo" to "c_maxim_saldo",
        "phone" to "c_phone",
        "skincare" to "c_skincare",
        "save#1" to "c_save1",
        "save#2" to "c_save2",
        "pandawa" to "c_pandawa",
        "kebutuhan rumah" to "c_rumah",
        "laundry" to "c_laundry",
        "sosial,komunitas" to "c_sosial",
        "iuran RT, sampah, dll" to "c_sosial",
        "family support" to "c_family",
        "Health" to "c_health",
        "personal care" to "c_personal",
        "Shopping" to "c_shopping",
        "shoping Electronics" to "c_electronics",
        "admin bank" to "c_admin_bank"
    )

    fun parseTimestamp(timeStr: String): Long {
        return try {
            dateFormat.parse(timeStr.trim())?.time ?: System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }

    fun mapWalletId(accountName: String): String {
        return when (accountName.trim().lowercase()) {
            "cash" -> "w_cash"
            "gopay" -> "w_gopay"
            "bca" -> "w_bca"
            "dana" -> "w_dana"
            "ovo" -> "w_ovo"
            "kasbon" -> "w_kasbon"
            "deina" -> "w_deina"
            else -> "w_cash"
        }
    }

    fun mapCategoryId(catName: String): String {
        return categoryMap[catName.trim()] ?: "c_makan"
    }

    fun mapMemberId(walletId: String): String {
        return if (walletId == "w_deina") "m2" else "m1"
    }
}
