package com.example.modules.dashboard.logic

import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import kotlin.math.abs

data class WalletDebtLedgerItem(
    val wallet: WalletAccount,
    val owner: Member?,
    val totalTransferIn: Double,
    val totalTransferOut: Double,
    val totalExpensePaid: Double,
    val netDebtBalance: Double // (Transfer In) - (Transfer Out) - (Expense Paid)
) {
    // Positif: Partner masih pegang uangmu
    // Negatif: Pengeluaran melebihi transfer, kamu berhutang ke partner
    // Nol: Lunas / Imbang
    val statusText: String
        get() = when {
            netDebtBalance > 0 -> "Partner Pegang Uangmu"
            netDebtBalance < 0 -> "Hutang ke Partner (Reimburse)"
            else -> "Lunas / Terbayar"
        }
}

object DebtLedgerCalculator {
    fun calculate(
        wallets: List<WalletAccount>,
        members: List<Member>,
        transactions: List<Transaction>
    ): List<WalletDebtLedgerItem> {
        return wallets.map { wallet ->
            val owner = members.find { it.id == wallet.memberId }
            val walletTxs = transactions.filter { it.walletId == wallet.id }

            // Transfer In: Masuk ke wallet ini dari transfer
            val transferIn = walletTxs
                .filter { it.amount > 0 && (it.note.contains("transfer", ignoreCase = true) || it.categoryId.contains("tf", ignoreCase = true)) }
                .sumOf { it.amount }

            // Transfer Out: Keluar dari wallet ini untuk transfer
            val transferOut = walletTxs
                .filter { it.amount < 0 && (it.note.contains("transfer", ignoreCase = true) || it.categoryId.contains("tf", ignoreCase = true)) }
                .sumOf { abs(it.amount) }

            // Expense Paid: Pengeluaran ril yang dibayar menggunakan wallet ini
            val expensePaid = walletTxs
                .filter { it.amount < 0 && !it.note.contains("transfer", ignoreCase = true) && !it.categoryId.contains("tf", ignoreCase = true) }
                .sumOf { abs(it.amount) }

            val netBalance = transferIn - transferOut - expensePaid

            WalletDebtLedgerItem(
                wallet = wallet,
                owner = owner,
                totalTransferIn = transferIn,
                totalTransferOut = transferOut,
                totalExpensePaid = expensePaid,
                netDebtBalance = netBalance
            )
        }
    }
}
