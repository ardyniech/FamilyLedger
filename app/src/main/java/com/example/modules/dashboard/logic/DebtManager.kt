package com.example.modules.dashboard.logic

import android.content.Context
import com.example.shared.models.DebtRecord
import com.example.shared.models.LoanType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DebtManager(private val context: Context) {
    private val _debts = MutableStateFlow<List<DebtRecord>>(emptyList())
    val debts: StateFlow<List<DebtRecord>> = _debts.asStateFlow()

    init {
        loadDebts()
    }

    private fun loadDebts() {
        val saved = DebtStorage.loadDebts(context)
        if (saved != null) {
            _debts.value = saved
        } else {
            val defaults = listOf(
                DebtRecord(
                    id = "loan_kpr",
                    personName = "KPR Rumah Griya Indah",
                    isHutang = true,
                    amount = 350_000_000L,
                    paidAmount = 70_000_000L,
                    dueDate = System.currentTimeMillis() + 5 * 86400000L,
                    note = "Auto-debet BTN Griya",
                    loanType = LoanType.KPR_MORTGAGE,
                    monthlyInstallment = 3_200_000L,
                    dueDayOfMonth = 7,
                    tenorRemainingMonths = 144,
                    totalTenorMonths = 180,
                    institutionName = "Bank BTN"
                ),
                DebtRecord(
                    id = "loan_motor",
                    personName = "Cicilan Honda Vario 160",
                    isHutang = true,
                    amount = 28_000_000L,
                    paidAmount = 14_000_000L,
                    dueDate = System.currentTimeMillis() + 8 * 86400000L,
                    note = "Cicilan via Virtual Account",
                    loanType = LoanType.VEHICLE_LOAN,
                    monthlyInstallment = 1_150_000L,
                    dueDayOfMonth = 10,
                    tenorRemainingMonths = 18,
                    totalTenorMonths = 36,
                    institutionName = "FIF Astra"
                ),
                DebtRecord(
                    id = "d1",
                    personName = "Budi (Teman Kantor)",
                    isHutang = false,
                    amount = 150000L,
                    dueDate = System.currentTimeMillis() + 7 * 86400000L,
                    note = "Pinjam uang makan siang"
                )
            )
            _debts.value = defaults
            DebtStorage.saveDebts(context, defaults)
        }
    }

    fun addDebt(debt: DebtRecord) {
        _debts.value = _debts.value + debt
        DebtStorage.saveDebts(context, _debts.value)
    }

    fun payDebt(debtId: String, amount: Long) {
        _debts.value = _debts.value.map {
            if (it.id == debtId) {
                val newPaid = it.paidAmount + amount
                val newTenor = if (it.tenorRemainingMonths > 0) (it.tenorRemainingMonths - 1).coerceAtLeast(0) else it.tenorRemainingMonths
                it.copy(
                    paidAmount = newPaid,
                    tenorRemainingMonths = newTenor,
                    isSettled = newPaid >= it.amount
                )
            } else it
        }
        DebtStorage.saveDebts(context, _debts.value)
    }

    fun deleteDebt(debtId: String) {
        _debts.value = _debts.value.filterNot { it.id == debtId }
        DebtStorage.saveDebts(context, _debts.value)
    }
}

