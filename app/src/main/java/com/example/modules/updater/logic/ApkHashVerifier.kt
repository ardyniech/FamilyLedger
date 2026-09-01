package com.example.modules.updater.logic

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

object ApkHashVerifier {
    private const val TAG = "ApkHashVerifier"

    fun verifyApkHash(apkFile: File, sha256Url: String?, expectedApkName: String): Boolean {
        if (sha256Url.isNullOrEmpty()) {
            Log.e(TAG, "SHA-256 verification URL missing; refusing to install APK without cryptographic verification.")
            return false
        }
        
        val localHash = calculateSha256(apkFile) ?: return false
        Log.d(TAG, "Local APK SHA-256: $localHash")
        
        val remoteHash = fetchRemoteHash(sha256Url, expectedApkName) ?: return false
        Log.d(TAG, "Remote SHA-256 Expected: $remoteHash")
        
        return localHash.equals(remoteHash, ignoreCase = true)
    }

    private fun calculateSha256(file: File): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            val hashBytes = digest.digest()
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e(TAG, "[Module:Updater] Error in calculateSha256: ${e.message}", e)
            null
        }
    }

    private fun fetchRemoteHash(url: String, apkName: String): String? {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val rawContent = response.body?.string()?.trim() ?: return null
                
                // Parse standard checksum file formats (e.g., "hash filename" or just the hash)
                val lines = rawContent.split("\n")
                for (line in lines) {
                    val parts = line.split(Regex("\\s+"))
                    if (parts.isNotEmpty()) {
                        val potentialHash = parts[0].trim()
                        if (potentialHash.length == 64) {
                            if (parts.size == 1 || line.contains(apkName, ignoreCase = true)) {
                                return potentialHash
                            }
                        }
                    }
                }
                // Fallback: If the whole file is just a 64-char hash string
                if (rawContent.length == 64) rawContent else null
            }
        } catch (e: Exception) {
            Log.e(TAG, "[Module:Updater] Error in fetchRemoteHash: ${e.message}", e)
            null
        }
    }
}
