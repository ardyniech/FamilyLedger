package com.example.modules.dashboard.logic

import com.example.modules.dashboard.csv.CsvDateParser
import com.example.shared.models.DebtRecord
import com.example.shared.models.RecurringBill
import java.util.Calendar

object BillClusterDetector {

    fun detectClusters(
        bills: List<RecurringBill>,
        debts: List<DebtRecord>,
        now: Long = System.currentTimeMillis()
    ): List<BillCluster> {
        val obligations = mutableListOf<ObligationItem>()
        val cal = Calendar.getInstance().apply { timeInMillis = now }
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        bills.filter { !it.isPaid }.forEach { b ->
            val dueTs = resolveDueTimestamp(b.dueDate, b.dueDayOfMonth, now, currentMonth, currentYear)
            if (dueTs >= now - 86400000L) {
                val day = Calendar.getInstance().apply { timeInMillis = dueTs }.get(Calendar.DAY_OF_MONTH)
                obligations.add(ObligationItem(b.id, b.name, b.amount, dueTs, day, false, b.frequency, b.targetWalletId))
            }
        }

        debts.filter { it.isHutang && !it.isSettled }.forEach { d ->
            val dueTs = if (d.dueDayOfMonth > 0) {
                resolveDueTimestamp("", d.dueDayOfMonth, now, currentMonth, currentYear)
            } else d.dueDate
            if (dueTs >= now - 86400000L) {
                val day = Calendar.getInstance().apply { timeInMillis = dueTs }.get(Calendar.DAY_OF_MONTH)
                val title = if (d.institutionName.isNotBlank()) "${d.loanType.label} - ${d.institutionName}" else d.personName
                val amt = if (d.monthlyInstallment > 0L) d.monthlyInstallment else d.remainingAmount
                obligations.add(ObligationItem(d.id, title, amt, dueTs, day, true, d.loanType.label, d.autoDebitWalletId))
            }
        }

        obligations.sortBy { it.dueTimestamp }
        if (obligations.isEmpty()) return emptyList()

        val clusters = mutableListOf<BillCluster>()
        var currentGroup = mutableListOf<ObligationItem>()

        for (item in obligations) {
            if (currentGroup.isEmpty()) {
                currentGroup.add(item)
            } else {
                val diffDays = ((item.dueTimestamp - currentGroup.last().dueTimestamp) / 86400000L).coerceAtLeast(0)
                if (diffDays <= 4) {
                    currentGroup.add(item)
                } else {
                    if (currentGroup.size >= 2) clusters.add(buildCluster(currentGroup, now))
                    currentGroup = mutableListOf(item)
                }
            }
        }
        if (currentGroup.size >= 2) {
            clusters.add(buildCluster(currentGroup, now))
        }

        return clusters
    }

    private fun buildCluster(items: List<ObligationItem>, now: Long): BillCluster {
        val first = items.first()
        val last = items.last()
        val total = items.sumOf { it.amount }
        val daysUntil = (((first.dueTimestamp - now) / 86400000L).toInt()).coerceAtLeast(0)
        val title = "Cluster Tgl ${first.dueDayOfMonth} - ${last.dueDayOfMonth} (${items.size} Tagihan)"
        return BillCluster(title, first.dueTimestamp, last.dueTimestamp, first.dueDayOfMonth, last.dueDayOfMonth, total, items, daysUntil)
    }

    private fun resolveDueTimestamp(dateStr: String, dueDay: Int, now: Long, month: Int, year: Int): Long {
        if (dueDay in 1..31) {
            val c = Calendar.getInstance().apply {
                set(year, month, dueDay.coerceAtMost(getActualMaximum(Calendar.DAY_OF_MONTH)), 23, 59, 59)
            }
            if (c.timeInMillis < now - 86400000L) c.add(Calendar.MONTH, 1)
            return c.timeInMillis
        }
        return CsvDateParser.parseTimestamp(dateStr)
    }
}
