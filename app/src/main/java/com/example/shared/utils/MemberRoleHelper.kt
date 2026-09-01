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

    fun isPrimaryRole(member: Member?, allMembers: List<Member> = emptyList()): Boolean {
        if (member == null) return false
        val idx = if (allMembers.isNotEmpty()) allMembers.indexOfFirst { it.id == member.id } else -1
        if (idx == 0) return true
        val r = member.role.trim().lowercase()
        return r in listOf("suami", "husband", "partner a", "pasangan 1", "spouse 1", "ayah", "bapak")
    }

    fun getPartnerA(members: List<Member>): Member? {
        return members.find { isPrimaryRole(it, members) } ?: members.getOrNull(0)
    }

    fun getPartnerB(members: List<Member>): Member? {
        val partnerA = getPartnerA(members)
        return members.find { it.id != partnerA?.id } ?: members.getOrNull(1)
    }

    fun getRoleColor(member: Member?, allMembers: List<Member> = emptyList()): Color {
        if (member == null) return DesignTokens.CobaltAccent
        val idx = if (allMembers.isNotEmpty()) allMembers.indexOfFirst { it.id == member.id } else -1
        return when {
            idx == 0 || isPrimaryRole(member, allMembers) -> DesignTokens.CobaltAccent
            idx == 1 || member.role.equals("Istri", ignoreCase = true) || member.role.equals("Wife", ignoreCase = true) || member.role.equals("Partner B", ignoreCase = true) || member.role.equals("Pasangan 2", ignoreCase = true) || member.role.equals("Ibu", ignoreCase = true) -> DesignTokens.AmberAccent
            idx == 2 -> DesignTokens.EmeraldAccent
            else -> DesignTokens.PurpleAccent
        }
    }

    fun getRoleColor(role: String): Color {
        val r = role.trim().lowercase()
        return when {
            r in listOf("suami", "husband", "partner a", "pasangan 1", "spouse 1", "ayah", "bapak") -> DesignTokens.CobaltAccent
            r in listOf("istri", "wife", "partner b", "pasangan 2", "spouse 2", "ibu", "mama") -> DesignTokens.AmberAccent
            else -> DesignTokens.EmeraldAccent
        }
    }

    fun getRoleEmoji(role: String): String {
        return when (role.trim().lowercase()) {
            "suami", "husband", "ayah", "bapak" -> "👨"
            "istri", "wife", "ibu", "mama" -> "👩"
            "partner a", "pasangan 1", "spouse 1" -> "🧑"
            "partner b", "pasangan 2", "spouse 2" -> "🌸"
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

    fun getDisplayName(member: Member?, default: String = "Pasangan"): String {
        return member?.name?.ifBlank { getPartnerLabel(member.role) } ?: default
    }
}

