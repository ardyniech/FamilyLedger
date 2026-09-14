package com.example.shared.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "household_expenses",
    indices = [
        Index(value = ["walletId"]),
        Index(value = ["categoryId"]),
        Index(value = ["memberId"]),
        Index(value = ["expenseDate"]),
        Index(value = ["syncStatus"])
    ]
)
data class HouseholdExpense(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Long, // Nilai pengeluaran dalam Rupiah
    val categoryId: String,
    val categoryName: String,
    val walletId: String,
    val walletName: String,
    val memberId: String,
    val memberName: String,
    val expenseDate: Long = System.currentTimeMillis(),
    val isNeed: Boolean = true, // Klasifikasi: Kebutuhan (true) vs Keinginan (false)
    val receiptPath: String = "",
    val notes: String = "",
    val syncStatus: Int = 0, // 0 = Lokal / Pending Sync, 1 = Synced
    val createdAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
