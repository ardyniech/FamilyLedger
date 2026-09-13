package com.example.modules.dashboard

import com.example.modules.dashboard.logic.BillClusterDetector
import com.example.shared.models.DebtRecord
import com.example.shared.models.LoanType
import com.example.shared.models.RecurringBill
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class BillClusterDetectorTest {

    @Test
    fun testDetectClusters_groupsAdjacentDueDates() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val now = cal.timeInMillis

        val bills = listOf(
            RecurringBill(
                id = "b1",
                name = "Listrik PLN",
                amount = 500_000L,
                dueDate = "05 Sept 2026",
                dueDayOfMonth = 5,
                categoryId = "cat_bill",
                autoPay = false
            ),
            RecurringBill(
                id = "b2",
                name = "Wifi Indihome",
                amount = 350_000L,
                dueDate = "06 Sept 2026",
                dueDayOfMonth = 6,
                categoryId = "cat_bill",
                autoPay = false
            )
        )

        val debts = listOf(
            DebtRecord(
                id = "d1",
                personName = "KPR BTN",
                isHutang = true,
                amount = 300_000_000L,
                paidAmount = 50_000_000L,
                dueDate = now + 6 * 86400000L,
                dueDayOfMonth = 7,
                loanType = LoanType.KPR_MORTGAGE,
                monthlyInstallment = 3_000_000L
            )
        )

        val clusters = BillClusterDetector.detectClusters(bills, debts, now)

        assertEquals("Should detect 1 consolidated cluster for days 5, 6, 7", 1, clusters.size)
        val cluster = clusters.first()
        assertEquals(5, cluster.startDay)
        assertEquals(7, cluster.endDay)
        assertEquals(3_850_000L, cluster.totalAmount)
        assertEquals(3, cluster.items.size)
    }

    @Test
    fun testDetectClusters_emptyInput_returnsEmptyList() {
        val clusters = BillClusterDetector.detectClusters(emptyList(), emptyList(), System.currentTimeMillis())
        assertTrue(clusters.isEmpty())
    }
}
