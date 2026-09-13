package com.example.core.storage

import androidx.room.*
import com.example.shared.models.HouseholdExpense
import kotlinx.coroutines.flow.Flow

@Dao
interface HouseholdExpenseDao {
    @Query("SELECT * FROM household_expenses WHERE isDeleted = 0 ORDER BY expenseDate DESC")
    fun getAllExpenses(): Flow<List<HouseholdExpense>>

    @Query("SELECT * FROM household_expenses WHERE id = :id AND isDeleted = 0 LIMIT 1")
    suspend fun getExpenseById(id: String): HouseholdExpense?

    @Query("SELECT * FROM household_expenses WHERE expenseDate BETWEEN :startDate AND :endDate AND isDeleted = 0 ORDER BY expenseDate DESC")
    fun getExpensesBetween(startDate: Long, endDate: Long): Flow<List<HouseholdExpense>>

    @Query("SELECT * FROM household_expenses WHERE categoryId = :categoryId AND isDeleted = 0 ORDER BY expenseDate DESC")
    fun getExpensesByCategory(categoryId: String): Flow<List<HouseholdExpense>>

    @Query("SELECT * FROM household_expenses WHERE memberId = :memberId AND isDeleted = 0 ORDER BY expenseDate DESC")
    fun getExpensesByMember(memberId: String): Flow<List<HouseholdExpense>>

    @Query("SELECT SUM(amount) FROM household_expenses WHERE isDeleted = 0")
    fun getTotalExpenseAmount(): Flow<Long?>

    @Query("SELECT SUM(amount) FROM household_expenses WHERE expenseDate BETWEEN :startDate AND :endDate AND isDeleted = 0")
    fun getTotalExpenseBetween(startDate: Long, endDate: Long): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: HouseholdExpense)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<HouseholdExpense>)

    @Update
    suspend fun updateExpense(expense: HouseholdExpense)

    @Query("UPDATE household_expenses SET isDeleted = 1, syncStatus = 0 WHERE id = :id")
    suspend fun softDeleteExpense(id: String)

    @Query("DELETE FROM household_expenses WHERE id = :id")
    suspend fun deleteExpensePermanently(id: String)

    @Query("DELETE FROM household_expenses")
    suspend fun clearAllExpenses()

    @Query("SELECT * FROM household_expenses WHERE syncStatus = 0")
    suspend fun getPendingExpenses(): List<HouseholdExpense>

    @Query("UPDATE household_expenses SET syncStatus = 1 WHERE id IN (:ids)")
    suspend fun markExpensesSynced(ids: List<String>)
}
