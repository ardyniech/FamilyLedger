package com.example.core.storage

import java.security.MessageDigest

object FastCryptoDigest {
    private val HEX_CHARS = "0123456789abcdef".toCharArray()
    private val digestThreadLocal = ThreadLocal.withInitial {
        MessageDigest.getInstance("SHA-256")
    }

    fun sha256Hex(input: String): String {
        val digest = digestThreadLocal.get() ?: MessageDigest.getInstance("SHA-256")
        digest.reset()
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        val result = CharArray(bytes.size * 2)
        var i = 0
        for (b in bytes) {
            val v = b.toInt() and 0xFF
            result[i++] = HEX_CHARS[v ushr 4]
            result[i++] = HEX_CHARS[v and 0x0F]
        }
        return String(result)
    }
}
