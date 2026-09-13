package com.example.modules.dashboard.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.modules.dashboard.logic.BillCluster
import com.example.modules.dashboard.logic.BillClusterDetector
import com.example.modules.dashboard.logic.DebtStorage
import com.example.modules.dashboard.logic.RecurringBillsStorage
import java.util.Calendar

class BillClusterReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val debts = DebtStorage.loadDebts(applicationContext) ?: emptyList()
        val bills = RecurringBillsStorage.loadBills(applicationContext) ?: emptyList()
        val now = System.currentTimeMillis()

        val clusters = BillClusterDetector.detectClusters(bills, debts, now)
        if (clusters.isEmpty()) return Result.success()

        val prefs = applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val todayKey = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        for (cluster in clusters) {
            val days = cluster.daysUntilStart
            if (days in listOf(0, 1, 3)) {
                val alertKey = "alert_${cluster.startDay}_${days}_day_$todayKey"
                val alreadySent = prefs.getBoolean(alertKey, false)
                if (!alreadySent) {
                    BillClusterNotificationHelper.showClusterAlert(applicationContext, cluster, days)
                    prefs.edit().putBoolean(alertKey, true).apply()
                }
            }
        }
        return Result.success()
    }

    companion object {
        private const val PREF_NAME = "bill_cluster_worker_prefs"
        const val WORK_NAME_PERIODIC = "PeriodicBillClusterReminder"
        const val WORK_NAME_IMMEDIATE = "ImmediateBillClusterReminder"
    }
}
