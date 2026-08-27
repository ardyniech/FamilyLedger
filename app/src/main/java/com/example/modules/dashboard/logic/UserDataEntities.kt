package com.example.modules.dashboard.logic

import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.WalletAccount

object UserDataEntities {
    fun getMembers(): List<Member> {
        val hId = "FAM-8821"
        return listOf(
            Member("m1", hId, "Husband", "Ardy", avatarUrl = ""),
            Member("m2", hId, "Wife", "Deina", avatarUrl = "")
        )
    }

    fun getWallets(): List<WalletAccount> {
        return listOf(
            WalletAccount("w_cash", "m1", "Cash", "Cash (Tunai)", 0.0),
            WalletAccount("w_gopay", "m1", "E-Wallet", "GoPay", 0.0),
            WalletAccount("w_bca", "m1", "Bank", "BCA", 0.0),
            WalletAccount("w_dana", "m1", "E-Wallet", "Dana", 0.0),
            WalletAccount("w_ovo", "m1", "E-Wallet", "OVO", 0.0),
            WalletAccount("w_kasbon", "m1", "Vault", "Kasbon", 0.0),
            WalletAccount("w_deina", "m2", "Bank", "Deina", 0.0)
        )
    }

    fun getCategories(): List<Category> {
        return listOf(
            Category("c_gojek", "Gojek", "Income"),
            Category("c_maxim", "Maxim", "Income"),
            Category("c_grab", "Grab", "Income"),
            Category("c_offline", "Offline", "Income"),
            Category("c_makan", "Makan", "Expense"),
            Category("c_jajan", "Jajan", "Expense"),
            Category("c_jajan_bareng", "Jajan Bareng (Kita)", "Expense"),
            Category("c_ngopi", "Ngopi", "Expense"),
            Category("c_rokok", "Rokok", "Expense"),
            Category("c_pln", "PLN", "Expense"),
            Category("c_pdam", "PDAM", "Expense"),
            Category("c_sewa_batre", "Sewa Batrei Polytron", "Expense"),
            Category("c_perawatan_motor", "Perawatan Motor", "Expense"),
            Category("c_transport", "Transportation", "Expense"),
            Category("c_maxim_saldo", "Maxim Saldo", "Expense"),
            Category("c_phone", "Phone / Pulsa", "Expense"),
            Category("c_skincare", "Skincare", "Expense"),
            Category("c_save1", "Save#1 (Awal Pacaran)", "Expense"),
            Category("c_save2", "Save#2 (Komitmen)", "Expense"),
            Category("c_pandawa", "Pandawa (Akad)", "Expense"),
            Category("c_rumah", "Kebutuhan Rumah", "Expense"),
            Category("c_laundry", "Laundry", "Expense"),
            Category("c_sosial", "Sosial & Komunitas", "Expense"),
            Category("c_family", "Family Support", "Expense"),
            Category("c_health", "Health", "Expense"),
            Category("c_personal", "Personal Care", "Expense"),
            Category("c_shopping", "Shopping", "Expense"),
            Category("c_electronics", "Shopping Electronics", "Expense"),
            Category("c_admin_bank", "Admin Bank", "Expense"),
            Category("c_tf_out", "Transfer Out", "Transfer"),
            Category("c_tf_in", "Transfer In", "Transfer")
        )
    }
}
