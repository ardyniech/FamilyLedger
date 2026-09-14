package com.example.modules.dashboard

import com.example.core.sync.p2p.Libp2pFraming
import com.example.core.sync.p2p.Libp2pPeerId
import com.example.core.sync.p2p.Libp2pProtocol
import com.example.core.sync.p2p.Libp2pSecurityChannel
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream

@RunWith(RobolectricTestRunner::class)
class Libp2pTechnicTest {

    @Test
    fun testLengthPrefixedFraming() {
        val sampleData = "Hello libp2p framing!".toByteArray(Charsets.UTF_8)
        val baos = ByteArrayOutputStream()
        Libp2pFraming.writeFrame(baos, sampleData)

        val bais = ByteArrayInputStream(baos.toByteArray())
        val decoded = Libp2pFraming.readFrame(bais)

        assertArrayEquals(sampleData, decoded)
        assertEquals("Hello libp2p framing!", String(decoded, Charsets.UTF_8))
    }

    @Test
    fun testSecurityChannelEncryptionAndDecryption() {
        val original = "Sensitive ledger data payload".toByteArray(Charsets.UTF_8)
        val pairCode = "SECURE-9921"

        val encryptedEnvelope = Libp2pSecurityChannel.encryptPayload(original, pairCode)
        assertNotNull(encryptedEnvelope)
        assertTrue(encryptedEnvelope.size > original.size)

        // Happy path: decrypt with correct pairCode
        val decrypted = Libp2pSecurityChannel.decryptPayload(encryptedEnvelope, pairCode)
        assertArrayEquals(original, decrypted)

        // Error path: wrong pairCode must throw authentication failure
        try {
            Libp2pSecurityChannel.decryptPayload(encryptedEnvelope, "WRONG-PAIR-CODE")
            fail("Expected exception for wrong pairCode in AEAD GCM verification")
        } catch (_: Exception) {
            // Success
        }
    }

    @Test
    fun testPeerIdAndMultiaddrFormatting() {
        val peerId = Libp2pPeerId.fromPairIdentity("FAM-8821", "Husband")
        assertTrue(peerId.id.startsWith("12D3KooW"))

        val multiaddr = peerId.toMultiaddr("192.168.43.1", 8888)
        assertEquals("/ip4/192.168.43.1/tcp/8888/p2p/${peerId.id}", multiaddr)

        val parsed = Libp2pPeerId.parseMultiaddr(multiaddr)
        assertNotNull(parsed)
        assertEquals("192.168.43.1", parsed?.first)
        assertEquals(8888, parsed?.second)
        assertEquals(peerId.id, parsed?.third)
    }

    @Test
    fun testMultistreamProtocolNegotiation() {
        val clientOut = PipedOutputStream()
        val serverIn = PipedInputStream(clientOut)

        val serverOut = PipedOutputStream()
        val clientIn = PipedInputStream(serverOut)

        var serverAgreedProto: String? = null
        val serverThread = Thread {
            serverAgreedProto = Libp2pProtocol.handleIncomingMultistream(
                serverIn,
                serverOut,
                setOf(Libp2pProtocol.SYNC_PROTOCOL_V1)
            )
        }
        serverThread.start()

        val clientSuccess = Libp2pProtocol.performMultistreamHandshake(
            clientIn,
            clientOut,
            Libp2pProtocol.SYNC_PROTOCOL_V1
        )
        serverThread.join(3000)

        assertTrue("Client negotiation should succeed", clientSuccess)
        assertEquals(Libp2pProtocol.SYNC_PROTOCOL_V1, serverAgreedProto)
    }
}
