package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

data class CategoryTotal(
    val category: String,
    val totalAmount: Double
)

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<Expense>)

    @Delete
    suspend fun delete(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    suspend fun getAllExpensesList(): List<Expense>

    @Query("SELECT category, SUM(amount) as totalAmount FROM expenses GROUP BY category")
    fun getTotalExpensesByCategory(): Flow<List<CategoryTotal>>

    @Query("SELECT category, SUM(amount) as totalAmount FROM expenses GROUP BY category")
    suspend fun getTotalExpensesByCategoryList(): List<CategoryTotal>

    @Query("SELECT SUM(amount) FROM expenses WHERE category = :category")
    fun getTotalAmountForCategory(category: String): Flow<Double?>
}
