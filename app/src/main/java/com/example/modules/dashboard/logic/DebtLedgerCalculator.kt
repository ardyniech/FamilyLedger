package com.example.modules.dashboard.logic

import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import kotlin.math.abs

data class WalletDebtLedgerItem(
    val wallet: WalletAccount,
    val owner: Member?,
    val totalTransferIn: Long,
    val totalTransferOut: Long,
    val totalExpensePaid: Long,
    val netDebtBalance: Long // (Transfer In) - (Transfer Out) - (Expense Paid)
) {
    val statusText: String
        get() = when {
            netDebtBalance > 0L -> "Partner Pegang Uangmu"
            netDebtBalance < 0L -> "Hutang ke Partner (Reimburse)"
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

            val transferIn = walletTxs
                .filter { it.amount > 0 && (it.note.contains("transfer", ignoreCase = true) || it.categoryId.contains("tf", ignoreCase = true)) }
                .sumOf { it.amount }

            val transferOut = walletTxs
                .filter { it.amount < 0 && (it.note.contains("transfer", ignoreCase = true) || it.categoryId.contains("tf", ignoreCase = true)) }
                .sumOf { abs(it.amount) }

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

