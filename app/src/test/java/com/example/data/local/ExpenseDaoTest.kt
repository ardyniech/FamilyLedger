package com.example.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.di.DatabaseModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExpenseDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ExpenseDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.expenseDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertAndGetAllExpenses_happyPath() = runBlocking {
        val expense = Expense(
            amount = 150000.0,
            category = "Groceries",
            date = System.currentTimeMillis(),
            description = "Supermarket shopping"
        )

        val id = dao.insertExpense(expense)
        assertTrue(id > 0)

        val allExpenses = dao.getAllExpenses().first()
        assertEquals(1, allExpenses.size)
        assertEquals("Groceries", allExpenses[0].category)
        assertEquals(150000.0, allExpenses[0].amount, 0.01)
        assertEquals("Supermarket shopping", allExpenses[0].description)
    }

    @Test
    fun testDeleteExpense_removesRow() = runBlocking {
        val expense = Expense(
            id = 1L,
            amount = 50000.0,
            category = "Transport",
            date = System.currentTimeMillis(),
            description = "Taxi"
        )
        dao.insert(expense)
        var list = dao.getAllExpenses().first()
        assertEquals(1, list.size)

        dao.delete(list[0])
        list = dao.getAllExpenses().first()
        assertTrue(list.isEmpty())
    }

    @Test
    fun testTotalExpensesByCategory_groupsAndSumsCorrectly() = runBlocking {
        dao.insert(Expense(amount = 100.0, category = "Food", date = 1000L, description = "Lunch"))
        dao.insert(Expense(amount = 200.0, category = "Food", date = 2000L, description = "Dinner"))
        dao.insert(Expense(amount = 50.0, category = "Bills", date = 3000L, description = "Water"))

        val totals = dao.getTotalExpensesByCategory().first()
        assertEquals(2, totals.size)

        val foodTotal = totals.find { it.category == "Food" }
        assertNotNull(foodTotal)
        assertEquals(300.0, foodTotal!!.totalAmount, 0.01)

        val billsTotal = totals.find { it.category == "Bills" }
        assertNotNull(billsTotal)
        assertEquals(50.0, billsTotal!!.totalAmount, 0.01)
    }

    @Test
    fun testEmptyDatabase_returnsEmptyListWithoutCrashing() = runBlocking {
        val expenses = dao.getAllExpenses().first()
        assertTrue(expenses.isEmpty())

        val totals = dao.getTotalExpensesByCategory().first()
        assertTrue(totals.isEmpty())
    }

    @Test
    fun testDatabaseModule_providesSingleton() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbInstance = DatabaseModule.provideAppDatabase(context)
        assertNotNull(dbInstance)
        val daoInstance = DatabaseModule.provideExpenseDao(dbInstance)
        assertNotNull(daoInstance)
    }
}
