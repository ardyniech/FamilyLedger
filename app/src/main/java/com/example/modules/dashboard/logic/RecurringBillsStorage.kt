package com.example.modules.dashboard.logic

import android.content.Context
import com.example.shared.models.RecurringBill
import org.json.JSONArray
import org.json.JSONObject

object RecurringBillsStorage {
    private const val PREFS_NAME = "family_ledger_recurring_bills"
    private const val KEY_BILLS = "bills_json"

    fun loadBills(context: Context?): List<RecurringBill>? {
        if (context == null) return null
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_BILLS, null) ?: return null
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<RecurringBill>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    RecurringBill(
                        id = obj.optString("id"),
                        name = obj.optString("name"),
                        amount = obj.optLong("amount"),
                        dueDate = obj.optString("dueDate"),
                        categoryId = obj.optString("categoryId"),
                        isPaid = obj.optBoolean("isPaid", false),
                        autoPay = obj.optBoolean("autoPay", false),
                        targetWalletId = if (obj.has("targetWalletId") && !obj.isNull("targetWalletId")) obj.optString("targetWalletId") else null,
                        frequency = obj.optString("frequency", "Monthly"),
                        lastProcessedTime = obj.optLong("lastProcessedTime", 0L),
                        dueDayOfMonth = obj.optInt("dueDayOfMonth", 0),
                        autoPopulateInLedger = obj.optBoolean("autoPopulateInLedger", true)
                    )
                )
            }
            list
        } catch (e: Exception) {
            null
        }
    }

    fun saveBills(context: Context?, bills: List<RecurringBill>) {
        if (context == null) return
        try {
            val array = JSONArray()
            bills.forEach { b ->
                val obj = JSONObject().apply {
                    put("id", b.id)
                    put("name", b.name)
                    put("amount", b.amount)
                    put("dueDate", b.dueDate)
                    put("categoryId", b.categoryId)
                    put("isPaid", b.isPaid)
                    put("autoPay", b.autoPay)
                    put("targetWalletId", b.targetWalletId)
                    put("frequency", b.frequency)
                    put("lastProcessedTime", b.lastProcessedTime)
                    put("dueDayOfMonth", b.dueDayOfMonth)
                    put("autoPopulateInLedger", b.autoPopulateInLedger)
                }
                array.put(obj)
            }
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
                .putString(KEY_BILLS, array.toString()).apply()
        } catch (ignored: Exception) {}
    }
}
