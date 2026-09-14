package com.example.core.storage

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.io.File
import java.security.SecureRandom

object DatabasePassphraseHelper {
    private const val TAG = "DbPassphraseHelper"
    private const val PREFS_NAME = "ledger_db_crypto_prefs"
    private const val KEY_DB_PASSPHRASE = "db_passphrase_hex"

    fun isRobolectricOrTest(): Boolean {
        return try {
            Class.forName("org.robolectric.Robolectric")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }

    fun getOrCreatePassphrase(context: Context): ByteArray {
        val prefs = try {
            val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                PREFS_NAME,
                masterKey,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (_: Exception) {
            context.getSharedPreferences("${PREFS_NAME}_fallback", Context.MODE_PRIVATE)
        }

        var hex = prefs.getString(KEY_DB_PASSPHRASE, null)
        if (hex.isNullOrBlank()) {
            val bytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
            hex = bytes.joinToString("") { "%02x".format(it) }
            prefs.edit().putString(KEY_DB_PASSPHRASE, hex).apply()
        }
        return hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }

    fun prepareDatabase(context: Context, dbName: String, passphrase: ByteArray) {
        if (isRobolectricOrTest()) return
        val dbFile = context.getDatabasePath(dbName)
        if (!dbFile.exists() || dbFile.length() < 16) return

        try {
            net.sqlcipher.database.SQLiteDatabase.loadLibs(context)
        } catch (t: Throwable) {
            Log.e(TAG, "Failed loading SQLCipher libs: ${t.message}")
            return
        }

        val isPlaintext = try {
            val header = ByteArray(16)
            dbFile.inputStream().use { it.read(header) }
            String(header, 0, 15, Charsets.US_ASCII) == "SQLite format 3"
        } catch (_: Exception) {
            false
        }

        if (isPlaintext) {
            migrateOrResetPlaintextDb(dbFile, dbName, passphrase)
        } else {
            verifyOrResetEncryptedDb(dbFile, dbName, passphrase)
        }
    }

    private fun migrateOrResetPlaintextDb(dbFile: File, dbName: String, passphrase: ByteArray) {
        val tempPlain = File(dbFile.parentFile, "${dbName}_plain_temp")
        var migrated = false
        try {
            if (tempPlain.exists()) tempPlain.delete()
            if (dbFile.renameTo(tempPlain)) {
                val encDb = net.sqlcipher.database.SQLiteDatabase.openOrCreateDatabase(dbFile.absolutePath, passphrase, null)
                encDb.rawExecSQL("ATTACH DATABASE '${tempPlain.absolutePath}' AS plaintext KEY ''")
                encDb.rawExecSQL("SELECT sqlcipher_export('main')")
                encDb.rawExecSQL("DETACH DATABASE plaintext")
                encDb.close()
                tempPlain.delete()
                migrated = true
                Log.i(TAG, "Migrated plaintext database to SQLCipher successfully")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Plaintext migration failed (${e.message}), resetting database cleanly")
        } finally {
            if (!migrated) {
                deleteDbFiles(dbFile, dbName, tempPlain)
            }
        }
    }

    private fun verifyOrResetEncryptedDb(dbFile: File, dbName: String, passphrase: ByteArray) {
        try {
            val testDb = net.sqlcipher.database.SQLiteDatabase.openOrCreateDatabase(
                dbFile.absolutePath,
                passphrase,
                null
            )
            val cursor = testDb.rawQuery("SELECT count(*) FROM sqlite_master", null)
            cursor.moveToFirst()
            cursor.close()
            testDb.close()
        } catch (e: Exception) {
            Log.w(TAG, "Database unreadable with key (${e.message}), resetting database cleanly")
            deleteDbFiles(dbFile, dbName, null)
        }
    }

    private fun deleteDbFiles(dbFile: File, dbName: String, tempFile: File?) {
        try {
            tempFile?.delete()
            dbFile.delete()
            listOf("-shm", "-wal", "-journal").forEach { File(dbFile.parentFile, "$dbName$it").delete() }
        } catch (_: Exception) {}
    }
}
