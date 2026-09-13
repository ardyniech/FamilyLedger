package com.example.modules.dashboard.logic

import android.content.Context
import com.example.core.storage.HouseholdRepository
import com.example.modules.dashboard.csv.ImportExecutionResult
import com.example.modules.dashboard.csv.ParsedTransaction
import com.example.modules.dashboard.csv.SmartCsvImportEngine
import com.example.shared.models.Member
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DashboardSyncController(
    private val repository: HouseholdRepository,
    private val scope: CoroutineScope,
    private val context: Context,
    private val householdPairCode: MutableStateFlow<String>
) {
    fun joinHousehold(code: String) {
        val upper = code.trim().uppercase()
        householdPairCode.value = upper
        context.getSharedPreferences("family_ledger_prefs", Context.MODE_PRIVATE)
            .edit().putString("household_pair_code", upper).apply()
        repository.syncEngine.updateHouseholdPairCode(scope, upper)
    }

    fun clearDatabase() = scope.launch {
        context.getSharedPreferences("family_ledger_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("is_clean_state", true).apply()
        repository.clearAllData()
    }

    fun importCsvTransactions(
        parsed: List<ParsedTransaction>,
        skipDuplicates: Boolean = true,
        clearFirst: Boolean = false,
        onComplete: ((ImportExecutionResult) -> Unit)? = null
    ) = scope.launch {
        if (clearFirst) {
            context.getSharedPreferences("family_ledger_prefs", Context.MODE_PRIVATE)
                .edit().putBoolean("is_clean_state", true).apply()
            repository.clearAllData()
        }
        onComplete?.invoke(SmartCsvImportEngine.executeImport(repository, parsed, skipDuplicates))
    }

    fun initializeMockDataIfNeeded() = scope.launch {
        DashboardSyncHelper.initMockIfNeeded(repository, householdPairCode.value, context)
    }

    fun exportSyncPayload(activeMember: Member?, onResult: (String) -> Unit) = scope.launch {
        onResult(DashboardSyncHelper.exportPayload(repository.p2pSyncManager, householdPairCode.value, activeMember))
    }

    fun importSyncPayload(payload: String, onResult: (Boolean, String) -> Unit) = scope.launch {
        val (ok, msg) = DashboardSyncHelper.importPayload(repository.p2pSyncManager, payload)
        onResult(ok, msg)
    }
}
