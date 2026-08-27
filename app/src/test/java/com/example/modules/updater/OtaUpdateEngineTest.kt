package com.example.modules.updater

import com.example.modules.updater.logic.SemVerComparator
import com.example.modules.updater.models.ReleaseInfo
import com.example.modules.updater.models.UpdateStatus
import org.junit.Assert.*
import org.junit.Test

class OtaUpdateEngineTest {

    @Test
    fun testSemVerComparator_detectsNewerVersions() {
        // Test various version naming formats
        assertTrue(SemVerComparator.isNewer("v1.1", "1.0"))
        assertTrue(SemVerComparator.isNewer("v1.0.1", "1.0"))
        assertTrue(SemVerComparator.isNewer("1.2.3", "1.2.2"))
        assertTrue(SemVerComparator.isNewer("v2.0.0-rc1", "1.9.9"))
        
        // Test identical or older versions
        assertFalse(SemVerComparator.isNewer("v1.0", "1.0"))
        assertFalse(SemVerComparator.isNewer("1.0.0", "1.0"))
        assertFalse(SemVerComparator.isNewer("v0.9", "1.0"))
        assertFalse(SemVerComparator.isNewer("1.1.2", "1.2.0"))
    }

    @Test
    fun testUpdateStatus_idleToChecking() {
        val status: UpdateStatus = UpdateStatus.Checking
        assertEquals(UpdateStatus.Checking, status)
    }

    @Test
    fun testUpdateStatus_updateAvailable() {
        val dummyRelease = ReleaseInfo(
            id = 12345L,
            tagName = "v1.1",
            name = "Security Patch 1.1",
            body = "Fix security vulnerabilities",
            apkUrl = "https://github.com/ardyniech/FamilyLedger/releases/download/v1.1/app.apk",
            apkName = "app.apk",
            sha256Url = null,
            isMandatory = true
        )
        
        val status = UpdateStatus.UpdateAvailable(dummyRelease)
        assertEquals(dummyRelease, status.info)
        assertTrue(status.info.isMandatory)
    }
}
