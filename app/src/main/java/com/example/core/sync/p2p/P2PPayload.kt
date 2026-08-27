package com.example.core.sync.p2p

import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

enum class P2PTransportType(val label: String) {
    WIFI_DIRECT("Wi-Fi Direct / Local Hotspot"),
    BLUETOOTH("Bluetooth Nearby"),
    NFC("NFC Touch Bump"),
    QR_CODE("QR Code Off-Grid"),
    MANUAL_PAYLOAD("Manual Text Payload")
}

data class P2PSyncPackage(
    val pairCode: String,
    val senderName: String,
    val senderRole: String,
    val timestamp: Long,
    val transactions: List<Transaction>,
    val wallets: List<WalletAccount>,
    val categories: List<Category>,
    val members: List<Member>
) {
    fun toJsonString(): String {
        val root = JSONObject()
        root.put("pairCode", pairCode)
        root.put("senderName", senderName)
        root.put("senderRole", senderRole)
        root.put("timestamp", timestamp)

        val txArray = JSONArray()
        transactions.forEach { t ->
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("walletId", t.walletId)
            obj.put("memberId", t.memberId)
            obj.put("categoryId", t.categoryId)
            obj.put("amount", t.amount)
            obj.put("note", t.note)
            obj.put("timestamp", t.timestamp)
            txArray.put(obj)
        }
        root.put("transactions", txArray)

        val walletArray = JSONArray()
        wallets.forEach { w ->
            val obj = JSONObject()
            obj.put("id", w.id)
            obj.put("memberId", w.memberId)
            obj.put("type", w.type)
            obj.put("name", w.name)
            obj.put("balance", w.balance)
            walletArray.put(obj)
        }
        root.put("wallets", walletArray)

        val catArray = JSONArray()
        categories.forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("name", c.name)
            obj.put("type", c.type)
            c.parentId?.let { obj.put("parentId", it) }
            catArray.put(obj)
        }
        root.put("categories", catArray)

        val memArray = JSONArray()
        members.forEach { m ->
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("householdId", m.householdId)
            obj.put("name", m.name)
            obj.put("role", m.role)
            obj.put("avatarUrl", m.avatarUrl)
            memArray.put(obj)
        }
        root.put("members", memArray)

        return root.toString()
    }

    fun toCompressedBase64(): String {
        val json = toJsonString()
        val bos = ByteArrayOutputStream()
        val gzos = GZIPOutputStream(bos)
        gzos.write(json.toByteArray(Charsets.UTF_8))
        gzos.finish()
        gzos.close()
        return Base64.getEncoder().encodeToString(bos.toByteArray())
    }

    companion object {
        fun fromJsonString(jsonString: String): P2PSyncPackage {
            val root = JSONObject(jsonString)
            val pairCode = root.optString("pairCode", "FAM-8821")
            val senderName = root.optString("senderName", "Pasangan")
            val senderRole = root.optString("senderRole", "Husband")
            val timestamp = root.optLong("timestamp", System.currentTimeMillis())

            val txList = mutableListOf<Transaction>()
            val txArr = root.optJSONArray("transactions") ?: JSONArray()
            for (i in 0 until txArr.length()) {
                val o = txArr.getJSONObject(i)
                txList.add(
                    Transaction(
                        id = o.getString("id"),
                        walletId = o.getString("walletId"),
                        memberId = o.getString("memberId"),
                        categoryId = o.getString("categoryId"),
                        amount = o.getDouble("amount"),
                        note = o.optString("note", ""),
                        timestamp = o.getLong("timestamp")
                    )
                )
            }

            val walletList = mutableListOf<WalletAccount>()
            val wArr = root.optJSONArray("wallets") ?: JSONArray()
            for (i in 0 until wArr.length()) {
                val o = wArr.getJSONObject(i)
                walletList.add(
                    WalletAccount(
                        id = o.getString("id"),
                        memberId = o.getString("memberId"),
                        type = o.getString("type"),
                        name = o.getString("name"),
                        balance = o.getDouble("balance")
                    )
                )
            }

            val catList = mutableListOf<Category>()
            val cArr = root.optJSONArray("categories") ?: JSONArray()
            for (i in 0 until cArr.length()) {
                val o = cArr.getJSONObject(i)
                catList.add(
                    Category(
                        id = o.getString("id"),
                        name = o.getString("name"),
                        type = o.getString("type"),
                        parentId = if (o.has("parentId")) o.getString("parentId") else null
                    )
                )
            }

            val memList = mutableListOf<Member>()
            val mArr = root.optJSONArray("members") ?: JSONArray()
            for (i in 0 until mArr.length()) {
                val o = mArr.getJSONObject(i)
                memList.add(
                    Member(
                        id = o.getString("id"),
                        householdId = o.optString("householdId", "h1"),
                        role = o.getString("role"),
                        name = o.getString("name"),
                        avatarUrl = o.optString("avatarUrl", "")
                    )
                )
            }

            return P2PSyncPackage(
                pairCode = pairCode,
                senderName = senderName,
                senderRole = senderRole,
                timestamp = timestamp,
                transactions = txList,
                wallets = walletList,
                categories = catList,
                members = memList
            )
        }

        fun fromCompressedBase64(base64Str: String): P2PSyncPackage {
            val bytes = Base64.getDecoder().decode(base64Str.trim())
            val gis = GZIPInputStream(ByteArrayInputStream(bytes))
            val json = gis.bufferedReader(Charsets.UTF_8).use { it.readText() }
            return fromJsonString(json)
        }
    }
}
