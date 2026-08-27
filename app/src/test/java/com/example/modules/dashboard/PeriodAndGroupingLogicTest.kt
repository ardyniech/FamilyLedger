package com.example.modules.dashboard

import com.example.modules.dashboard.logic.*
import com.example.shared.models.Transaction
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class PeriodAndGroupingLogicTest {

    @Test
    fun testTransactionGroupingByDay() {
        val now = System.currentTimeMillis()
        val tx1 = Transaction("1", "w1", "m1", "c1", -50000.0, "Makan Siang", now)
        val tx2 = Transaction("2", "w1", "m1", "c1", -25000.0, "Kopi Sore", now - 1000)
        
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayMillis = cal.timeInMillis
        val txYesterday = Transaction("3", "w1", "m1", "c1", -100000.0, "Belanja Kemarin", yesterdayMillis)

        val groups = TransactionGroupingHelper.groupByDay(listOf(tx1, tx2, txYesterday))
        assertEquals(2, groups.size)
        assertTrue(groups[0].displayHeader.contains("Hari Ini"))
        assertEquals(2, groups[0].transactions.size)
        assertEquals(75000.0, groups[0].dayTotalOutflow, 0.01)
        assertTrue(groups[1].displayHeader.contains("Kemarin"))
        assertEquals(1, groups[1].transactions.size)
        assertEquals(100000.0, groups[1].dayTotalOutflow, 0.01)
    }

    @Test
    fun testPeriodFilterDateRanges() {
        for (period in DashboardPeriod.values()) {
            val (start, end) = PeriodFilterHelper.getPeriodDateRange(period)
            assertTrue("Start must be <= End for ${period.name}", start <= end)
        }
    }

    @Test
    fun testPeriodSummaryCalculation() {
        val now = System.currentTimeMillis()
        val txExpense = Transaction("1", "w1", "m1", "c1", -150000.0, "Belanja", now)
        val txIncome = Transaction("2", "w1", "m1", "c2", 500000.0, "Gaji Freelance", now)

        val summary = PeriodFilterHelper.calculateSummary(
            transactions = listOf(txExpense, txIncome),
            period = DashboardPeriod.DAILY,
            baseMonthlyBudget = 3000000.0
        )

        assertEquals(2, summary.transactionCount)
        assertEquals(150000.0, summary.totalOutflow, 0.01)
        assertEquals(500000.0, summary.totalInflow, 0.01)
        assertEquals(350000.0, summary.netBalance, 0.01)
        assertEquals(100000.0, summary.adjustedBudget, 0.01) // 3000000 / 30
    }

    @Test
    fun testMonthFilterOffset() {
        val now = System.currentTimeMillis()
        val currentTx = Transaction("1", "w1", "m1", "c1", -50000.0, "Bulan Ini", now)
        
        val currData = MonthFilterHelper.filterForMonth(listOf(currentTx), 0)
        assertEquals(1, currData.transactions.size)
        assertEquals(50000.0, currData.totalExpense, 0.01)

        val pastData = MonthFilterHelper.filterForMonth(listOf(currentTx), -1)
        assertEquals(0, pastData.transactions.size)
    }
}
