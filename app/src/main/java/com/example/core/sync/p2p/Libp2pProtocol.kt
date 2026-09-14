package com.example.core.sync.p2p

import java.io.InputStream
import java.io.OutputStream

object Libp2pProtocol {
    const val MULTISTREAM_ID = "/multistream/1.0.0"
    const val SYNC_PROTOCOL_V1 = "/familyledger/sync/1.0.0"
    const val PING_PROTOCOL_V1 = "/familyledger/ping/1.0.0"
    const val PROTOCOL_NA = "na"
    const val MAX_FRAME_SIZE = 8 * 1024 * 1024 // 8 MB safety limit to protect RAM

    fun performMultistreamHandshake(input: InputStream, output: OutputStream, targetProtocol: String): Boolean {
        // Step 1: Multistream header negotiation
        Libp2pFraming.writeFrame(output, (MULTISTREAM_ID + "\n").toByteArray(Charsets.UTF_8))
        val remoteMultistream = String(Libp2pFraming.readFrame(input), Charsets.UTF_8).trim()
        if (remoteMultistream != MULTISTREAM_ID) {
            Libp2pFraming.writeFrame(output, (PROTOCOL_NA + "\n").toByteArray(Charsets.UTF_8))
            return false
        }

        // Step 2: Negotiate specific application protocol
        Libp2pFraming.writeFrame(output, (targetProtocol + "\n").toByteArray(Charsets.UTF_8))
        val remoteProtocol = String(Libp2pFraming.readFrame(input), Charsets.UTF_8).trim()
        return remoteProtocol == targetProtocol
    }

    fun handleIncomingMultistream(input: InputStream, output: OutputStream, supportedProtocols: Set<String>): String? {
        val clientMultistream = String(Libp2pFraming.readFrame(input), Charsets.UTF_8).trim()
        if (clientMultistream != MULTISTREAM_ID) {
            Libp2pFraming.writeFrame(output, (PROTOCOL_NA + "\n").toByteArray(Charsets.UTF_8))
            return null
        }
        Libp2pFraming.writeFrame(output, (MULTISTREAM_ID + "\n").toByteArray(Charsets.UTF_8))

        val requestedProtocol = String(Libp2pFraming.readFrame(input), Charsets.UTF_8).trim()
        return if (supportedProtocols.contains(requestedProtocol)) {
            Libp2pFraming.writeFrame(output, (requestedProtocol + "\n").toByteArray(Charsets.UTF_8))
            requestedProtocol
        } else {
            Libp2pFraming.writeFrame(output, (PROTOCOL_NA + "\n").toByteArray(Charsets.UTF_8))
            null
        }
    }
}
