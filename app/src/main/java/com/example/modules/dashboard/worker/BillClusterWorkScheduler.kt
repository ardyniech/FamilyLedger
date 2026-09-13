package com.example.modules.dashboard.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object BillClusterWorkScheduler {

    fun scheduleDailyClusterCheck(context: Context) {
        try {
            val request = PeriodicWorkRequestBuilder<BillClusterReminderWorker>(
                12, TimeUnit.HOURS,
                1, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                BillClusterReminderWorker.WORK_NAME_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        } catch (e: Exception) {
            android.util.Log.w("BillClusterScheduler", "WorkManager not initialized: ${e.message}")
        }
    }

    fun triggerImmediateCheck(context: Context) {
        try {
            val request = OneTimeWorkRequestBuilder<BillClusterReminderWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                BillClusterReminderWorker.WORK_NAME_IMMEDIATE,
                ExistingWorkPolicy.REPLACE,
                request
            )
        } catch (e: Exception) {
            android.util.Log.w("BillClusterScheduler", "WorkManager trigger failed: ${e.message}")
        }
    }
}
