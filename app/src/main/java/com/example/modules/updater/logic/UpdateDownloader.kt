package com.example.modules.updater.logic

import android.content.Context
import android.util.Log
import com.example.modules.updater.models.UpdateStatus
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.SocketTimeoutException

object UpdateDownloader {
    private const val TAG = "UpdateDownloader"
    private val client = OkHttpClient()

    fun downloadApk(
        context: Context,
        url: String,
        apkName: String,
        onProgress: (UpdateStatus) -> Unit
    ): File? {
        val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
        val targetFile = File(updatesDir, apkName)
        
        var attempt = 0
        var backoffMs = 1000L
        val maxAttempts = 3

        while (attempt < maxAttempts) {
            try {
                if (performDownload(url, targetFile, onProgress)) {
                    return targetFile
                }
            } catch (e: Exception) {
                attempt++
                Log.w(TAG, "Download attempt $attempt failed: ${e.message}")
                if (attempt >= maxAttempts) {
                    onProgress(UpdateStatus.Failed("Download failed after $maxAttempts attempts."))
                    return null
                }
                onProgress(UpdateStatus.Failed("Network dropped. Retrying in ${backoffMs / 1000}s..."))
                Thread.sleep(backoffMs)
                backoffMs *= 2 // Exponential backoff
            }
        }
        return null
    }

    private fun performDownload(
        url: String,
        targetFile: File,
        onProgress: (UpdateStatus) -> Unit
    ): Boolean {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val body = response.body ?: throw IOException("Empty response body")
            val totalBytes = body.contentLength()
            
            body.byteStream().use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalRead = 0L
                    var startTime = System.currentTimeMillis()
                    var lastUpdate = System.currentTimeMillis()

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        
                        val now = System.currentTimeMillis()
                        if (now - lastUpdate >= 150L || totalRead == totalBytes) {
                            val durationSec = (now - startTime) / 1000.0
                            val speed = if (durationSec > 0) (totalRead / (1024.0 * 1024.0)) / durationSec else 0.0
                            val progress = if (totalBytes > 0) totalRead.toFloat() / totalBytes else 0f
                            
                            onProgress(
                                UpdateStatus.Downloading(
                                    progress = progress,
                                    speedMbSec = speed,
                                    downloadedBytes = totalRead,
                                    totalBytes = totalBytes
                                )
                            )
                            lastUpdate = now
                        }
                    }
                }
            }
        }
        return true
    }
}
