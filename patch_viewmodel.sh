sed -i '/fun addTransaction/i \
    fun saveCategory(id: String?, name: String, type: String, parentId: String? = null) {\
        viewModelScope.launch {\
            val category = Category(id ?: UUID.randomUUID().toString(), name, type, parentId = parentId)\
            repository.addCategory(category)\
        }\
    }\
\
    fun saveWalletAccount(id: String?, memberId: String, type: String, name: String, balance: Double) {\
        viewModelScope.launch {\
            val wallet = WalletAccount(id ?: UUID.randomUUID().toString(), memberId, type, name, balance)\
            repository.addWallet(wallet)\
        }\
    }\
\
    fun transferFunds(amount: Double, note: String, fromWalletId: String, toWalletId: String) {\
        viewModelScope.launch {\
            val fromWallet = wallets.value.find { it.id == fromWalletId } ?: return@launch\
            val toWallet = wallets.value.find { it.id == toWalletId } ?: return@launch\
            \
            // Ensure Transfer categories exist\
            val transferExpenseCat = categories.value.find { it.name == "Transfer Out" } ?: Category("cat_tf_out", "Transfer Out", "Expense").also { repository.addCategory(it) }\
            val transferIncomeCat = categories.value.find { it.name == "Transfer In" } ?: Category("cat_tf_in", "Transfer In", "Income").also { repository.addCategory(it) }\
\
            // Expense from source\
            repository.addTransaction(\
                Transaction(UUID.randomUUID().toString(), fromWallet.id, fromWallet.memberId, transferExpenseCat.id, -amount, note)\
            )\
            \
            // Income to destination\
            repository.addTransaction(\
                Transaction(UUID.randomUUID().toString(), toWallet.id, toWallet.memberId, transferIncomeCat.id, amount, note)\
            )\
        }\
    }\
' app/src/main/java/com/example/modules/dashboard/DashboardViewModel.kt
sh patch_viewmodel.sh
