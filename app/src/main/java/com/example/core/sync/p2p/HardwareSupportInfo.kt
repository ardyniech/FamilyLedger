package com.example.core.sync.p2p

import android.content.Context
import android.content.pm.PackageManager
import android.nfc.NfcAdapter

data class HardwareSupportStatus(
    val hasBluetooth: Boolean,
    val isBluetoothEnabled: Boolean,
    val hasWifiDirect: Boolean,
    val hasNfc: Boolean,
    val isNfcEnabled: Boolean
)

object HardwareSupportChecker {
    fun checkHardwareSupport(context: Context): HardwareSupportStatus {
        val pm = context.packageManager

        val hasBt = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
        var isBtEnabled = false
        if (hasBt) {
            try {
                val adapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
                isBtEnabled = adapter?.isEnabled == true
            } catch (e: SecurityException) {
                isBtEnabled = false
            } catch (e: Exception) {
                isBtEnabled = false
            }
        }

        val hasWifi = pm.hasSystemFeature(PackageManager.FEATURE_WIFI) || pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)

        val hasNfcFeature = pm.hasSystemFeature(PackageManager.FEATURE_NFC)
        var isNfcActive = false
        if (hasNfcFeature) {
            try {
                val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
                isNfcActive = nfcAdapter?.isEnabled == true
            } catch (e: Exception) {
                isNfcActive = false
            }
        }

        return HardwareSupportStatus(
            hasBluetooth = hasBt,
            isBluetoothEnabled = isBtEnabled,
            hasWifiDirect = hasWifi,
            hasNfc = hasNfcFeature,
            isNfcEnabled = isNfcActive
        )
    }
}
