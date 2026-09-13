package com.example

import android.app.Application
import com.example.core.AppContainer
import com.example.core.DefaultAppContainer
import com.example.modules.dashboard.worker.BillClusterWorkScheduler

class FamilyLedgerApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        BillClusterWorkScheduler.scheduleDailyClusterCheck(this)
    }
}
