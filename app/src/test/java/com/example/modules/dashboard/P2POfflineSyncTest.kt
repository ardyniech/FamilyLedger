package com.example.modules.dashboard

import com.example.core.sync.p2p.P2PSyncPackage
import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class P2POfflineSyncTest {

    @Test
    fun testP2PPackageSerializationAndCompression() {
        val member = Member("m1", "h1", "Husband", "Suami", "")
        val wallet = WalletAccount("w1", "m1", "Cash", "Dompet Suami", 500000L)
        val category = Category("c1", "Makanan", "Expense")
        val tx = Transaction("t1", "w1", "m1", "c1", -25000L, "Makan Siang", System.currentTimeMillis())

        val pkg = P2PSyncPackage(
            pairCode = "FAM-8821",
            senderName = "Suami",
            senderRole = "Husband",
            timestamp = System.currentTimeMillis(),
            transactions = listOf(tx),
            wallets = listOf(wallet),
            categories = listOf(category),
            members = listOf(member)
        )

        val compressedBase64 = pkg.toCompressedBase64()
        assertNotNull(compressedBase64)

        val restoredPkg = P2PSyncPackage.fromCompressedBase64(compressedBase64)
        assertEquals("FAM-8821", restoredPkg.pairCode)
        assertEquals("Suami", restoredPkg.senderName)
        assertEquals(1, restoredPkg.transactions.size)
        assertEquals("t1", restoredPkg.transactions[0].id)
        assertEquals(-25000L, restoredPkg.transactions[0].amount)
        assertEquals(1, restoredPkg.wallets.size)
        assertEquals("w1", restoredPkg.wallets[0].id)
    }
}
