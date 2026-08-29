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
    val role: String, // e.g. "Suami", "Istri", or custom
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
    val balance: Long, // IDR minor unit (Integer rupiah)
    val monthlyTransferCap: Long = 0L,
    val syncStatus: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

@Entity(tableName = "category_groups")
data class CategoryGroup(
    @PrimaryKey val id: String,
    val name: String,
    val colorHex: String = "#3B82F6",
    val iconName: String = "📁",
    val description: String = "",
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
    val groupId: String? = null,
    val isSavings: Boolean = false,
    val syncStatus: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val budgetLimit: Long = 0L
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: String,
    val walletId: String,
    val memberId: String, // Who created it
    val categoryId: String,
    val amount: Long, // IDR rupiah, negative for expense, positive for income
    val note: String,
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

data class RecurringBill(
    val id: String,
    val name: String,
    val amount: Long,
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
    val targetAmount: Long,
    val currentAmount: Long,
    val category: String, // "Rumah", "Pendidikan", "Darurat", "Liburan", "Investasi"
    val iconEmoji: String = "🎯"
) {
    val isCompleted: Boolean
        get() = currentAmount >= targetAmount
}
