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
            FinancialGoal("g1", "Save#1 - Tabungan Awal Pacaran", 5000000L, 2230000L, "Tabungan", "🌱"),
            FinancialGoal("g2", "Save#2 - Tabungan Komitmen & Menikah", 10000000L, 1573000L, "Komitmen", "💍"),
            FinancialGoal("g3", "Dana Darurat Keluarga", 15000000L, 3500000L, "Darurat", "🛡️")
        )
    )
    val financialGoals: StateFlow<List<FinancialGoal>> = _financialGoals.asStateFlow()

    fun updateMonthlyBudget(newBudget: Long) {
        _monthlyBudget.value = newBudget
    }

    fun addFinancialGoal(title: String, targetAmount: Long, initialAmount: Long, category: String, iconEmoji: String) {
        val newGoal = FinancialGoal(
            id = UUID.randomUUID().toString(),
            title = title,
            targetAmount = targetAmount,
            currentAmount = initialAmount,
            category = category,
            iconEmoji = iconEmoji
        )
        _financialGoals.value = _financialGoals.value + newGoal
    }

    fun depositToGoal(goalId: String, amount: Long) {
        _financialGoals.value = _financialGoals.value.map {
            if (it.id == goalId) it.copy(currentAmount = it.currentAmount + amount) else it
        }
    }
}

