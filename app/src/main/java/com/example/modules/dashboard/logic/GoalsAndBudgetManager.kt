package com.example.modules.dashboard.logic

import com.example.shared.models.FinancialGoal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class GoalsAndBudgetManager {
    private val _monthlyBudget = MutableStateFlow<Long>(5000000L)
    val monthlyBudget: StateFlow<Long> = _monthlyBudget.asStateFlow()

    private val _financialGoals = MutableStateFlow<List<FinancialGoal>>(
        listOf(
            FinancialGoal("g1", "Tabungan Rumah Impian", 50000000L, 18500000L, "Rumah", "🏡", "31 Des 2027", 1830211200000L, "#3B82F6"),
            FinancialGoal("g2", "Dana Darurat 6 Bulan", 20000000L, 12000000L, "Darurat", "🛡️", "30 Jun 2027", 1814313600000L, "#10B981"),
            FinancialGoal("g3", "Liburan Akhir Tahun", 10000000L, 4500000L, "Liburan", "✈️", "31 Des 2026", 1798675200000L, "#F59E0B")
        )
    )
    val financialGoals: StateFlow<List<FinancialGoal>> = _financialGoals.asStateFlow()

    fun updateMonthlyBudget(newBudget: Long) {
        _monthlyBudget.value = newBudget
    }

    fun addFinancialGoal(
        title: String,
        targetAmount: Long,
        initialAmount: Long,
        category: String,
        iconEmoji: String,
        deadline: String = "",
        targetTimestamp: Long = 0L,
        colorHex: String = "#3B82F6"
    ) {
        val newGoal = FinancialGoal(
            id = UUID.randomUUID().toString(),
            title = title,
            targetAmount = targetAmount,
            currentAmount = initialAmount,
            category = category,
            iconEmoji = iconEmoji,
            deadline = deadline,
            targetTimestamp = targetTimestamp,
            colorHex = colorHex
        )
        _financialGoals.value = _financialGoals.value + newGoal
    }

    fun updateFinancialGoal(goal: FinancialGoal) {
        _financialGoals.value = _financialGoals.value.map { if (it.id == goal.id) goal else it }
    }

    fun deleteFinancialGoal(goalId: String) {
        _financialGoals.value = _financialGoals.value.filter { it.id != goalId }
    }

    fun depositToGoal(goalId: String, amount: Long) {
        _financialGoals.value = _financialGoals.value.map {
            if (it.id == goalId) it.copy(currentAmount = it.currentAmount + amount) else it
        }
    }
}
