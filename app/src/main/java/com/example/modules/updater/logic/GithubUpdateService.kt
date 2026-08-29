package com.example.modules.updater.logic

import android.util.Log
import com.example.modules.updater.models.ReleaseInfo
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object GithubUpdateService {
    private const val TAG = "GithubUpdateService"
    private val client = OkHttpClient()

    fun fetchLatestRelease(owner: String, repo: String): ReleaseInfo? {
        val url = "https://api.github.com/repos/$owner/$repo/releases/latest"
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/vnd.github.v3+json")
            .header("User-Agent", "FamilyLedger-Updater")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    when (response.code) {
                        404 -> {
                            Log.i(TAG, "No public release found (HTTP 404) for repository '$owner/$repo'. Assuming up-to-date.")
                            return null
                        }
                        403, 429 -> {
                            val rateLimitReset = response.header("X-RateLimit-Reset")
                            Log.w(TAG, "GitHub API rate limit exceeded (HTTP ${response.code}). Reset: $rateLimitReset")
                            return null
                        }
                        else -> {
                            Log.e(TAG, "Failed to fetch release: HTTP ${response.code}")
                            return null
                        }
                    }
                }
                val bodyStr = response.body?.string() ?: return null
                parseReleaseJson(bodyStr)
            }
        } catch (e: Exception) {
            Log.e(TAG, "[Module:Updater] Error in fetchLatestRelease: ${e.message}", e)
            null
        }
    }

    private fun parseReleaseJson(jsonStr: String): ReleaseInfo? {
        return try {
            val json = JSONObject(jsonStr)
            val id = json.getLong("id")
            val tagName = json.getString("tag_name")
            val name = json.optString("name", tagName)
            val body = json.optString("body", "")
            
            // Search for APK and SHA-256 assets
            val assets = json.getJSONArray("assets")
            var apkUrl = ""
            var apkName = ""
            var sha256Url: String? = null
            
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                val assetName = asset.getString("name")
                val downloadUrl = asset.getString("browser_download_url")
                
                if (assetName.endsWith(".apk")) {
                    // Prioritize universal or matching architecture if needed, otherwise take first apk
                    apkUrl = downloadUrl
                    apkName = assetName
                } else if (assetName.endsWith(".sha256") || assetName.lowercase().contains("checksums")) {
                    sha256Url = downloadUrl
                }
            }
            
            if (apkUrl.isEmpty()) {
                throw Exception("Rilis '$tagName' ditemukan di GitHub, tetapi tidak ada aset file .apk yang diunggah. Silakan unggah berkas APK ke rilis tersebut.")
            }

            // Determine if the release body indicates a mandatory update (e.g., contains [MANDATORY])
            val isMandatory = body.contains("[MANDATORY]", ignoreCase = true) || 
                               body.contains("security patch", ignoreCase = true)

            ReleaseInfo(
                id = id,
                tagName = tagName,
                name = name,
                body = body,
                apkUrl = apkUrl,
                apkName = apkName,
                sha256Url = sha256Url,
                isMandatory = isMandatory
            )
        } catch (e: Exception) {
            Log.e(TAG, "[Module:Updater] Error in parseReleaseJson: ${e.message}", e)
            throw e
        }
    }
}
