package com.example.modules.dashboard.logic

import com.example.shared.models.Category
import com.example.shared.models.CategoryGroup
import com.example.shared.models.Member
import com.example.shared.models.WalletAccount

object UserDataEntities {
    fun getMembers(pairCode: String = "FAM-8821"): List<Member> {
        val hId = pairCode
        return listOf(
            Member("m1", hId, "Suami", "Ardy", avatarUrl = ""),
            Member("m2", hId, "Istri", "Deina", avatarUrl = "")
        )
    }

    fun getCategoryGroups(): List<CategoryGroup> {
        return listOf(
            CategoryGroup("cg_op", "Operasional Rumah Tangga", "#3B82F6", "🏠", "Kebutuhan harian rumah tangga"),
            CategoryGroup("cg_ob", "Kewajiban Tetap (KPR/Cicilan)", "#EF4444", "📑", "Listrik, air, cicilan berkala"),
            CategoryGroup("cg_rel", "Investasi Hubungan", "#EC4899", "❤️", "Quality time & jajan bersama"),
            CategoryGroup("cg_per", "Pribadi", "#F59E0B", "👤", "Kebutuhan personal & self-care"),
            CategoryGroup("cg_sav", "Tabungan & Komitmen", "#10B981", "💰", "Simpanan masa depan & akad")
        )
    }

    fun getWallets(): List<WalletAccount> {
        return listOf(
            WalletAccount("w_cash", "m1", "Cash", "Cash (Tunai)", 0.0, monthlyTransferCap = 2000000.0),
            WalletAccount("w_gopay", "m1", "E-Wallet", "GoPay", 0.0, monthlyTransferCap = 1500000.0),
            WalletAccount("w_bca", "m1", "Bank", "BCA", 0.0, monthlyTransferCap = 5000000.0),
            WalletAccount("w_dana", "m1", "E-Wallet", "Dana", 0.0),
            WalletAccount("w_ovo", "m1", "E-Wallet", "OVO", 0.0),
            WalletAccount("w_kasbon", "m1", "Vault", "Kasbon", 0.0),
            WalletAccount("w_deina", "m2", "Bank", "Deina", 0.0, monthlyTransferCap = 3000000.0)
        )
    }

    fun getCategories(): List<Category> {
        return listOf(
            Category("c_gojek", "Gojek", "Income", groupId = "cg_op"),
            Category("c_maxim", "Maxim", "Income", groupId = "cg_op"),
            Category("c_grab", "Grab", "Income", groupId = "cg_op"),
            Category("c_offline", "Offline", "Income", groupId = "cg_op"),
            Category("c_makan", "Makan", "Expense", groupId = "cg_rel"),
            Category("c_jajan", "Jajan", "Expense", groupId = "cg_rel"),
            Category("c_jajan_bareng", "Jajan Bareng (Kita)", "Expense", groupId = "cg_rel"),
            Category("c_ngopi", "Ngopi", "Expense", groupId = "cg_per"),
            Category("c_rokok", "Rokok", "Expense", groupId = "cg_per"),
            Category("c_pln", "PLN", "Expense", groupId = "cg_ob"),
            Category("c_pdam", "PDAM", "Expense", groupId = "cg_ob"),
            Category("c_sewa_batre", "Sewa Batrei Polytron", "Expense", groupId = "cg_ob"),
            Category("c_perawatan_motor", "Perawatan Motor", "Expense", groupId = "cg_ob"),
            Category("c_transport", "Transportation", "Expense", groupId = "cg_op"),
            Category("c_maxim_saldo", "Maxim Saldo", "Expense", groupId = "cg_op"),
            Category("c_phone", "Phone / Pulsa", "Expense", groupId = "cg_per"),
            Category("c_skincare", "Skincare", "Expense", groupId = "cg_per"),
            Category("c_save1", "Save#1 (Awal Pacaran)", "Expense", groupId = "cg_sav", isSavings = true),
            Category("c_save2", "Save#2 (Komitmen)", "Expense", groupId = "cg_sav", isSavings = true),
            Category("c_pandawa", "Pandawa (Akad)", "Expense", groupId = "cg_sav", isSavings = true),
            Category("c_rumah", "Kebutuhan Rumah", "Expense", groupId = "cg_op"),
            Category("c_laundry", "Laundry", "Expense", groupId = "cg_op"),
            Category("c_sosial", "Sosial & Komunitas", "Expense", groupId = "cg_rel"),
            Category("c_family", "Family Support", "Expense", groupId = "cg_rel"),
            Category("c_health", "Health", "Expense", groupId = "cg_per"),
            Category("c_personal", "Personal Care", "Expense", groupId = "cg_per"),
            Category("c_shopping", "Shopping", "Expense", groupId = "cg_per"),
            Category("c_electronics", "Shopping Electronics", "Expense", groupId = "cg_per"),
            Category("c_admin_bank", "Admin Bank", "Expense", groupId = "cg_op"),
            Category("c_tf_out", "Transfer Out", "Transfer"),
            Category("c_tf_in", "Transfer In", "Transfer")
        )
    }
}
