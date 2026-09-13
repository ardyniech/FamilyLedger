package com.example.modules.dashboard.logic

import com.example.shared.models.Category
import com.example.shared.models.CategoryGroup
import com.example.shared.models.HouseholdRole

data class RoleTemplateDefinition(
    val role: HouseholdRole,
    val themeName: String,
    val groups: List<CategoryGroup>,
    val categories: List<Category>,
    val sampleHighlights: List<String>
)
