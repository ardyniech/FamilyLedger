package com.example.core.sync.p2p

import com.example.shared.models.*
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
        val root = JSONObject().apply {
            put("pairCode", pairCode); put("senderName", senderName); put("senderRole", senderRole); put("timestamp", timestamp)
            put("transactions", JSONArray().apply { transactions.forEach { t -> put(JSONObject().apply { put("id", t.id); put("walletId", t.walletId); put("memberId", t.memberId); put("categoryId", t.categoryId); put("amount", t.amount); put("note", t.note); put("timestamp", t.timestamp) }) } })
            put("wallets", JSONArray().apply { wallets.forEach { w -> put(JSONObject().apply { put("id", w.id); put("memberId", w.memberId); put("type", w.type); put("name", w.name); put("balance", w.balance) }) } })
            put("categories", JSONArray().apply { categories.forEach { c -> put(JSONObject().apply { put("id", c.id); put("name", c.name); put("type", c.type); put("budgetLimit", c.budgetLimit); c.parentId?.let { put("parentId", it) } }) } })
            put("members", JSONArray().apply { members.forEach { m -> put(JSONObject().apply { put("id", m.id); put("householdId", m.householdId); put("role", m.role); put("name", m.name); put("avatarUrl", m.avatarUrl) }) } })
        }
        return root.toString()
    }

    fun toCompressedBase64(): String {
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).use { it.write(toJsonString().toByteArray(Charsets.UTF_8)) }
        return Base64.getEncoder().encodeToString(bos.toByteArray())
    }

    companion object {
        fun fromJsonString(jsonString: String): P2PSyncPackage {
            val root = JSONObject(jsonString)
            val txArr = root.optJSONArray("transactions") ?: JSONArray()
            val txList = (0 until txArr.length()).map { i ->
                val o = txArr.getJSONObject(i)
                Transaction(o.getString("id"), o.getString("walletId"), o.getString("memberId"), o.getString("categoryId"), o.getDouble("amount"), o.optString("note", ""), o.getLong("timestamp"))
            }
            val wArr = root.optJSONArray("wallets") ?: JSONArray()
            val walletList = (0 until wArr.length()).map { i ->
                val o = wArr.getJSONObject(i)
                WalletAccount(o.getString("id"), o.getString("memberId"), o.getString("type"), o.getString("name"), o.getDouble("balance"))
            }
            val cArr = root.optJSONArray("categories") ?: JSONArray()
            val catList = (0 until cArr.length()).map { i ->
                val o = cArr.getJSONObject(i)
                Category(id = o.getString("id"), name = o.getString("name"), type = o.getString("type"), parentId = if (o.has("parentId")) o.getString("parentId") else null, budgetLimit = if (o.has("budgetLimit")) o.getDouble("budgetLimit") else 0.0)
            }
            val mArr = root.optJSONArray("members") ?: JSONArray()
            val memList = (0 until mArr.length()).map { i ->
                val o = mArr.getJSONObject(i)
                Member(o.getString("id"), o.optString("householdId", "h1"), o.getString("role"), o.getString("name"), o.optString("avatarUrl", ""))
            }
            return P2PSyncPackage(root.optString("pairCode", "FAM-8821"), root.optString("senderName", "Pasangan"), root.optString("senderRole", "Husband"), root.optLong("timestamp", System.currentTimeMillis()), txList, walletList, catList, memList)
        }

        fun fromCompressedBase64(base64Str: String): P2PSyncPackage {
            val bytes = Base64.getDecoder().decode(base64Str.trim())
            val json = GZIPInputStream(ByteArrayInputStream(bytes)).bufferedReader(Charsets.UTF_8).use { it.readText() }
            return fromJsonString(json)
        }
    }
}
