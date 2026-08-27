package com.example.shared.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "households")
data class Household(
    @PrimaryKey val id: String,
    val pairCode: String,
    val syncStatus: Int = 0, // 0 = Pending, 1 = Synced
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

@Entity(tableName = "members")
data class Member(
    @PrimaryKey val id: String,
    val householdId: String,
    val role: String, // "Husband" or "Wife"
    val name: String,
    val avatarUrl: String = "",
    val syncStatus: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

@Entity(tableName = "wallet_accounts")
data class WalletAccount(
    @PrimaryKey val id: String,
    val memberId: String,
    val type: String, // Cash, EWallet, Bank, Vault
    val name: String,
    val balance: Double,
    val syncStatus: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // Income or Expense
    val iconName: String = "",
    val parentId: String? = null,
    val syncStatus: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: String,
    val walletId: String,
    val memberId: String, // Who created it
    val categoryId: String,
    val amount: Double,
    val note: String,
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

data class RecurringBill(
    val id: String,
    val name: String,
    val amount: Double,
    val dueDate: String, // e.g. "Aug 28, 2026"
    val categoryId: String,
    val isPaid: Boolean = false,
    val autoPay: Boolean = false,
    val targetWalletId: String? = null,
    val frequency: String = "Monthly", // "One-Time", "Daily", "Weekly", "Monthly", "Yearly"
    val lastProcessedTime: Long = 0L
)

data class FinancialGoal(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val category: String, // "Rumah", "Pendidikan", "Darurat", "Liburan", "Investasi"
    val iconEmoji: String = "🎯"
) {
    val isCompleted: Boolean
        get() = currentAmount >= targetAmount
}
