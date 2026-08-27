package com.example.modules.dashboard.logic

import com.example.core.storage.HouseholdRepository

object SampleDataInitializer {
    suspend fun populateDefaultFamilyData(repository: HouseholdRepository) {
        RealDataImporter.seedRealData(repository)
    }
}

