package com.example.modules.dashboard.logic

import com.example.core.storage.HouseholdRepository
import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.WalletAccount

object RealDataImporter {
    suspend fun seedRealData(repository: HouseholdRepository, pairCode: String = "") {
        // 1. Clear old mock data completely
        repository.clearAllData()

        // 2. Prepare Base Entities
        val effectivePairCode = pairCode.ifBlank { "FAM-LOCAL" }
        val members: List<Member> = UserDataEntities.getMembers(effectivePairCode)
        val categoryGroups = UserDataEntities.getCategoryGroups()
        val categories: List<Category> = UserDataEntities.getCategories()
        val initialWallets: List<WalletAccount> = UserDataEntities.getWallets()
        val transactions = CsvTransactionBuilder.buildTransactions()

        // 3. Compute exact final wallet balances from all transactions
        val calculatedWallets = initialWallets.map { wallet ->
            val netBalance = transactions
                .filter { it.walletId == wallet.id }
                .sumOf { it.amount }
            wallet.copy(balance = netBalance)
        }

        // 4. Batch insert all real records into Room Database
        repository.insertHousehold(com.example.shared.models.Household(id = effectivePairCode, pairCode = effectivePairCode))
        repository.insertMembers(members)
        repository.insertCategoryGroups(categoryGroups)
        repository.insertCategories(categories)
        repository.insertWallets(calculatedWallets)
        repository.insertTransactions(transactions)
    }
}
