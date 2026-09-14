package com.example.core.sync.p2p

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object Libp2pSecurityChannel {
    private const val GCM_TAG_LENGTH_BITS = 128
    private const val IV_LENGTH_BYTES = 12
    private const val SALT_LENGTH_BYTES = 16
    private const val ITERATION_COUNT = 2048
    private const val KEY_LENGTH_BITS = 256

    fun encryptPayload(plainBytes: ByteArray, pairCode: String): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH_BYTES).apply { random.nextBytes(this) }
        val iv = ByteArray(IV_LENGTH_BYTES).apply { random.nextBytes(this) }

        val key = deriveKey(pairCode, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        val cipherText = cipher.doFinal(plainBytes)

        // Envelope: salt (16) + iv (12) + cipherText
        return salt + iv + cipherText
    }

    fun decryptPayload(envelope: ByteArray, pairCode: String): ByteArray {
        if (envelope.size < SALT_LENGTH_BYTES + IV_LENGTH_BYTES + (GCM_TAG_LENGTH_BITS / 8)) {
            throw IllegalArgumentException("Corrupted or truncated libp2p secure envelope")
        }

        val salt = envelope.copyOfRange(0, SALT_LENGTH_BYTES)
        val iv = envelope.copyOfRange(SALT_LENGTH_BYTES, SALT_LENGTH_BYTES + IV_LENGTH_BYTES)
        val cipherText = envelope.copyOfRange(SALT_LENGTH_BYTES + IV_LENGTH_BYTES, envelope.size)

        val key = deriveKey(pairCode, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        return cipher.doFinal(cipherText)
    }

    private fun deriveKey(pairCode: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(pairCode.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH_BITS)
        val secretBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(secretBytes, "AES")
    }
}
