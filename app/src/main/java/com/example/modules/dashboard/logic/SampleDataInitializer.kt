package com.example.modules.dashboard.logic

import com.example.core.storage.HouseholdRepository

object SampleDataInitializer {
    suspend fun populateDefaultFamilyData(repository: HouseholdRepository, pairCode: String = "FAM-8821") {
        RealDataImporter.seedRealData(repository, pairCode)
    }
}

