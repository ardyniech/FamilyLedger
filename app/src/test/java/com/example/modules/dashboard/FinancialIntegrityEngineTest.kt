package com.example.modules.dashboard

import com.example.modules.dashboard.logic.*
import com.example.shared.models.*
import org.junit.Assert.*
import org.junit.Test

class FinancialIntegrityEngineTest {

    @Test
    fun testClusterDetection_closeDueDates_createsCluster() {
        val kpr = DebtRecord(
            id = "d1",
            personName = "KPR Rumah BTN",
            isHutang = true,
            amount = 350_000_000L,
            dueDate = 0L,
            loanType = LoanType.KPR_MORTGAGE,
            monthlyInstallment = 3_200_000L,
            dueDayOfMonth = 7,
            institutionName = "Bank BTN"
        )
        val bill = RecurringBill(
            id = "b1",
            name = "Indihome Wifi",
            amount = 385_000L,
            dueDate = "Tgl 10 Setiap Bulan",
            dueDayOfMonth = 10,
            categoryId = "cat1"
        )

        val clusters = BillClusterDetector.detectClusters(listOf(bill), listOf(kpr))
        assertEquals(1, clusters.size)
        val cluster = clusters.first()
        assertEquals(7, cluster.startDay)
        assertEquals(10, cluster.endDay)
        assertEquals(3_585_000L, cluster.totalAmount)
        assertEquals(2, cluster.items.size)
    }

    @Test
    fun testIncomeTargetCalculation_dailyCadence() {
        val cluster = BillCluster(
            title = "Cluster A",
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 86400000L * 3,
            startDay = 7,
            endDay = 10,
            totalAmount = 3_500_000L,
            items = emptyList(),
            daysUntilStart = 5
        )

        val target = RequiredIncomeCalculator.calculate(
            cadence = CashflowCadence.DAILY,
            liquidBalance = 1_000_000L,
            clusters = listOf(cluster),
            totalUpcomingMonthObligation = 3_500_000L,
            totalIncomeThisMonth = 10_000_000L
        )

        // Shortfall = 3,500,000 - 1,000,000 = 2,500,000. Days = 5 -> 500,000 / day
        assertEquals(500_000L, target.requiredAmount)
        assertEquals("/ hari", target.unitLabel)
        assertEquals(2_500_000L, target.shortfall)
    }

    @Test
    fun testEmptyObligations_returnsZeroTargetGracefully() {
        val target = RequiredIncomeCalculator.calculate(
            cadence = CashflowCadence.DAILY,
            liquidBalance = 5_000_000L,
            clusters = emptyList(),
            totalUpcomingMonthObligation = 0L,
            totalIncomeThisMonth = 10_000_000L
        )

        assertEquals(0L, target.requiredAmount)
        assertEquals(0, target.urgencyLevel)
        assertTrue(target.statusMessage.contains("Aman"))
    }
}
