package com.example.core.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shared.models.CategoryGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryGroupDao {
    @Query("SELECT * FROM category_groups WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllCategoryGroups(): Flow<List<CategoryGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryGroup(group: CategoryGroup)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryGroups(groups: List<CategoryGroup>)

    @Query("DELETE FROM category_groups WHERE id = :id")
    suspend fun deleteCategoryGroup(id: String)

    @Query("SELECT * FROM category_groups WHERE id = :id LIMIT 1")
    suspend fun getCategoryGroupById(id: String): CategoryGroup?

    @Query("SELECT * FROM category_groups WHERE syncStatus = 0")
    suspend fun getPendingCategoryGroups(): List<CategoryGroup>

    @Query("UPDATE category_groups SET syncStatus = 1 WHERE id IN (:ids)")
    suspend fun markCategoryGroupsSynced(ids: List<String>)

    @Query("DELETE FROM category_groups")
    suspend fun clearCategoryGroups()
}
