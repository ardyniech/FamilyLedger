package com.example.modules.dashboard.logic

import android.content.Context
import android.widget.Toast
import com.example.core.storage.HouseholdRepository
import com.example.shared.models.Category
import com.example.shared.models.CategoryGroup
import com.example.shared.models.HouseholdRole
import com.example.shared.models.Member
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RolePersonalizationHelper(
    private val repository: HouseholdRepository,
    private val scope: CoroutineScope,
    private val context: Context
) {
    fun applyRoleAndTemplate(
        role: HouseholdRole,
        activeMember: Member?,
        existingCategories: List<Category>,
        existingGroups: List<CategoryGroup>,
        onComplete: ((RoleTemplateApplyResult) -> Unit)? = null
    ) {
        if (activeMember == null) return
        scope.launch {
            try {
                val result = RoleTemplateManager.applyTemplate(
                    role = role,
                    activeMember = activeMember,
                    repository = repository,
                    existingCategories = existingCategories,
                    existingGroups = existingGroups
                )
                Toast.makeText(context, result.feedbackMessage, Toast.LENGTH_LONG).show()
                onComplete?.invoke(result)
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal menerapkan template: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
