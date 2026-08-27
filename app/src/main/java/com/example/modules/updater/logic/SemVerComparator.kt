package com.example.modules.updater.logic

import android.util.Log

object SemVerComparator {
    private const val TAG = "SemVerComparator"

    fun isNewer(remoteTag: String, localVersion: String): Boolean {
        val cleanRemote = cleanVersion(remoteTag)
        val cleanLocal = cleanVersion(localVersion)
        
        println("Comparing Remote: $cleanRemote with Local: $cleanLocal")
        
        val remoteParts = cleanRemote.split(".").map { it.toIntOrNull() ?: 0 }
        val localParts = cleanLocal.split(".").map { it.toIntOrNull() ?: 0 }
        
        val maxLen = maxOf(remoteParts.size, localParts.size)
        for (i in 0 until maxLen) {
            val rVal = remoteParts.getOrElse(i) { 0 }
            val lVal = localParts.getOrElse(i) { 0 }
            if (rVal > lVal) return true
            if (rVal < lVal) return false
        }
        return false
    }

    private fun cleanVersion(version: String): String {
        return version.trim()
            .lowercase()
            .removePrefix("v")
            .split("-")[0] // ignore pre-release suffix for simple SemVer
    }
}
