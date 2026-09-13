package com.example.modules.dashboard.logic

import com.example.shared.models.WalletAccount

object WifeDefaultWallets {
    fun createDefaultWallets(memberId: String): List<WalletAccount> {
        return listOf(
            WalletAccount(
                id = "w_${memberId}_dapur",
                memberId = memberId,
                type = "Cash",
                name = "Kas Dapur (Tunai / Amplop)",
                balance = 0L,
                monthlyTransferCap = 3000000L
            ),
            WalletAccount(
                id = "w_${memberId}_bank",
                memberId = memberId,
                type = "Bank",
                name = "BCA (Rekening Deina / Nafkah)",
                balance = 0L,
                monthlyTransferCap = 5000000L
            ),
            WalletAccount(
                id = "w_${memberId}_spay",
                memberId = memberId,
                type = "E-Wallet",
                name = "ShopeePay / E-Wallet Belanja",
                balance = 0L,
                monthlyTransferCap = 2000000L
            ),
            WalletAccount(
                id = "w_${memberId}_cadangan",
                memberId = memberId,
                type = "Vault",
                name = "Dana Cadangan & Tabungan Dapur",
                balance = 0L
            )
        )
    }
}
