package com.example.core.sync.p2p

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object Libp2pFraming {

    fun writeFrame(out: OutputStream, payload: ByteArray) {
        if (payload.size > Libp2pProtocol.MAX_FRAME_SIZE) {
            throw IOException("Payload exceeds max frame size: ${payload.size} > ${Libp2pProtocol.MAX_FRAME_SIZE}")
        }
        val dos = DataOutputStream(out)
        dos.writeInt(payload.size)
        dos.write(payload)
        dos.flush()
    }

    fun readFrame(input: InputStream): ByteArray {
        val dis = DataInputStream(input)
        val frameSize = dis.readInt()
        if (frameSize < 0 || frameSize > Libp2pProtocol.MAX_FRAME_SIZE) {
            throw IOException("Illegal frame size: $frameSize (Max allowed: ${Libp2pProtocol.MAX_FRAME_SIZE})")
        }
        val buffer = ByteArray(frameSize)
        dis.readFully(buffer)
        return buffer
    }
}
