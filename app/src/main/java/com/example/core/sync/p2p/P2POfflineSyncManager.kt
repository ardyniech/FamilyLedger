package com.example.core.sync.p2p

import android.util.Log
import com.example.core.storage.HouseholdDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class P2POfflineSyncManager(private val dao: HouseholdDao) {

    private val isServerRunning = AtomicBoolean(false)
    private var serverSocket: ServerSocket? = null

    suspend fun createSyncPackage(
        pairCode: String,
        senderName: String,
        senderRole: String
    ): P2PSyncPackage = withContext(Dispatchers.IO) {
        val txs = dao.getAllTransactions().first()
        val wallets = dao.getAllWallets().first()
        val categories = dao.getAllCategories().first()
        val members = dao.getAllMembers().first()

        P2PSyncPackage(
            pairCode = pairCode,
            senderName = senderName,
            senderRole = senderRole,
            timestamp = System.currentTimeMillis(),
            transactions = txs,
            wallets = wallets,
            categories = categories,
            members = members
        )
    }

    suspend fun importSyncPackage(pkg: P2PSyncPackage): P2PImportResult = withContext(Dispatchers.IO) {
        var importedTxCount = 0
        var importedWalletCount = 0
        var importedCategoryCount = 0

        val existingTxs = dao.getAllTransactions().first().associateBy { it.id }
        val existingCategories = dao.getAllCategories().first().associateBy { it.id }
        val existingWallets = dao.getAllWallets().first().associateBy { it.id }
        val existingMembers = dao.getAllMembers().first().associateBy { it.id }

        // 1. Merge Members
        pkg.members.forEach { m ->
            if (!existingMembers.containsKey(m.id)) {
                dao.insertMember(m)
            }
        }

        // 2. Merge Categories
        pkg.categories.forEach { c ->
            if (!existingCategories.containsKey(c.id)) {
                dao.insertCategory(c)
                importedCategoryCount++
            }
        }

        // 3. Merge Wallets
        pkg.wallets.forEach { w ->
            if (!existingWallets.containsKey(w.id)) {
                dao.insertWallet(w)
                importedWalletCount++
            }
        }

        // 4. Merge Transactions and reconcile wallet balance
        pkg.transactions.forEach { t ->
            if (!existingTxs.containsKey(t.id)) {
                dao.addTransactionAndUpdateWallet(t)
                importedTxCount++
            }
        }

        P2PImportResult(
            success = true,
            importedTransactions = importedTxCount,
            importedWallets = importedWalletCount,
            importedCategories = importedCategoryCount,
            message = "Selesai mengimpor $importedTxCount transaksi & $importedWalletCount dompet dari ${pkg.senderName} (${pkg.senderRole})"
        )
    }

    suspend fun startLocalWifiHost(
        port: Int = 8888,
        pairCode: String,
        senderName: String,
        senderRole: String,
        onClientSynced: (P2PImportResult) -> Unit
    ) = withContext(Dispatchers.IO) {
        if (isServerRunning.get()) return@withContext
        try {
            serverSocket = ServerSocket(port)
            isServerRunning.set(true)
            Log.d("P2PSync", "[Module:P2POfflineSync] Info in startLocalWifiHost: Wi-Fi Direct P2P Host active on port $port")

            while (isServerRunning.get()) {
                val clientSocket = serverSocket?.accept() ?: break
                launchClientHandler(clientSocket, pairCode, senderName, senderRole, onClientSynced)
            }
        } catch (e: Exception) {
            Log.e("P2PSync", "[Module:P2POfflineSync] Error in startLocalWifiHost: ${e.message}")
        } finally {
            stopLocalWifiHost()
        }
    }

    private fun launchClientHandler(
        socket: Socket,
        pairCode: String,
        senderName: String,
        senderRole: String,
        onClientSynced: (P2PImportResult) -> Unit
    ) {
        Thread {
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val writer = PrintWriter(socket.getOutputStream(), true)

                // Read incoming package json from client
                val incomingCompressed = reader.readLine()
                if (!incomingCompressed.isNullOrEmpty()) {
                    val incomingPkg = P2PSyncPackage.fromCompressedBase64(incomingCompressed)
                    val importRes = kotlinx.coroutines.runBlocking { importSyncPackage(incomingPkg) }

                    // Send back host's package
                    val hostPkg = kotlinx.coroutines.runBlocking { createSyncPackage(pairCode, senderName, senderRole) }
                    writer.println(hostPkg.toCompressedBase64())

                    onClientSynced(importRes)
                }
            } catch (e: Exception) {
                Log.e("P2PSync", "[Module:P2POfflineSync] Error in launchClientHandler: ${e.message}")
            } finally {
                try { socket.close() } catch (_: Exception) {}
            }
        }.start()
    }

    suspend fun syncWithLocalWifiHost(
        hostIp: String,
        port: Int = 8888,
        pairCode: String,
        senderName: String,
        senderRole: String
    ): P2PImportResult = withContext(Dispatchers.IO) {
        try {
            val socket = Socket(hostIp, port)
            socket.soTimeout = 8000
            val writer = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            val myPkg = createSyncPackage(pairCode, senderName, senderRole)
            writer.println(myPkg.toCompressedBase64())

            val responseCompressed = reader.readLine()
            socket.close()

            if (!responseCompressed.isNullOrEmpty()) {
                val hostPkg = P2PSyncPackage.fromCompressedBase64(responseCompressed)
                importSyncPackage(hostPkg)
            } else {
                P2PImportResult(false, 0, 0, 0, "Host tidak memberikan balasan data")
            }
        } catch (e: Exception) {
            P2PImportResult(false, 0, 0, 0, "Gagal terhubung ke host IP $hostIp: ${e.message}")
        }
    }

    fun stopLocalWifiHost() {
        isServerRunning.set(false)
        try {
            serverSocket?.close()
        } catch (_: Exception) {}
        serverSocket = null
    }
}

data class P2PImportResult(
    val success: Boolean,
    val importedTransactions: Int,
    val importedWallets: Int,
    val importedCategories: Int,
    val message: String
)
