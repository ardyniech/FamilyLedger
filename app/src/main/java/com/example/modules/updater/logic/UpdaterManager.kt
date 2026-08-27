package com.example.modules.updater.logic

import android.content.Context
import android.util.Log
import com.example.modules.updater.models.ReleaseInfo
import com.example.modules.updater.models.UpdateStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class UpdaterManager(
    private val owner: String,
    private val repo: String,
    private val currentVersionName: String
) {
    private val _status = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val status: StateFlow<UpdateStatus> = _status.asStateFlow()

    private var currentReleaseInfo: ReleaseInfo? = null
    private var downloadedApkFile: File? = null

    fun checkForUpdates(scope: CoroutineScope) {
        _status.value = UpdateStatus.Checking
        scope.launch(Dispatchers.IO) {
            try {
                val latest = GithubUpdateService.fetchLatestRelease(owner, repo)
                if (latest != null && SemVerComparator.isNewer(latest.tagName, currentVersionName)) {
                    currentReleaseInfo = latest
                    _status.value = UpdateStatus.UpdateAvailable(latest)
                } else {
                    _status.value = UpdateStatus.UpToDate
                }
            } catch (e: Exception) {
                _status.value = UpdateStatus.Failed(e.message ?: "Gagal memeriksa pembaruan.")
            }
        }
    }

    fun startDownload(context: Context, scope: CoroutineScope) {
        val info = currentReleaseInfo ?: return
        scope.launch(Dispatchers.IO) {
            _status.value = UpdateStatus.Downloading(0f, 0.0, 0L, 0L)
            val apkFile = UpdateDownloader.downloadApk(context, info.apkUrl, info.apkName) { newStatus ->
                _status.value = newStatus
            }
            if (apkFile != null) {
                _status.value = UpdateStatus.Verifying("Verifying cryptographic SHA-256 signature...")
                val verified = ApkHashVerifier.verifyApkHash(apkFile, info.sha256Url, info.apkName)
                if (verified) {
                    downloadedApkFile = apkFile
                    _status.value = UpdateStatus.ReadyToInstall(apkFile.absolutePath)
                } else {
                    apkFile.delete()
                    _status.value = UpdateStatus.Failed("Security Verification Failed: SHA-256 mismatch!")
                }
            }
        }
    }

    fun installUpdate(context: Context) {
        val apkFile = downloadedApkFile ?: return
        if (!PackageInstallerManager.canInstallPackages(context)) {
            _status.value = UpdateStatus.Failed("Permission required: Install Unknown Apps.")
            context.startActivity(PackageInstallerManager.requestInstallPermissionIntent(context))
            return
        }
        PackageInstallerManager.triggerInstallation(context, apkFile)
    }

    fun resetToIdle() {
        _status.value = UpdateStatus.Idle
    }
}
