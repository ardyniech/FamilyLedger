package com.example.core.sync.p2p

import android.util.Log
import com.example.core.storage.HouseholdDao
import com.example.core.sync.SyncProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class P2POfflineSyncManager(
    private val dao: HouseholdDao,
    private val auditDao: com.example.core.storage.LedgerAuditDao? = null
) {
    private val isServerRunning = AtomicBoolean(false)
    private var serverSocket: ServerSocket? = null
    private val syncProtocol = SyncProtocol(dao, auditDao)

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

    fun getLocalMultiaddr(ip: String, port: Int, pairCode: String, role: String): String {
        return Libp2pPeerId.fromPairIdentity(pairCode, role).toMultiaddr(ip, port)
    }

    suspend fun startLocalWifiHost(port: Int = 8888, pairCode: String, senderName: String, senderRole: String, onClientSynced: (P2PImportResult) -> Unit) = withContext(Dispatchers.IO) {
        if (isServerRunning.get()) return@withContext
        try {
            serverSocket = ServerSocket(port).apply { reuseAddress = true }
            isServerRunning.set(true)
            while (isServerRunning.get()) {
                val clientSocket = serverSocket?.accept() ?: break
                clientSocket.soTimeout = 10000
                Thread { handleIncomingClient(clientSocket, pairCode, senderName, senderRole, onClientSynced) }.start()
            }
        } catch (e: Exception) {
            Log.e("P2PSync", "[Module:P2POfflineSync] Host error: ${e.message}")
        } finally { stopLocalWifiHost() }
    }

    private fun handleIncomingClient(socket: Socket, pairCode: String, name: String, role: String, onSynced: (P2PImportResult) -> Unit) {
        try {
            val input = socket.getInputStream()
            val output = socket.getOutputStream()
            val proto = Libp2pProtocol.handleIncomingMultistream(input, output, setOf(Libp2pProtocol.SYNC_PROTOCOL_V1))
            if (proto != Libp2pProtocol.SYNC_PROTOCOL_V1) {
                onSynced(P2PImportResult(false, 0, 0, 0, "Protokol ditolak (bukan /familyledger/sync/1.0.0)"))
                return
            }
            val encryptedIncoming = Libp2pFraming.readFrame(input)
            val rawIncoming = Libp2pSecurityChannel.decryptPayload(encryptedIncoming, pairCode)
            val incomingPkg = P2PSyncPackage.fromCompressedBase64(String(rawIncoming, Charsets.UTF_8))
            val importRes = kotlinx.coroutines.runBlocking { importSyncPackage(incomingPkg) }

            val hostPkg = kotlinx.coroutines.runBlocking { createSyncPackage(pairCode, name, role) }
            val encryptedHostPkg = Libp2pSecurityChannel.encryptPayload(hostPkg.toCompressedBase64().toByteArray(Charsets.UTF_8), pairCode)
            Libp2pFraming.writeFrame(output, encryptedHostPkg)
            onSynced(importRes)
        } catch (e: Exception) {
            Log.e("P2PSync", "[Module:P2POfflineSync] Client handling error: ${e.message}")
            onSynced(P2PImportResult(false, 0, 0, 0, "Kegagalan dekripsi/handshake: ${e.message}"))
        } finally {
            try { socket.close() } catch (_: Exception) {}
        }
    }

    suspend fun syncWithLocalWifiHost(hostIp: String, port: Int = 8888, pairCode: String, senderName: String, senderRole: String): P2PImportResult = withContext(Dispatchers.IO) {
        var socket: Socket? = null
        try {
            socket = Socket(hostIp, port).apply { soTimeout = 10000 }
            val input = socket.getInputStream()
            val output = socket.getOutputStream()
            val agreed = Libp2pProtocol.performMultistreamHandshake(input, output, Libp2pProtocol.SYNC_PROTOCOL_V1)
            if (!agreed) {
                return@withContext P2PImportResult(false, 0, 0, 0, "Negosiasi multistream gagal dengan host $hostIp")
            }
            val myPkg = createSyncPackage(pairCode, senderName, senderRole)
            val encryptedMyPkg = Libp2pSecurityChannel.encryptPayload(myPkg.toCompressedBase64().toByteArray(Charsets.UTF_8), pairCode)
            Libp2pFraming.writeFrame(output, encryptedMyPkg)

            val encryptedResponse = Libp2pFraming.readFrame(input)
            val rawResponse = Libp2pSecurityChannel.decryptPayload(encryptedResponse, pairCode)
            val hostPkg = P2PSyncPackage.fromCompressedBase64(String(rawResponse, Charsets.UTF_8))
            importSyncPackage(hostPkg)
        } catch (e: Exception) {
            P2PImportResult(false, 0, 0, 0, "Koneksi P2P libp2p gagal: ${e.message}")
        } finally {
            try { socket?.close() } catch (_: Exception) {}
        }
    }

    fun stopLocalWifiHost() {
        isServerRunning.set(false)
        try { serverSocket?.close() } catch (_: Exception) {}
        serverSocket = null
    }
}
