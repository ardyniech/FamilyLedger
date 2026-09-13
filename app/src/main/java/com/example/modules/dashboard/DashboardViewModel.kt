package com.example.modules.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.auth.AppLockManager
import com.example.core.auth.AuthManager
import com.example.core.storage.HouseholdRepository
import com.example.core.sync.SyncState
import com.example.core.sync.TransferNotificationManager
import com.example.modules.dashboard.logic.*
import com.example.shared.models.*
import com.example.shared.utils.MultiCurrencyHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: HouseholdRepository,
    private val authManager: AuthManager,
    private val context: Context
) : ViewModel() {
    private val goalsManager = GoalsAndBudgetManager(context)
    private val billsManager = RecurringBillsManager(context)
    val debtManager = DebtManager(context)
    val undoManager = UndoTransactionManager()
    val appLockManager = AppLockManager(context)
    val fabPositionManager = FabPositionManager(context)
    val fabPosition: StateFlow<FabPosition> = fabPositionManager.fabPosition
    val dashboardLayoutManager = DashboardLayoutManager(context)
    val cardOrder: StateFlow<List<DashboardCardType>> = dashboardLayoutManager.cardOrder
    val hiddenCards: StateFlow<Set<DashboardCardType>> = dashboardLayoutManager.hiddenCards
    val transferNotificationManager = TransferNotificationManager()
    private val actionDelegate = DashboardActionDelegate(repository, viewModelScope, transferNotificationManager)
    private val recurringAutoScheduler = RecurringBillAutoScheduler(viewModelScope, billsManager, actionDelegate)
    val rolePersonalizationHelper = RolePersonalizationHelper(repository, viewModelScope, context)

    private val _transferState = MutableStateFlow<TransferState>(TransferState.Idle)
    val transferState: StateFlow<TransferState> = _transferState.asStateFlow()
    private val _transactionState = MutableStateFlow<TransactionState>(TransactionState.Idle)
    val transactionState: StateFlow<TransactionState> = _transactionState.asStateFlow()
    private val _selectedCurrency = MutableStateFlow(MultiCurrencyHelper.Currency.IDR)
    val selectedCurrency: StateFlow<MultiCurrencyHelper.Currency> = _selectedCurrency.asStateFlow()

    val syncState: StateFlow<SyncState> = repository.syncEngine.syncState
    val authState: StateFlow<AuthUiState> = authManager.authState
    val p2pSyncManager = repository.p2pSyncManager
    val transferActiveBanner: StateFlow<TransferNotification?> = transferNotificationManager.activeBanner
    val debts: StateFlow<List<DebtRecord>> = debtManager.debts
    val lastDeletedTx: StateFlow<Transaction?> = undoManager.lastDeletedTx

    private val _activeMemberId = MutableStateFlow("m1")
    val activeMemberId: StateFlow<String> = _activeMemberId.asStateFlow()
    private fun getHouseholdCode(ctx: Context): String = ctx.getSharedPreferences("family_ledger_prefs", Context.MODE_PRIVATE)
        .getString("household_pair_code", null) ?: "FAM-${(1000..9999).random()}".also { ctx.getSharedPreferences("family_ledger_prefs", Context.MODE_PRIVATE).edit().putString("household_pair_code", it).apply() }

    private val _householdPairCode = MutableStateFlow(getHouseholdCode(context))
    val householdPairCode: StateFlow<String> = _householdPairCode.asStateFlow()
    private val _selectedPeriod = MutableStateFlow(DashboardPeriod.MONTHLY)
    val selectedPeriod: StateFlow<DashboardPeriod> = _selectedPeriod.asStateFlow()

    val monthlyBudget: StateFlow<Long> = goalsManager.monthlyBudget
    val financialGoals: StateFlow<List<FinancialGoal>> = goalsManager.financialGoals
    val recurringBills: StateFlow<List<RecurringBill>> = billsManager.recurringBills
    private val _cashflowCadence = MutableStateFlow(CashflowCadence.DAILY)
    val cashflowCadence: StateFlow<CashflowCadence> = _cashflowCadence.asStateFlow()

    val members: StateFlow<List<Member>> = repository.members.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val wallets: StateFlow<List<WalletAccount>> = repository.wallets.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val transactions: StateFlow<List<Transaction>> = repository.transactions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val categories: StateFlow<List<Category>> = repository.categories.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val categoryGroups: StateFlow<List<CategoryGroup>> = repository.categoryGroups.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val householdExpenses: StateFlow<List<HouseholdExpense>> = repository.householdExpenses.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val totalBalance: StateFlow<Long> = wallets.combine(members) { w, _ -> w.sumOf { it.balance } }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)
    val budgetExceedances: StateFlow<List<CategoryExceedance>> = combine(transactions, categories) { txs, cats -> DashboardExceedanceCalculator.calculate(txs, cats) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financialIntegrityReport: StateFlow<FinancialIntegrityReport> = combine(recurringBills, debts, totalBalance, cashflowCadence) { bills, dList, balance, cadence ->
        FinancialIntegrityEngine.generateReport(bills, dList, balance, cadence)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialIntegrityReport(emptyList(), null, RequiredIncomeData(CashflowCadence.DAILY, 0L, "/ hari", 1, 0L, 0L, 0L, 0f, "", 0), emptyList(), 0L, 0L))

    val nafkahAllocationReport: StateFlow<NafkahAllocationReport> = combine(members, wallets, transactions, categories, _activeMemberId) { mems, wals, txs, cats, activeId ->
        NafkahAllocationCalculator.calculate(mems.find { it.id == activeId }, mems, wals, txs, cats)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NafkahAllocationReport(false, "Suami", "Istri", 0L, 0L, 0L, 0f, "", "", emptyList()))

    private val syncController = DashboardSyncController(repository, viewModelScope, context, _householdPairCode)
    private val txOps = DashboardTransactionOperations(repository, actionDelegate, debtManager, undoManager, recurringAutoScheduler, viewModelScope, context, _transactionState, _transferState)

    init {
        initializeMockDataIfNeeded()
        repository.syncEngine.startBackgroundSync(viewModelScope, _householdPairCode.value)
        recurringAutoScheduler.startAutoProcessing(wallets, recurringBills)
    }

    fun setCashflowCadence(c: CashflowCadence) { _cashflowCadence.value = c }
    fun setSelectedPeriod(p: DashboardPeriod) { _selectedPeriod.value = p }
    fun setActiveMember(mId: String) { _activeMemberId.value = mId }
    fun updateMemberRole(member: Member) = viewModelScope.launch { repository.addMember(member) }
    fun applyHouseholdRole(role: HouseholdRole) = rolePersonalizationHelper.applyRoleAndTemplate(role, members.value.find { it.id == _activeMemberId.value }, categories.value, categoryGroups.value)
    fun saveCategoryGroup(group: CategoryGroup) = viewModelScope.launch { repository.addCategoryGroup(group) }
    fun deleteCategoryGroup(group: CategoryGroup) = viewModelScope.launch { repository.deleteCategoryGroup(group) }
    fun joinHousehold(code: String) = syncController.joinHousehold(code)

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
    fun addRecurringBill(n: String, a: Long, d: String, c: String, ap: Boolean = false, w: String? = null, f: String = "Monthly", dueDay: Int = 0, autoPopulate: Boolean = true) = billsManager.addRecurringBill(n, a, d, c, ap, w, f, dueDay, autoPopulate)
    fun deleteRecurringBill(bId: String) = billsManager.deleteRecurringBill(bId)
    fun deleteCategory(cat: Category) = actionDelegate.deleteCategory(cat)
    fun saveCategory(id: String?, name: String, type: String, parentId: String? = null, budgetLimit: Long = 0L) = actionDelegate.saveCategory(id, name, type, parentId, budgetLimit)
    fun saveWalletAccount(id: String?, mId: String, type: String, name: String, bal: Long, monthlyTransferCap: Long = 0L) = actionDelegate.saveWalletAccount(id, mId, type, name, bal, monthlyTransferCap)
    fun addTransaction(amt: Long, note: String, wId: String, cId: String, isIncome: Boolean = false, ts: Long = System.currentTimeMillis(), goalId: String? = null) = txOps.addTransaction(amt, note, wId, cId, isIncome, ts, goalId, wallets.value)
    fun resetTransactionState() { _transactionState.value = TransactionState.Idle }
    fun deleteTransaction(tx: Transaction) = txOps.deleteTransaction(tx)
    fun undoDeleteTransaction() = txOps.undoDeleteTransaction(lastDeletedTx.value)
    fun setSelectedCurrency(c: MultiCurrencyHelper.Currency) { _selectedCurrency.value = c }
    fun addDebt(debt: DebtRecord) = debtManager.addDebt(debt)
    fun payDebt(debtId: String, amount: Long) = txOps.payDebt(debtId, amount, wallets.value, categories.value)
    fun deleteDebt(debtId: String) = debtManager.deleteDebt(debtId)
    fun updateTransaction(oldTx: Transaction, newTx: Transaction) = actionDelegate.updateTransaction(oldTx, newTx)
    fun transferFunds(amount: Long, note: String, fWId: String, tWId: String) = txOps.transferFunds(amount, note, fWId, tWId, wallets.value, categories.value, members.value)
    fun resetTransferState() { _transferState.value = TransferState.Idle }
    fun confirmTransferNotification(nId: String, emoji: String) = transferNotificationManager.confirmTransfer(nId, emoji)
    fun dismissTransferBanner() = transferNotificationManager.dismissBanner()
    fun payRecurringBill(billId: String, walletId: String) = txOps.payRecurringBill(billId, walletId, wallets.value, recurringBills.value)
    fun clearDatabase() = syncController.clearDatabase()
    fun importCsvTransactions(parsed: List<com.example.modules.dashboard.csv.ParsedTransaction>, skipDuplicates: Boolean = true, clearFirst: Boolean = false, onComplete: ((com.example.modules.dashboard.csv.ImportExecutionResult) -> Unit)? = null) = syncController.importCsvTransactions(parsed, skipDuplicates, clearFirst, onComplete)
    fun initializeMockDataIfNeeded() = syncController.initializeMockDataIfNeeded()
    fun exportSyncPayload(onResult: (String) -> Unit) = syncController.exportSyncPayload(members.value.find { it.id == _activeMemberId.value }, onResult)
    fun importSyncPayload(payload: String, onResult: (Boolean, String) -> Unit) = syncController.importSyncPayload(payload, onResult)
    fun addHouseholdExpense(e: HouseholdExpense) = viewModelScope.launch { repository.addHouseholdExpense(e) }
    fun deleteHouseholdExpense(e: HouseholdExpense) = viewModelScope.launch { repository.deleteHouseholdExpense(e.id) }
    fun deleteHouseholdExpense(id: String) = viewModelScope.launch { repository.deleteHouseholdExpense(id) }
}
