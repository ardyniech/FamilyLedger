package com.example.shared.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "households",
    indices = [Index(value = ["pairCode"], unique = true)]
)
data class Household(
    @PrimaryKey val id: String,
    val pairCode: String,
    val syncStatus: Int = 0, // 0 = Pending, 1 = Synced
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

@Entity(
    tableName = "members",
    foreignKeys = [
        ForeignKey(
            entity = Household::class,
            parentColumns = ["id"],
            childColumns = ["householdId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["householdId"]),
        Index(value = ["syncStatus"])
    ]
)
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

@Entity(
    tableName = "wallet_accounts",
    indices = [
        Index(value = ["memberId"]),
        Index(value = ["syncStatus"])
    ]
)
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

@Entity(
    tableName = "category_groups",
    indices = [
        Index(value = ["syncStatus"])
    ]
)
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

@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["groupId"]),
        Index(value = ["parentId"]),
        Index(value = ["syncStatus"])
    ]
)
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

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = WalletAccount::class,
            parentColumns = ["id"],
            childColumns = ["walletId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["walletId"]),
        Index(value = ["categoryId"]),
        Index(value = ["memberId"]),
        Index(value = ["timestamp"]),
        Index(value = ["syncStatus"]),
        Index(value = ["goalId"])
    ]
)
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
    val isDeleted: Boolean = false,
    val goalId: String? = null
)
