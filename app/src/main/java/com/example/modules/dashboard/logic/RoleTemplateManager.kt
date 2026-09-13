package com.example.modules.dashboard.logic

import com.example.core.storage.HouseholdRepository
import com.example.shared.models.Category
import com.example.shared.models.CategoryGroup
import com.example.shared.models.HouseholdRole
import com.example.shared.models.Member
import kotlinx.coroutines.flow.firstOrNull

data class RoleTemplateApplyResult(
    val role: HouseholdRole,
    val addedGroupsCount: Int,
    val addedCategoriesCount: Int,
    val addedWalletsCount: Int = 0,
    val feedbackMessage: String
)

object RoleTemplateManager {
    suspend fun applyTemplate(
        role: HouseholdRole,
        activeMember: Member,
        repository: HouseholdRepository,
        existingCategories: List<Category>,
        existingGroups: List<CategoryGroup>
    ): RoleTemplateApplyResult {
        // 1. Update member role
        val updatedMember = activeMember.copy(
            role = role.code,
            updatedAt = System.currentTimeMillis()
        )
        repository.addMember(updatedMember)

        // 2. Prepare Template Groups
        val templateGroups = RoleTemplateCategoryData.getGroupsForRole(role)
        val groupsToInsert = templateGroups.filter { tg ->
            existingGroups.none { it.id == tg.id || it.name.equals(tg.name, ignoreCase = true) }
        }
        if (groupsToInsert.isNotEmpty()) {
            repository.insertCategoryGroups(groupsToInsert)
        }

        // 3. Prepare Template Categories
        val templateCategories = RoleTemplateCategoryData.getCategoriesForRole(role)
        val categoriesToInsert = templateCategories.filter { tc ->
            existingCategories.none { it.id == tc.id || it.name.equals(tc.name, ignoreCase = true) }
        }
        if (categoriesToInsert.isNotEmpty()) {
            repository.insertCategories(categoriesToInsert)
        }

        // 4. Auto-provision default wallets if wife has none
        var addedWallets = 0
        if (role == HouseholdRole.ISTRI) {
            val allWallets = repository.wallets.firstOrNull() ?: emptyList()
            val memberWallets = allWallets.filter { it.memberId == activeMember.id }
            if (memberWallets.isEmpty()) {
                val wifeWallets = WifeDefaultWallets.createDefaultWallets(activeMember.id)
                repository.insertWallets(wifeWallets)
                addedWallets = wifeWallets.size
            }
        }

        val baseFeedback = role.templateFeedback.format(categoriesToInsert.size)
        val feedback = if (addedWallets > 0) "$baseFeedback + $addedWallets dompet belanja siap pakai." else baseFeedback

        return RoleTemplateApplyResult(
            role = role,
            addedGroupsCount = groupsToInsert.size,
            addedCategoriesCount = categoriesToInsert.size,
            addedWalletsCount = addedWallets,
            feedbackMessage = feedback
        )
    }
}
