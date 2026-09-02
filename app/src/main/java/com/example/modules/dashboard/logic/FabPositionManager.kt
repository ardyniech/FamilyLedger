package com.example.modules.dashboard.logic

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class FabPosition(val label: String, val description: String) {
    RIGHT("Tangan Kanan (Kanan Bawah)", "Posisi jempol kanan ergonomis"),
    LEFT("Tangan Kiri (Kiri Bawah)", "Posisi jempol kiri ergonomis")
}

class FabPositionManager(context: Context) {
    private val prefs = context.getSharedPreferences("fab_settings_prefs", Context.MODE_PRIVATE)

    private val _fabPosition = MutableStateFlow(loadFabPosition())
    val fabPosition: StateFlow<FabPosition> = _fabPosition.asStateFlow()

    private fun loadFabPosition(): FabPosition {
        val saved = prefs.getString("fab_position", FabPosition.RIGHT.name)
        return try {
            FabPosition.valueOf(saved ?: FabPosition.RIGHT.name)
        } catch (e: Exception) {
            FabPosition.RIGHT
        }
    }

    fun setFabPosition(position: FabPosition) {
        prefs.edit().putString("fab_position", position.name).apply()
        _fabPosition.value = position
    }
}
