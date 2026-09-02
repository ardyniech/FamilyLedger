package com.example.modules.dashboard

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.core.auth.AuthManager
import com.example.core.auth.LocalAuthManager
import com.example.core.storage.AppDatabase
import com.example.core.storage.HouseholdRepository
import com.example.shared.models.Category
import com.example.shared.models.CategoryGroup
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DashboardViewModelTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: HouseholdRepository
    private lateinit var authManager: AuthManager
    private lateinit var viewModel: DashboardViewModel
    private lateinit var context: Context

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() = runBlocking {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext<Application>()

        // In-memory Database
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .setQueryExecutor { it.run() }
            .setTransactionExecutor { it.run() }
            .build()

        repository = HouseholdRepository(
            dao = db.householdDao(),
            auditDao = db.ledgerAuditDao(),
            categoryGroupDao = db.categoryGroupDao(),
            database = db
        )

        // Seed database synchronously
        com.example.modules.dashboard.logic.RealDataImporter.seedRealData(repository, "FAM-8821")

        val localAuth = LocalAuthManager(context)
        authManager = AuthManager(localAuth)

        viewModel = DashboardViewModel(repository, authManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        try {
            val method = androidx.lifecycle.ViewModel::class.java.getDeclaredMethod("onCleared")
            method.isAccessible = true
            method.invoke(viewModel)
        } catch (_: Exception) {}
        // Let cancellation propagate and background coroutines clean up
        runBlocking {
            try {
                kotlinx.coroutines.delay(100)
            } catch (_: Exception) {}
        }
        db.close()
        Dispatchers.resetMain()
    }

    @Test
    fun testJoinHouseholdUpdatesPairCode() = runTest {
        val testCode = "family123"
        viewModel.joinHousehold(testCode)
        advanceUntilIdle()

        // Assert that the state in view model is updated to uppercase
        assertEquals("FAMILY123", viewModel.householdPairCode.value)
    }

    @Test
    fun testAddTransactionStoresInDatabase() = runTest {
        // Keep active collectors for the entire test so the StateFlows update properly
        val collectJob1 = launch { viewModel.wallets.collect {} }
        val collectJob2 = launch { viewModel.categories.collect {} }

        // Wait until StateFlows actually receive the values from Room
        val wallets = viewModel.wallets.first { it.isNotEmpty() }
        val categories = viewModel.categories.first { it.isNotEmpty() }

        val wallet = wallets.first()
        val category = categories.first()

        // Since database is pre-seeded via RealDataImporter on viewModel init:
        val initialTxs = repository.transactions.first()

        // Add transaction via view model
        val amount = 150000L
        val note = "Groceries (Test Unit)"
        viewModel.addTransaction(
            amt = amount,
            note = note,
            wId = wallet.id,
            cId = category.id,
            isIncome = false
        )
        advanceUntilIdle()

        // Verify that transaction is added to database
        val txs = repository.transactions.first()
        assertEquals(initialTxs.size + 1, txs.size)
        val storedTx = txs.find { it.note == note }
        assertTrue(storedTx != null)
        assertEquals(-amount, storedTx!!.amount) // Expense should be stored as negative

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun testUpdateMemberRole() = runTest {
        // Get seeded member
        val member = repository.members.first().firstOrNull() ?: Member(id = "m1", householdId = "FAM-DEFAULT", role = "Suami", name = "Budi").also { repository.addMember(it) }
        advanceUntilIdle()

        val updatedMember = member.copy(role = "Ayah")
        viewModel.updateMemberRole(updatedMember)
        advanceUntilIdle()

        val members = repository.members.first()
        val updatedFromDb = members.find { it.id == member.id }
        assertEquals("Ayah", updatedFromDb?.role)
    }

    @Test
    fun testCategoryGroupPersistence() = runTest {
        val initialGroups = repository.categoryGroups.first()

        val group = CategoryGroup(id = "g_test_unit", name = "Rumah Tangga Unit Test")
        viewModel.saveCategoryGroup(group)
        advanceUntilIdle()

        val groups = repository.categoryGroups.first()
        assertEquals(initialGroups.size + 1, groups.size)
        assertTrue(groups.any { it.id == "g_test_unit" && it.name == "Rumah Tangga Unit Test" })

        viewModel.deleteCategoryGroup(group)
        advanceUntilIdle()

        val groupsAfterDelete = repository.categoryGroups.first()
        assertEquals(initialGroups.size, groupsAfterDelete.size)
        assertTrue(groupsAfterDelete.none { it.id == "g_test_unit" })
    }
}
