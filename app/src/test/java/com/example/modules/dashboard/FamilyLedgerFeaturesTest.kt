package com.example.modules.dashboard

import com.example.modules.dashboard.logic.*
import com.example.shared.models.*
import com.example.core.storage.LedgerEventEntity
import com.example.core.storage.TransferEventEntity
import org.junit.Assert.*
import org.junit.Test
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class FamilyLedgerFeaturesTest {

    @Test
    fun testCategoryGroupCalculator() {
        val groups = listOf(
            CategoryGroup("g1", "Operasional Rumah Tangga", "#3B82F6", "🏠"),
            CategoryGroup("g2", "Kewajiban Tetap", "#EF4444", "📑")
        )
        val categories = listOf(
            Category("c1", "Listrik", "Expense", groupId = "g1"),
            Category("c2", "KPR", "Expense", groupId = "g2"),
            Category("c3", "Lain-lain", "Expense", groupId = null)
        )
        val transactions = listOf(
            Transaction("t1", "w1", "m1", "c1", -500_000L, "Listrik", 1000L),
            Transaction("t2", "w1", "m1", "c2", -2_000_000L, "Cicilan KPR", 1000L),
            Transaction("t3", "w1", "m1", "c3", -100_000L, "Snack", 1000L)
        )

        val summaries = CategoryGroupCalculator.calculate(transactions, categories, groups)
        assertEquals(3, summaries.size)
        val kprGroup = summaries.find { it.group.id == "g2" }
        assertNotNull(kprGroup)
        assertEquals(2_000_000L, kprGroup!!.totalExpense)
    }

    @Test
    fun testSavingsIntegrityCalculator() {
        val categories = listOf(
            Category("c_save", "Tabungan Haji", "Savings", isSavings = true)
        )
        val transactions = listOf(
            Transaction("t1", "w1", "m1", "c_save", 5_000_000L, "Setoran Tabungan", 1000L),
            Transaction("t2", "w1", "m1", "c_save", -1_000_000L, "Tarik pakai tabungan", 2000L)
        )

        val report = SavingsIntegrityCalculator.calculate(transactions, categories)
        assertEquals(5_000_000L, report.totalSavingsAllocated)
        assertEquals(1_000_000L, report.totalSavingsUsed)
        assertEquals(4_000_000L, report.netSavingsRetained)
        assertEquals(80.0, report.overallIntegrityRate, 0.01)
        assertEquals(1, report.compromisedCount)
    }

    @Test
    fun testDebtLedgerCalculator() {
        val members = listOf(
            Member("m1", "h1", "Suami", "Budi"),
            Member("m2", "h1", "Istri", "Ani")
        )
        val wallets = listOf(
            WalletAccount("w_partner", "m2", "Gopay", "Gopay Istri", 0L)
        )
        val transactions = listOf(
            Transaction("t1", "w_partner", "m2", "cat_tf", 1_000_000L, "Transfer uang belanja", 1000L),
            Transaction("t2", "w_partner", "m2", "cat_exp", -300_000L, "Belanja Pasar", 2000L)
        )

        val ledger = DebtLedgerCalculator.calculate(wallets, members, transactions)
        val item = ledger.first()
        // Running Balance = (1.000.000) - (0) - (300.000) = 700.000 (Partner masih pegang uangmu)
        assertEquals(700_000L, item.netDebtBalance)
        assertEquals("Partner Pegang Uangmu", item.statusText)
    }

    @Test
    fun testTransferBudgetCap() {
        val targetWallet = WalletAccount("w2", "m2", "Bank", "BCA Istri", 0L, monthlyTransferCap = 5_000_000L)
        val transactions = listOf(
            Transaction("t1", "w2", "m2", "tf", 4_500_000L, "Transfer rutin", System.currentTimeMillis())
        )

        // Attempting to transfer 1.000.000 more (Total = 5.500.000 > 5.000.000 cap)
        val eval = TransferBudgetCapCalculator.evaluate(targetWallet, 1_000_000L, transactions)
        assertNotNull(eval)
        assertEquals(TransferCapStatus.EXCEEDED, eval!!.status)
    }

    @Test
    fun testCashflowHealthCalculator() {
        val transactions = listOf(
            Transaction("t1", "w1", "m1", "c_inc", 10_000_000L, "Gaji", System.currentTimeMillis()),
            Transaction("t2", "w1", "m1", "c_exp", -6_000_000L, "Pengeluaran", System.currentTimeMillis())
        )
        val summary = CashflowHealthCalculator.calculate(transactions, transactions)
        assertEquals(4_000_000L, summary.netCashflow)
        assertTrue(summary.isSurplus)
        assertEquals(40.0, summary.savingsRate, 0.01)
    }

    @Test
    fun testFinancialInvariantsValidation() {
        val debitTx = Transaction("t_deb", "w1", "m1", "c1", -500_000L, "Transfer out")
        val creditTx = Transaction("t_cred", "w2", "m1", "c1", 500_000L, "Transfer in")

        // Valid transfer should pass without exception
        com.example.core.storage.FinancialInvariants.validateTransfer(debitTx, creditTx)

        // Invalid transfer: asymmetric amount
        val invalidCredit = Transaction("t_cred2", "w2", "m1", "c1", 400_000L, "Mismatch")
        try {
            com.example.core.storage.FinancialInvariants.validateTransfer(debitTx, invalidCredit)
            fail("Should throw InvariantViolationException for mismatched amounts")
        } catch (e: com.example.core.storage.FinancialInvariants.InvariantViolationException) {
            assertTrue(e.message!!.contains("Debit amount"))
        }

        // Invalid transfer: same wallet
        val sameWalletCredit = Transaction("t_cred3", "w1", "m1", "c1", 500_000L, "Same wallet")
        try {
            com.example.core.storage.FinancialInvariants.validateTransfer(debitTx, sameWalletCredit)
            fail("Should throw InvariantViolationException for same wallet transfer")
        } catch (e: com.example.core.storage.FinancialInvariants.InvariantViolationException) {
            assertTrue(e.message!!.contains("must be different"))
        }
    }

    @Test
    fun testDeterministicGenesisHash() {
        val hash1 = LedgerEventEntity.computeGenesisHash("FAM-HOUSEHOLD-123")
        val hash2 = LedgerEventEntity.computeGenesisHash("FAM-HOUSEHOLD-123")
        assertEquals(hash1, hash2)
        assertTrue(hash1.startsWith("GENESIS_"))
        assertEquals(72, hash1.length) // "GENESIS_" + 64 hex characters
    }

    @Test
    fun testConflictResolverLWW() {
        val oldTx = Transaction("t1", "w1", "m1", "c1", -100_000L, "Note", updatedAt = 1000L)
        val newTx = Transaction("t1", "w1", "m1", "c1", -150_000L, "Updated Note", updatedAt = 2000L)

        val resNewer = com.example.core.sync.ConflictResolver.resolveTransaction(oldTx, newTx)
        assertEquals(com.example.core.sync.ConflictResolution.ACCEPT_INCOMING, resNewer)

        val resOlder = com.example.core.sync.ConflictResolver.resolveTransaction(newTx, oldTx)
        assertEquals(com.example.core.sync.ConflictResolution.KEEP_LOCAL, resOlder)
    }

    @Test
    fun testConcurrentHashChainGeneration() = runBlocking {
        val storedEvents = mutableListOf<LedgerEventEntity>()
        val mockDao = object : com.example.core.storage.LedgerAuditDao {
            override fun getAllLedgerEvents(): kotlinx.coroutines.flow.Flow<List<LedgerEventEntity>> = kotlinx.coroutines.flow.flowOf(storedEvents)
            override fun getLedgerEventsByHousehold(householdId: String): kotlinx.coroutines.flow.Flow<List<LedgerEventEntity>> = kotlinx.coroutines.flow.flowOf(storedEvents.filter { it.householdId == householdId })
            override suspend fun getLatestLedgerEvent(householdId: String): LedgerEventEntity? = synchronized(storedEvents) { storedEvents.filter { it.householdId == householdId }.maxByOrNull { it.logicalClock } }
            override suspend fun getLatestLedgerEvent(): LedgerEventEntity? = synchronized(storedEvents) { storedEvents.maxByOrNull { it.logicalClock } }
            override suspend fun insertLedgerEvent(event: LedgerEventEntity) { synchronized(storedEvents) { storedEvents.add(event) } }
            override suspend fun insertLedgerEvents(events: List<LedgerEventEntity>) { synchronized(storedEvents) { storedEvents.addAll(events) } }
            override suspend fun getPendingLedgerEvents(): List<LedgerEventEntity> = emptyList()
            override suspend fun markLedgerEventsSynced(ids: List<String>) {}
            override fun getAllTransfers(): kotlinx.coroutines.flow.Flow<List<TransferEventEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertTransfer(transfer: TransferEventEntity) {}
            override suspend fun updateTransferStatus(transferId: String, status: String, confirmedBy: String) {}
            override suspend fun updateTransferAcknowledgment(transferId: String, ack: String) {}
        }

        val ledgerEngine = com.example.core.storage.LedgerEngineService(mockDao)
        val jobs = (1..10).map { i ->
            launch(Dispatchers.Default) {
                ledgerEngine.recordEvent(
                    householdId = "FAM-CONCURRENT",
                    entityId = "entity_$i",
                    actorId = "actor_$i",
                    deviceId = "DEV1",
                    eventType = "EXPENSE",
                    amount = i * 1000L,
                    reason = "Concurrent test $i"
                )
            }
        }
        jobs.forEach { it.join() }

        assertEquals(10, storedEvents.size)
        assertTrue(ledgerEngine.verifyHashChain(storedEvents, "FAM-CONCURRENT"))
    }
}
