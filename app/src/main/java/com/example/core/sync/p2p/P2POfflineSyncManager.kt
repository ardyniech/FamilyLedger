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
import java.util.concurrent.atomic.AtomicReference

class P2POfflineSyncManager(
    private val dao: HouseholdDao,
    private val auditDao: com.example.core.storage.LedgerAuditDao? = null
) {
    private val isServerRunning = AtomicBoolean(false)
    private var serverSocket: ServerSocket? = null
    private val syncProtocol = com.example.core.sync.SyncProtocol(dao, auditDao)
    private val pendingPairCode = AtomicReference<String>("")

    suspend fun createSyncPackage(pairCode: String, senderName: String, senderRole: String): P2PSyncPackage = withContext(Dispatchers.IO) {
        val txs = dao.getAllTransactions().first()
        val wallets = dao.getAllWallets().first()
        val categories = dao.getAllCategories().first()
        val members = dao.getAllMembers().first()
        P2PSyncPackage(pairCode, senderName, senderRole, System.currentTimeMillis(), txs, wallets, categories, members)
    }

    suspend fun importSyncPackage(pkg: P2PSyncPackage): P2PImportResult = withContext(Dispatchers.IO) {
        syncProtocol.reconcileAndCommit(pkg)
    }

    suspend fun startLocalWifiHost(port: Int = 8888, pairCode: String, senderName: String, senderRole: String, onClientSynced: (P2PImportResult) -> Unit) = withContext(Dispatchers.IO) {
        if (isServerRunning.get()) return@withContext
        try {
            serverSocket = ServerSocket(port)
            isServerRunning.set(true)
            pendingPairCode.set(pairCode)
            while (isServerRunning.get()) {
                val clientSocket = serverSocket?.accept() ?: break
                launchClientHandler(clientSocket, pairCode, senderName, senderRole, onClientSynced)
            }
        } catch (e: Exception) {
            Log.e("P2PSync", "[Module:P2POfflineSync] Error in startLocalWifiHost: ${e.message}")
        } finally { stopLocalWifiHost() }
    }

    private fun launchClientHandler(socket: Socket, pairCode: String, senderName: String, senderRole: String, onClientSynced: (P2PImportResult) -> Unit) {
        Thread {
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val writer = PrintWriter(socket.getOutputStream(), true)
                val incomingCompressed = reader.readLine()
                if (!incomingCompressed.isNullOrEmpty()) {
                    val incomingPkg = P2PSyncPackage.fromCompressedBase64(incomingCompressed)
                    // Verifikasi pair code sebelum sinkronisasi
                    val currentPairCode = pendingPairCode.get()
                    if (incomingPkg.pairCode != currentPairCode) {
                        Log.w("P2PSync", "[Module:P2POfflineSync] Pair code mismatch: expected $currentPairCode, got ${incomingPkg.pairCode}")
                        socket.close()
                        onClientSynced(P2PImportResult(false, 0, 0, 0, "Pair code tidak cocok"))
                        return@Thread
                    }
                    val importRes = kotlinx.coroutines.runBlocking { importSyncPackage(incomingPkg) }
                    val hostPkg = kotlinx.coroutines.runBlocking { createSyncPackage(currentPairCode, senderName, senderRole) }
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

    suspend fun syncWithLocalWifiHost(hostIp: String, port: Int = 8888, pairCode: String, senderName: String, senderRole: String): P2PImportResult = withContext(Dispatchers.IO) {
        try {
            val socket = Socket(hostIp, port).apply { soTimeout = 8000 }
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
        try { serverSocket?.close() } catch (_: Exception) {}
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
