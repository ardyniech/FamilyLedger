package com.example.modules.dashboard.logic

import android.content.Context
import com.example.shared.models.DebtRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DebtManager(context: Context) {
    private val prefs = context.getSharedPreferences("family_ledger_debts", Context.MODE_PRIVATE)
    private val _debts = MutableStateFlow<List<DebtRecord>>(emptyList())
    val debts: StateFlow<List<DebtRecord>> = _debts.asStateFlow()

    init {
        loadDebts()
    }

    private fun loadDebts() {
        // Default initial debts for demo/real tracking
        _debts.value = listOf(
            DebtRecord(id = "d1", personName = "Budi (Teman Kantor)", isHutang = false, amount = 150000L, dueDate = System.currentTimeMillis() + 7 * 86400000L, note = "Pinjam uang makan siang"),
            DebtRecord(id = "d2", personName = "Toko Jaya Elektronik", isHutang = true, amount = 500000L, dueDate = System.currentTimeMillis() + 14 * 86400000L, note = "Cicilan kipas angin")
        )
    }

    fun addDebt(debt: DebtRecord) {
        _debts.value = _debts.value + debt
    }

    fun payDebt(debtId: String, amount: Long) {
        _debts.value = _debts.value.map {
            if (it.id == debtId) {
                val newPaid = it.paidAmount + amount
                it.copy(paidAmount = newPaid, isSettled = newPaid >= it.amount)
            } else it
        }
    }

    fun deleteDebt(debtId: String) {
        _debts.value = _debts.value.filterNot { it.id == debtId }
    }
}
