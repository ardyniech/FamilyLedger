package com.example.modules.dashboard.logic

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardLayoutManager(context: Context) {
    private val prefs = context.getSharedPreferences("dashboard_layout_prefs", Context.MODE_PRIVATE)

    private val _cardOrder = MutableStateFlow(loadCardOrder())
    val cardOrder: StateFlow<List<DashboardCardType>> = _cardOrder.asStateFlow()

    private val _hiddenCards = MutableStateFlow(loadHiddenCards())
    val hiddenCards: StateFlow<Set<DashboardCardType>> = _hiddenCards.asStateFlow()

    private fun loadCardOrder(): List<DashboardCardType> {
        val saved = prefs.getString("card_order_csv", null) ?: return DashboardCardType.getDefaultList()
        val tokens = saved.split(",")
        val result = mutableListOf<DashboardCardType>()
        for (token in tokens) {
            try {
                result.add(DashboardCardType.valueOf(token))
            } catch (_: Exception) {}
        }
        return if (result.isEmpty()) DashboardCardType.getDefaultList() else result
    }

    private fun loadHiddenCards(): Set<DashboardCardType> {
        val saved = prefs.getStringSet("hidden_cards_set", emptySet()) ?: emptySet()
        return saved.mapNotNull {
            try { DashboardCardType.valueOf(it) } catch (_: Exception) { null }
        }.toSet()
    }

    fun moveUp(card: DashboardCardType) {
        val current = _cardOrder.value.toMutableList()
        val index = current.indexOf(card)
        if (index > 0) {
            current.removeAt(index)
            current.add(index - 1, card)
            saveOrder(current)
        }
    }

    fun moveDown(card: DashboardCardType) {
        val current = _cardOrder.value.toMutableList()
        val index = current.indexOf(card)
        if (index >= 0 && index < current.size - 1) {
            current.removeAt(index)
            current.add(index + 1, card)
            saveOrder(current)
        }
    }

    fun toggleVisibility(card: DashboardCardType) {
        val currentHidden = _hiddenCards.value.toMutableSet()
        if (currentHidden.contains(card)) {
            currentHidden.remove(card)
        } else {
            currentHidden.add(card)
        }
        prefs.edit().putStringSet("hidden_cards_set", currentHidden.map { it.name }.toSet()).apply()
        _hiddenCards.value = currentHidden
    }

    fun resetToDefault() {
        val defaultList = DashboardCardType.getDefaultList()
        saveOrder(defaultList)
        prefs.edit().remove("hidden_cards_set").apply()
        _hiddenCards.value = emptySet()
    }

    private fun saveOrder(newList: List<DashboardCardType>) {
        val csv = newList.joinToString(",") { it.name }
        prefs.edit().putString("card_order_csv", csv).apply()
        _cardOrder.value = newList
    }
}
