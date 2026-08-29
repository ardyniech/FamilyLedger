package com.example.modules.dashboard.logic

import com.example.core.storage.HouseholdRepository
import com.example.core.sync.p2p.P2POfflineSyncManager
import com.example.core.sync.p2p.P2PSyncPackage
import com.example.shared.models.Member
import kotlinx.coroutines.flow.firstOrNull

object DashboardSyncHelper {
    suspend fun exportPayload(p2p: P2POfflineSyncManager, pairCode: String, member: Member?): String {
        return p2p.createSyncPackage(pairCode, member?.name ?: "Unknown", member?.role ?: "Member").toCompressedBase64()
    }

    suspend fun importPayload(p2p: P2POfflineSyncManager, payload: String): Pair<Boolean, String> {
        return try {
            val res = p2p.importSyncPackage(P2PSyncPackage.fromCompressedBase64(payload))
            Pair(res.success, res.message)
        } catch (e: Exception) {
            Pair(false, "Format data tidak valid: ${e.message}")
        }
    }

    suspend fun initMockIfNeeded(repo: HouseholdRepository, pairCode: String) {
        val m = repo.members.firstOrNull()
        if (m.isNullOrEmpty() || m.none { it.name == "Ardy" }) {
            SampleDataInitializer.populateDefaultFamilyData(repo, pairCode)
        }
    }
}
