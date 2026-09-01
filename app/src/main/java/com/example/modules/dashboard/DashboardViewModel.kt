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

class DashboardViewModel(
    private val repository: HouseholdRepository,
    private val authManager: AuthManager,
    private val context: Context
) : ViewModel() {
    private val goalsManager = GoalsAndBudgetManager(context)
    private val billsManager = RecurringBillsManager()
    val transferNotificationManager = TransferNotificationManager()
    private val actionDelegate = DashboardActionDelegate(repository, viewModelScope, transferNotificationManager)
    private val recurringAutoScheduler = RecurringBillAutoScheduler(viewModelScope, billsManager, actionDelegate)

    val syncState: StateFlow<SyncState> = repository.syncEngine.syncState
    val authState: StateFlow<AuthUiState> = authManager.authState
    val p2pSyncManager = repository.p2pSyncManager
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

    val monthlyBudget: StateFlow<Long> = goalsManager.monthlyBudget
    val financialGoals: StateFlow<List<FinancialGoal>> = goalsManager.financialGoals
    val recurringBills: StateFlow<List<RecurringBill>> = billsManager.recurringBills
    val members: StateFlow<List<Member>> = repository.members.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val wallets: StateFlow<List<WalletAccount>> = repository.wallets.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val transactions: StateFlow<List<Transaction>> = repository.transactions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val categories: StateFlow<List<Category>> = repository.categories.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val categoryGroups: StateFlow<List<CategoryGroup>> = repository.categoryGroups.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val totalBalance: StateFlow<Long> = wallets.combine(members) { w, _ -> w.sumOf { it.balance } }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)
    val budgetExceedances: StateFlow<List<CategoryExceedance>> = combine(transactions, categories) { txs, cats -> DashboardExceedanceCalculator.calculate(txs, cats) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        initializeMockDataIfNeeded()
        repository.syncEngine.startBackgroundSync(viewModelScope, _householdPairCode.value)
        recurringAutoScheduler.startAutoProcessing(wallets, recurringBills)
    }

    fun setSelectedPeriod(p: DashboardPeriod) { _selectedPeriod.value = p }
    fun setActiveMember(mId: String) { _activeMemberId.value = mId }
    fun updateMemberRole(member: Member) = viewModelScope.launch { repository.addMember(member) }
    fun saveCategoryGroup(group: CategoryGroup) = viewModelScope.launch { repository.addCategoryGroup(group) }
    fun deleteCategoryGroup(group: CategoryGroup) = viewModelScope.launch { repository.deleteCategoryGroup(group) }
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
    fun updateMonthlyBudget(b: Long) = goalsManager.updateMonthlyBudget(b)
    fun addFinancialGoal(t: String, tgt: Long, init: Long, c: String, e: String, d: String = "", ts: Long = 0L, hex: String = "#3B82F6") = goalsManager.addFinancialGoal(t, tgt, init, c, e, d, ts, hex)
    fun updateFinancialGoal(g: FinancialGoal) = goalsManager.updateFinancialGoal(g)
    fun deleteFinancialGoal(gId: String) = goalsManager.deleteFinancialGoal(gId)
    fun depositToGoal(gId: String, amt: Long) = goalsManager.depositToGoal(gId, amt)
    fun addRecurringBill(n: String, a: Long, d: String, c: String, ap: Boolean = false, w: String? = null, f: String = "Monthly") = billsManager.addRecurringBill(n, a, d, c, ap, w, f)
    fun deleteRecurringBill(bId: String) = billsManager.deleteRecurringBill(bId)
    fun deleteCategory(cat: Category) = actionDelegate.deleteCategory(cat)
    fun saveCategory(id: String?, name: String, type: String, parentId: String? = null, budgetLimit: Long = 0L) = actionDelegate.saveCategory(id, name, type, parentId, budgetLimit)
    fun saveWalletAccount(id: String?, mId: String, type: String, name: String, bal: Long, monthlyTransferCap: Long = 0L) = actionDelegate.saveWalletAccount(id, mId, type, name, bal, monthlyTransferCap)
    fun addTransaction(amt: Long, note: String, wId: String, cId: String, isIncome: Boolean = false, ts: Long = System.currentTimeMillis(), goalId: String? = null) = actionDelegate.addTransaction(amt, note, wId, cId, isIncome, ts, wallets.value, goalId)
    fun deleteTransaction(tx: Transaction) = actionDelegate.deleteTransaction(tx)
    fun updateTransaction(oldTx: Transaction, newTx: Transaction) = actionDelegate.updateTransaction(oldTx, newTx)
    fun transferFunds(amount: Long, note: String, fWId: String, tWId: String) = actionDelegate.transferFunds(amount, note, fWId, tWId, wallets.value, categories.value, members.value)
    fun confirmTransferNotification(nId: String, emoji: String) = transferNotificationManager.confirmTransfer(nId, emoji)
    fun dismissTransferBanner() = transferNotificationManager.dismissBanner()
    fun payRecurringBill(billId: String, walletId: String) = recurringAutoScheduler.payRecurringBill(billId, walletId, wallets.value, recurringBills.value)
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
