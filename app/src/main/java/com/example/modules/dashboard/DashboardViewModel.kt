package com.example.modules.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.auth.AuthManager
import com.example.core.storage.HouseholdRepository
import com.example.core.sync.SyncState
import com.example.modules.dashboard.logic.DashboardPeriod
import com.example.modules.dashboard.logic.GoalsAndBudgetManager
import com.example.modules.dashboard.logic.RecurringBillsManager
import com.example.modules.dashboard.logic.SampleDataInitializer
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
    val transferNotificationManager = com.example.core.sync.TransferNotificationManager()
    val transferActiveBanner: StateFlow<com.example.shared.models.TransferNotification?> = transferNotificationManager.activeBanner

    private val _activeMemberId = MutableStateFlow("m1")
    val activeMemberId: StateFlow<String> = _activeMemberId.asStateFlow()

    private fun getOrCreateHouseholdCode(context: Context): String {
        val prefs = context.getSharedPreferences("family_ledger_prefs", Context.MODE_PRIVATE)
        var code = prefs.getString("household_pair_code", null)
        if (code == null) {
            val randomNum = (1000..9999).random()
            code = "FAM-$randomNum"
            prefs.edit().putString("household_pair_code", code).apply()
        }
        return code
    }

    private val _householdPairCode = MutableStateFlow(getOrCreateHouseholdCode(context))
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

    val budgetExceedances: StateFlow<List<CategoryExceedance>> = combine(transactions, categories) { txs, cats ->
        val calendar = java.util.Calendar.getInstance()
        val currentYear = calendar.get(java.util.Calendar.YEAR)
        val currentMonth = calendar.get(java.util.Calendar.MONTH)

        val currentMonthTxs = txs.filter { t ->
            val txCal = java.util.Calendar.getInstance().apply { timeInMillis = t.timestamp }
            txCal.get(java.util.Calendar.YEAR) == currentYear &&
                    txCal.get(java.util.Calendar.MONTH) == currentMonth &&
                    t.amount < 0 &&
                    !t.isDeleted
        }

        cats.filter { it.type == "Expense" && !it.isDeleted && it.budgetLimit > 0.0 }.mapNotNull { cat ->
            val spent = currentMonthTxs.filter { it.categoryId == cat.id }.sumOf { -it.amount }
            if (spent > cat.budgetLimit) {
                CategoryExceedance(cat, cat.budgetLimit, spent)
            } else {
                null
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        repository.syncEngine.startBackgroundSync(viewModelScope, _householdPairCode.value)
        
        // Auto process scheduled auto-pay recurring bills when due
        viewModelScope.launch {
            combine(wallets, recurringBills) { wList, bList ->
                Pair(wList, bList)
            }.collect { (wList, bList) ->
                if (wList.isNotEmpty()) {
                    val now = System.currentTimeMillis()
                    bList.forEach { bill ->
                        if (!bill.isPaid && bill.autoPay && bill.targetWalletId != null) {
                            val dueTime = com.example.modules.dashboard.csv.CsvDateParser.parseTimestamp(bill.dueDate)
                            if (dueTime <= now) {
                                val wallet = wList.find { it.id == bill.targetWalletId }
                                if (wallet != null) {
                                    // Process payment automatically
                                    payRecurringBill(bill.id, wallet.id)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setSelectedPeriod(period: DashboardPeriod) { _selectedPeriod.value = period }
    fun setActiveMember(memberId: String) { _activeMemberId.value = memberId }
    fun joinHousehold(pairCode: String) {
        val upperCode = pairCode.trim().uppercase()
        _householdPairCode.value = upperCode
        val prefs = context.getSharedPreferences("family_ledger_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("household_pair_code", upperCode).apply()
        repository.syncEngine.updateHouseholdPairCode(viewModelScope, upperCode)
    }

    fun signInWithGoogle(context: Context) {
        authManager.signInWithGoogle(context, viewModelScope) {
            val user = (authManager.authState.value as? AuthUiState.Authenticated)?.user
            if (user != null) {
                viewModelScope.launch {
                    val currentMember = members.value.find { it.id == _activeMemberId.value }
                    if (currentMember != null) {
                        repository.addMember(currentMember.copy(name = user.displayName, avatarUrl = user.photoUrl ?: ""))
                    }
                }
            }
        }
    }

    fun signOut(context: Context) = authManager.signOut(context, viewModelScope)
    fun clearAuthError() = authManager.clearError()

    fun updateMonthlyBudget(newBudget: Double) = goalsManager.updateMonthlyBudget(newBudget)
    fun addFinancialGoal(title: String, target: Double, initial: Double, cat: String, emoji: String) = goalsManager.addFinancialGoal(title, target, initial, cat, emoji)
    fun depositToGoal(goalId: String, amount: Double) = goalsManager.depositToGoal(goalId, amount)
    fun addRecurringBill(
        name: String,
        amount: Double,
        due: String,
        catId: String,
        autoPay: Boolean = false,
        targetWalletId: String? = null,
        frequency: String = "Monthly"
    ) = billsManager.addRecurringBill(name, amount, due, catId, autoPay, targetWalletId, frequency)

    fun deleteRecurringBill(billId: String) = billsManager.deleteRecurringBill(billId)
    fun deleteCategory(category: Category) = viewModelScope.launch { repository.addCategory(category.copy(isDeleted = true, syncStatus = 0, updatedAt = System.currentTimeMillis())) }
    fun saveCategory(id: String?, name: String, type: String, parentId: String? = null, budgetLimit: Double = 0.0) = viewModelScope.launch { repository.addCategory(Category(id ?: UUID.randomUUID().toString(), name, type, parentId = parentId, syncStatus = 0, updatedAt = System.currentTimeMillis(), budgetLimit = budgetLimit)) }
    fun saveWalletAccount(id: String?, memberId: String, type: String, name: String, balance: Double) = viewModelScope.launch { repository.addWallet(WalletAccount(id ?: UUID.randomUUID().toString(), memberId, type, name, balance)) }

    fun addTransaction(amount: Double, note: String, walletId: String, categoryId: String, isIncome: Boolean = false, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            wallets.value.find { it.id == walletId }?.let { wallet ->
                repository.addTransaction(Transaction(UUID.randomUUID().toString(), wallet.id, wallet.memberId, categoryId, if (isIncome) amount else -amount, note, timestamp))
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
    }

    fun updateTransaction(oldTx: Transaction, newTx: Transaction) = viewModelScope.launch {
        repository.updateTransaction(oldTx, newTx)
    }

    fun transferFunds(amount: Double, note: String, fromWalletId: String, toWalletId: String) {
        viewModelScope.launch {
            val fromWallet = wallets.value.find { it.id == fromWalletId } ?: return@launch
            val toWallet = wallets.value.find { it.id == toWalletId } ?: return@launch
            val isCross = fromWallet.memberId != toWallet.memberId
            val outType = if (isCross) "Expense" else "Transfer"
            val inType = if (isCross) "Income" else "Transfer"
            val outCat = categories.value.find { it.name == "Transfer Out" && it.type == outType } ?: Category(UUID.randomUUID().toString(), "Transfer Out", outType).also { repository.addCategory(it) }
            val inCat = categories.value.find { it.name == "Transfer In" && it.type == inType } ?: Category(UUID.randomUUID().toString(), "Transfer In", inType).also { repository.addCategory(it) }
            repository.addTransaction(Transaction(UUID.randomUUID().toString(), fromWallet.id, fromWallet.memberId, outCat.id, -amount, note))
            repository.addTransaction(Transaction(UUID.randomUUID().toString(), toWallet.id, toWallet.memberId, inCat.id, amount, note))

            val fromMember = members.value.find { it.id == fromWallet.memberId }
            val toMember = members.value.find { it.id == toWallet.memberId }
            if (fromMember != null && toMember != null && isCross) {
                transferNotificationManager.createTransferNotification(
                    senderId = fromMember.id,
                    senderName = fromMember.name,
                    senderRole = fromMember.role,
                    recipientId = toMember.id,
                    recipientName = toMember.name,
                    recipientRole = toMember.role,
                    amount = amount,
                    note = if (note.isBlank()) "Transfer Dana" else note,
                    fromWalletName = fromWallet.name,
                    toWalletName = toWallet.name
                )
            }
        }
    }

    fun confirmTransferNotification(notificationId: String, emojiReaction: String) {
        transferNotificationManager.confirmTransfer(notificationId, emojiReaction)
    }

    fun dismissTransferBanner() {
        transferNotificationManager.dismissBanner()
    }

    fun payRecurringBill(billId: String, walletId: String) = viewModelScope.launch {
        val bill = recurringBills.value.find { it.id == billId }
        if (bill != null && !bill.isPaid && wallets.value.any { it.id == walletId }) {
            addTransaction(bill.amount, "Paid: ${bill.name}", walletId, bill.categoryId)
            billsManager.markBillPaid(billId)
        }
    }

    fun importCsvTransactions(parsed: List<com.example.modules.dashboard.csv.ParsedTransaction>, skipDuplicates: Boolean = true, onComplete: ((com.example.modules.dashboard.csv.ImportExecutionResult) -> Unit)? = null) = viewModelScope.launch {
        val res = com.example.modules.dashboard.csv.SmartCsvImportEngine.executeImport(repository, parsed, skipDuplicates)
        onComplete?.invoke(res)
    }

    fun initializeMockDataIfNeeded() = viewModelScope.launch {
        val memberList = repository.members.firstOrNull()
        if (memberList.isNullOrEmpty() || memberList.none { it.name == "Ardy" }) {
            SampleDataInitializer.populateDefaultFamilyData(repository, _householdPairCode.value)
        }
    }

    fun exportSyncPayload(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val member = members.value.find { it.id == _activeMemberId.value }
            val pkg = p2pSyncManager.createSyncPackage(
                pairCode = _householdPairCode.value,
                senderName = member?.name ?: "Unknown",
                senderRole = member?.role ?: "Member"
            )
            onResult(pkg.toCompressedBase64())
        }
    }

    fun importSyncPayload(payload: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val pkg = com.example.core.sync.p2p.P2PSyncPackage.fromCompressedBase64(payload)
                val result = p2pSyncManager.importSyncPackage(pkg)
                onResult(result.success, result.message)
            } catch (e: Exception) {
                onResult(false, "Format data tidak valid: ${e.message}")
            }
        }
    }
}

data class CategoryExceedance(
    val category: Category,
    val budgetLimit: Double,
    val currentSpent: Double
)
