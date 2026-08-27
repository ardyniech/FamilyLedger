package com.example.modules.updater.models

import java.io.Serializable

data class ReleaseInfo(
    val id: Long,
    val tagName: String,       // e.g. "v1.2.4"
    val name: String,          // e.g. "Security Patch 1.2.4"
    val body: String,          // Release notes / Changelog markdown
    val apkUrl: String,        // URL to download the target APK asset
    val apkName: String,       // Name of the target APK file
    val sha256Url: String?,    // Optional URL to download verification file
    val isMandatory: Boolean   // Forced security update flag
) : Serializable

sealed class UpdateStatus {
    object Idle : UpdateStatus()
    object Checking : UpdateStatus()
    data class UpdateAvailable(val info: ReleaseInfo) : UpdateStatus()
    object UpToDate : UpdateStatus()
    data class Downloading(
        val progress: Float,       // 0.0f to 1.0f
        val speedMbSec: Double,    // Speed in MB/s
        val downloadedBytes: Long,
        val totalBytes: Long
    ) : UpdateStatus()
    data class Verifying(val message: String) : UpdateStatus()
    data class ReadyToInstall(val apkPath: String) : UpdateStatus()
    data class Failed(val errorMsg: String) : UpdateStatus()
}
