package com.example.core.auth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AppLockManagerSecurityTest {

    private lateinit var context: Context
    private lateinit var appLockManager: AppLockManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        appLockManager = AppLockManager(context)
    }

    @Test
    fun testDefaultPinAndLockState() {
        assertNotNull(appLockManager)
        assertTrue(appLockManager.verifyPin("1234"))
        assertFalse(appLockManager.verifyPin("9999"))
    }

    @Test
    fun testSetAndVerifyPinWithEncryptedStorage() {
        appLockManager.setPin("5678")
        assertTrue(appLockManager.isLockEnabled())
        assertTrue(appLockManager.verifyPin("5678"))
        assertFalse(appLockManager.verifyPin("1234"))
        assertEquals("••••", appLockManager.getMaskedPin())
    }

    @Test
    fun testBiometricTogglePersistence() {
        assertFalse(appLockManager.isBiometricEnabled())
        appLockManager.setBiometricEnabled(true)
        assertTrue(appLockManager.isBiometricEnabled())
        appLockManager.setBiometricEnabled(false)
        assertFalse(appLockManager.isBiometricEnabled())
    }

    @Test
    fun testMalformedPinVerification() {
        assertFalse(appLockManager.verifyPin(""))
        assertFalse(appLockManager.verifyPin("abc"))
        assertFalse(appLockManager.verifyPin("12345"))
    }
}
