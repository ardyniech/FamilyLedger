package com.example.modules.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.auth.AuthManager
import com.example.core.storage.HouseholdRepository
import com.example.core.sync.SyncState
import com.example.core.sync.TransferNotificationManager
import com.example.modules.dashboard.logic.*
import com.example.shared.models.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class DashboardViewModel(
    private val repository: HouseholdRepository,
    private val authManager: AuthManager,
    private val context: Context
) : ViewModel() {
    private val goalsManager = GoalsAndBudgetManager()
    private val billsManager = RecurringBillsManager()
    val syncState: StateFlow<SyncState> = repository.syncEngine.syncState
    val authState: StateFlow<AuthUiState> = authManager.authState
    val p2pSyncManager = repository.p2pSyncManager
    val transferNotificationManager = TransferNotificationManager()
    val transferActiveBanner: StateFlow<TransferNotification?> = transferNotificationManager.activeBanner

    private val _activeMemberId = MutableStateFlow("m1")
    val activeMemberId: StateFlow<String> = _activeMemberId.asStateFlow()

    private fun getHouseholdCode(ctx: Context): String {
        val prefs = ctx.getSharedPreferences("family_ledger_prefs", Context.MODE_PRIVATE)
        return prefs.getString("household_pair_code", null) ?: "FAM-${(1000..9999).random()}".also {
            prefs.edit().putString("household_pair_code", it).apply()
        }
    }

    private val _householdPairCode = MutableStateFlow(getHouseholdCode(context))
    val householdPairCode: StateFlow<String> = _householdPairCode.asStateFlow()
    private val _selectedPeriod = MutableStateFlow(DashboardPeriod.MONTHLY)
    val selectedPeriod: StateFlow<DashboardPeriod> = _selectedPeriod.asStateFlow()

    val monthlyBudget: StateFlow<Double> = goalsManager.monthlyBudget
    val financialGoals: StateFlow<List<FinancialGoal>> = goalsManager.financialGoals
    val recurringBills: StateFlow<List<RecurringBill>> = billsManager.recurringBills
    val members: StateFlow<List<Member>> = repository.members.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val wallets: StateFlow<List<WalletAccount>> = repository.wallets.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val transactions: StateFlow<List<Transaction>> = repository.transactions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val categories: StateFlow<List<Category>> = repository.categories.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val totalBalance: StateFlow<Double> = wallets.combine(members) { w, _ -> w.sumOf { it.balance } }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)
    val budgetExceedances: StateFlow<List<CategoryExceedance>> = combine(transactions, categories) { txs, cats -> DashboardExceedanceCalculator.calculate(txs, cats) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        repository.syncEngine.startBackgroundSync(viewModelScope, _householdPairCode.value)
        viewModelScope.launch {
            combine(wallets, recurringBills) { w, b -> Pair(w, b) }.collect { (wList, bList) ->
                if (wList.isNotEmpty()) {
                    val now = System.currentTimeMillis()
                    bList.filter { !it.isPaid && it.autoPay && it.targetWalletId != null }.forEach { bill ->
                        if (com.example.modules.dashboard.csv.CsvDateParser.parseTimestamp(bill.dueDate) <= now) {
                            wList.find { it.id == bill.targetWalletId }?.let { payRecurringBill(bill.id, it.id) }
                        }
                    }
                }
            }
        }
    }

    fun setSelectedPeriod(p: DashboardPeriod) { _selectedPeriod.value = p }
    fun setActiveMember(mId: String) { _activeMemberId.value = mId }
    fun joinHousehold(code: String) {
        val upper = code.trim().uppercase()
        _householdPairCode.value = upper
        context.getSharedPreferences("family_ledger_prefs", Context.MODE_PRIVATE).edit().putString("household_pair_code", upper).apply()
        repository.syncEngine.updateHouseholdPairCode(viewModelScope, upper)
    }

    fun signInLocal(uId: String, p: String, ctx: Context) = authManager.signInLocal(uId, p, ctx) { updateAuthProfile() }
    fun createLocalAccount(uId: String, p: String, ctx: Context) = authManager.createLocalAccount(uId, p, ctx) { updateAuthProfile() }
    private fun updateAuthProfile() {
        (authManager.authState.value as? AuthUiState.Authenticated)?.user?.let { u ->
            viewModelScope.launch { members.value.find { it.id == _activeMemberId.value }?.let { repository.addMember(it.copy(name = u.displayName, avatarUrl = u.photoUrl ?: "")) } }
        }
    }

    fun signOut(ctx: Context) = authManager.signOut(ctx, viewModelScope)
    fun clearAuthError() = authManager.clearError()
    fun updateMonthlyBudget(b: Double) = goalsManager.updateMonthlyBudget(b)
    fun addFinancialGoal(t: String, tgt: Double, init: Double, c: String, e: String) = goalsManager.addFinancialGoal(t, tgt, init, c, e)
    fun depositToGoal(gId: String, amt: Double) = goalsManager.depositToGoal(gId, amt)
    fun addRecurringBill(n: String, a: Double, d: String, c: String, ap: Boolean = false, w: String? = null, f: String = "Monthly") = billsManager.addRecurringBill(n, a, d, c, ap, w, f)
    fun deleteRecurringBill(bId: String) = billsManager.deleteRecurringBill(bId)
    fun deleteCategory(cat: Category) = viewModelScope.launch { repository.addCategory(cat.copy(isDeleted = true, syncStatus = 0, updatedAt = System.currentTimeMillis())) }
    fun saveCategory(id: String?, name: String, type: String, parentId: String? = null, budgetLimit: Double = 0.0) = viewModelScope.launch { repository.addCategory(Category(id ?: UUID.randomUUID().toString(), name, type, parentId = parentId, syncStatus = 0, updatedAt = System.currentTimeMillis(), budgetLimit = budgetLimit)) }
    fun saveWalletAccount(id: String?, mId: String, type: String, name: String, bal: Double) = viewModelScope.launch { repository.addWallet(WalletAccount(id ?: UUID.randomUUID().toString(), mId, type, name, bal)) }
    fun addTransaction(amt: Double, note: String, wId: String, cId: String, isIncome: Boolean = false, ts: Long = System.currentTimeMillis()) = viewModelScope.launch {
        wallets.value.find { it.id == wId }?.let { repository.addTransaction(Transaction(UUID.randomUUID().toString(), it.id, it.memberId, cId, if (isIncome) amt else -amt, note, ts)) }
    }
    fun deleteTransaction(tx: Transaction) = viewModelScope.launch { repository.deleteTransaction(tx) }
    fun updateTransaction(oldTx: Transaction, newTx: Transaction) = viewModelScope.launch { repository.updateTransaction(oldTx, newTx) }
    fun transferFunds(amount: Double, note: String, fWId: String, tWId: String) = viewModelScope.launch {
        val fW = wallets.value.find { it.id == fWId } ?: return@launch
        val tW = wallets.value.find { it.id == tWId } ?: return@launch
        DashboardTransferHelper.executeTransfer(amount, note, fW, tW, categories.value, members.value, repository, transferNotificationManager)
    }
    fun confirmTransferNotification(nId: String, emoji: String) = transferNotificationManager.confirmTransfer(nId, emoji)
    fun dismissTransferBanner() = transferNotificationManager.dismissBanner()
    fun payRecurringBill(billId: String, walletId: String) = viewModelScope.launch {
        recurringBills.value.find { it.id == billId }?.let { if (!it.isPaid && wallets.value.any { w -> w.id == walletId }) { addTransaction(it.amount, "Paid: ${it.name}", walletId, it.categoryId); billsManager.markBillPaid(billId) } }
    }
    fun importCsvTransactions(parsed: List<com.example.modules.dashboard.csv.ParsedTransaction>, skipDuplicates: Boolean = true, onComplete: ((com.example.modules.dashboard.csv.ImportExecutionResult) -> Unit)? = null) = viewModelScope.launch {
        onComplete?.invoke(com.example.modules.dashboard.csv.SmartCsvImportEngine.executeImport(repository, parsed, skipDuplicates))
    }
    fun initializeMockDataIfNeeded() = viewModelScope.launch { DashboardSyncHelper.initMockIfNeeded(repository, _householdPairCode.value) }
    fun exportSyncPayload(onResult: (String) -> Unit) = viewModelScope.launch { onResult(DashboardSyncHelper.exportPayload(p2pSyncManager, _householdPairCode.value, members.value.find { it.id == _activeMemberId.value })) }
    fun importSyncPayload(payload: String, onResult: (Boolean, String) -> Unit) = viewModelScope.launch {
        val (ok, msg) = DashboardSyncHelper.importPayload(p2pSyncManager, payload)
        onResult(ok, msg)
    }
}

