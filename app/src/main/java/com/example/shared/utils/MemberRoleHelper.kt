package com.example.shared.utils

import androidx.compose.ui.graphics.Color
import com.example.shared.models.Member
import com.example.shared.theme.DesignTokens

object MemberRoleHelper {
    val ROLE_PRESETS = listOf(
        Pair("Suami", "Istri"),
        Pair("Partner A", "Partner B"),
        Pair("Pasangan 1", "Pasangan 2"),
        Pair("Spouse 1", "Spouse 2")
    )

    fun getRoleColor(member: Member?, allMembers: List<Member>): Color {
        if (member == null) return DesignTokens.CobaltAccent
        val idx = allMembers.indexOfFirst { it.id == member.id }
        return when {
            idx == 0 || member.role.equals("Suami", ignoreCase = true) || member.role.equals("Husband", ignoreCase = true) || member.role.equals("Partner A", ignoreCase = true) || member.role.equals("Pasangan 1", ignoreCase = true) -> DesignTokens.CobaltAccent
            idx == 1 || member.role.equals("Istri", ignoreCase = true) || member.role.equals("Wife", ignoreCase = true) || member.role.equals("Partner B", ignoreCase = true) || member.role.equals("Pasangan 2", ignoreCase = true) -> DesignTokens.AmberAccent
            idx == 2 -> DesignTokens.EmeraldAccent
            else -> DesignTokens.PurpleAccent
        }
    }

    fun getRoleEmoji(role: String): String {
        return when (role.trim().lowercase()) {
            "suami", "husband" -> "👨"
            "istri", "wife" -> "👩"
            "partner a", "pasangan 1" -> "🧑"
            "partner b", "pasangan 2" -> "🌸"
            else -> "✨"
        }
    }

    fun getPartnerLabel(role: String): String {
        return when (role.trim().lowercase()) {
            "husband" -> "Suami"
            "wife" -> "Istri"
            else -> role
        }
    }
}
