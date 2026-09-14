package com.example.core.sync.p2p

import java.security.MessageDigest

data class Libp2pPeerId(val id: String) {

    fun toMultiaddr(ip: String, port: Int): String {
        return "/ip4/$ip/tcp/$port/p2p/$id"
    }

    companion object {
        fun fromPairIdentity(pairCode: String, role: String): Libp2pPeerId {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest("familyledger:$pairCode:$role".toByteArray(Charsets.UTF_8))
            val hex = hash.take(16).joinToString("") { "%02x".format(it) }
            return Libp2pPeerId("12D3KooW$hex")
        }

        fun parseMultiaddr(multiaddr: String): Triple<String, Int, String>? {
            val parts = multiaddr.trim().split("/").filter { it.isNotEmpty() }
            // Expected format: ip4 / <ip> / tcp / <port> / p2p / <peerId>
            if (parts.size >= 6 && parts[0] == "ip4" && parts[2] == "tcp" && parts[4] == "p2p") {
                val ip = parts[1]
                val port = parts[3].toIntOrNull() ?: 8888
                val peerId = parts[5]
                return Triple(ip, port, peerId)
            }
            return null
        }
    }
}
