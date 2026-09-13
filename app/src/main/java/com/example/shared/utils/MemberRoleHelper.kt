package com.example.shared.utils

import androidx.compose.ui.graphics.Color
import com.example.shared.models.HouseholdRole
import com.example.shared.models.Member
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette

object MemberRoleHelper {
    val ROLE_PRESETS = listOf(HouseholdRole.SUAMI.code, HouseholdRole.ISTRI.code)

    fun isHusband(member: Member?): Boolean {
        if (member == null) return true
        return HouseholdRole.fromString(member.role) == HouseholdRole.SUAMI
    }

    fun isWife(member: Member?): Boolean {
        if (member == null) return false
        return HouseholdRole.fromString(member.role) == HouseholdRole.ISTRI
    }

    fun getPartnerA(members: List<Member>): Member? {
        return members.find { isHusband(it) } ?: members.firstOrNull()
    }

    fun getPartnerB(members: List<Member>): Member? {
        return members.find { isWife(it) } ?: members.getOrNull(1)
    }

    fun getRoleColor(role: String): Color {
        val hRole = HouseholdRole.fromString(role)
        return RoleThemePalette.forRole(hRole).primaryAccent
    }

    fun getRoleColor(member: Member?, defaultColor: Color = DesignTokens.CobaltAccent): Color {
        if (member == null) return defaultColor
        return getRoleColor(member.role)
    }

    fun getRoleColor(ownerId: String?, members: List<Member>): Color {
        val member = members.find { it.id == ownerId }
        return getRoleColor(member)
    }

    fun getRoleColor(member: Member?, members: List<Member>): Color {
        return getRoleColor(member)
    }

    fun getRoleEmoji(role: String): String {
        return HouseholdRole.fromString(role).emoji
    }

    fun getRoleEmoji(member: Member?): String {
        if (member == null) return HouseholdRole.SUAMI.emoji
        return getRoleEmoji(member.role)
    }

    fun getRoleEmoji(ownerId: String?, members: List<Member>): String {
        val member = members.find { it.id == ownerId }
        return getRoleEmoji(member)
    }

    fun getRoleEmoji(member: Member?, members: List<Member>): String {
        return getRoleEmoji(member)
    }

    fun getPartnerLabel(role: String): String {
        return HouseholdRole.fromString(role).code
    }

    fun getPartnerLabel(member: Member?): String {
        if (member == null) return HouseholdRole.SUAMI.code
        return getPartnerLabel(member.role)
    }

    fun getPartnerLabel(ownerId: String?, members: List<Member>): String {
        val member = members.find { it.id == ownerId }
        return getPartnerLabel(member)
    }

    fun getPartnerLabel(member: Member?, members: List<Member>): String {
        return getPartnerLabel(member)
    }

    fun getDisplayName(member: Member?, default: String = "Pasangan"): String {
        return member?.name?.ifBlank { getPartnerLabel(member.role) } ?: default
    }

    fun getDisplayName(memberId: String?, members: List<Member>, default: String = "Pasangan"): String {
        val member = members.find { it.id == memberId }
        return getDisplayName(member, default)
    }
}
