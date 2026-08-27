package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FamilyLedger", appName)
  }

  @Test
  fun `evaluate math calculations accurately`() {
    val res1 = com.example.shared.utils.MathUtils.evaluateMath("10000+5000")
    assertEquals(15000.0, res1 ?: 0.0, 0.001)

    val res2 = com.example.shared.utils.MathUtils.evaluateMath("50000*2")
    assertEquals(100000.0, res2 ?: 0.0, 0.001)
  }

  @Test
  fun `financial goal completion state`() {
    val goal = com.example.shared.models.FinancialGoal(
      id = "g1",
      title = "Dana Darurat",
      targetAmount = 50000000.0,
      currentAmount = 50000000.0,
      category = "Tabungan",
      iconEmoji = "🛡️"
    )
    assertEquals(true, goal.isCompleted)
  }

  @Test
  fun `transaction sync status and domain integrity`() {
    val tx = com.example.shared.models.Transaction(
      id = "tx-123",
      walletId = "w1",
      memberId = "m1",
      categoryId = "c1",
      amount = -150000.0,
      note = "Belanja Mingguan",
      syncStatus = 0
    )
    assertEquals(0, tx.syncStatus)
    val syncedTx = tx.copy(syncStatus = 1)
    assertEquals(1, syncedTx.syncStatus)
    assertEquals(-150000.0, syncedTx.amount, 0.001)
  }

  @Test
  fun `auth user model and state transitions`() {
    val user = com.example.shared.models.AuthUser(
      uid = "firebase-uid-123",
      email = "user@example.com",
      displayName = "Ayah Budi",
      photoUrl = "https://example.com/photo.jpg"
    )
    assertEquals("firebase-uid-123", user.uid)
    assertEquals("Ayah Budi", user.displayName)

    val state: com.example.shared.models.AuthUiState = com.example.shared.models.AuthUiState.Authenticated(user)
    assertEquals(true, state is com.example.shared.models.AuthUiState.Authenticated)
    assertEquals("user@example.com", (state as com.example.shared.models.AuthUiState.Authenticated).user.email)
  }

  @Test
  fun `monthly budget tracking compares expenses against limit and goal safety`() {
    val budgetLimit = 10000000.0
    val expenses = listOf(
      com.example.shared.models.Transaction("t1", "w1", "m1", "c1", -1500000.0, "Dapur"),
      com.example.shared.models.Transaction("t2", "w2", "m2", "c2", -500000.0, "Bensin"),
      com.example.shared.models.Transaction("t3", "w1", "m1", "c3", -1200000.0, "Listrik & Air")
    )
    val totalExpenses = expenses.filter { it.amount < 0 }.sumOf { -it.amount }
    val remainingBudget = (budgetLimit - totalExpenses).coerceAtLeast(0.0)
    val progress = (totalExpenses / budgetLimit).toFloat()

    assertEquals(3200000.0, totalExpenses, 0.001)
    assertEquals(6800000.0, remainingBudget, 0.001)
    assertEquals(0.32f, progress, 0.01f)
    assertEquals(true, totalExpenses < budgetLimit)
  }
}
