package com.example.modules.dashboard.logic

import android.content.Context
import com.example.shared.models.DebtRecord
import com.example.shared.models.LoanType
import org.json.JSONArray
import org.json.JSONObject

object DebtStorage {
    private const val PREFS_NAME = "family_ledger_debts"
    private const val KEY_DEBTS = "debts_json"

    fun loadDebts(context: Context?): List<DebtRecord>? {
        if (context == null) return null
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_DEBTS, null) ?: return null
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<DebtRecord>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    DebtRecord(
                        id = obj.optString("id"),
                        personName = obj.optString("personName"),
                        isHutang = obj.optBoolean("isHutang", true),
                        amount = obj.optLong("amount"),
                        paidAmount = obj.optLong("paidAmount", 0L),
                        dueDate = obj.optLong("dueDate"),
                        note = obj.optString("note", ""),
                        isSettled = obj.optBoolean("isSettled", false),
                        loanType = LoanType.fromString(obj.optString("loanType", "PERSONAL_DEBT")),
                        monthlyInstallment = obj.optLong("monthlyInstallment", 0L),
                        dueDayOfMonth = obj.optInt("dueDayOfMonth", 0),
                        tenorRemainingMonths = obj.optInt("tenorRemainingMonths", 0),
                        totalTenorMonths = obj.optInt("totalTenorMonths", 0),
                        institutionName = obj.optString("institutionName", ""),
                        autoDebitWalletId = if (obj.has("autoDebitWalletId") && !obj.isNull("autoDebitWalletId")) obj.optString("autoDebitWalletId") else null,
                        annualInterestRate = obj.optDouble("annualInterestRate", 0.0)
                    )
                )
            }
            list
        } catch (e: Exception) {
            null
        }
    }

    fun saveDebts(context: Context?, debts: List<DebtRecord>) {
        if (context == null) return
        try {
            val array = JSONArray()
            debts.forEach { d ->
                val obj = JSONObject().apply {
                    put("id", d.id)
                    put("personName", d.personName)
                    put("isHutang", d.isHutang)
                    put("amount", d.amount)
                    put("paidAmount", d.paidAmount)
                    put("dueDate", d.dueDate)
                    put("note", d.note)
                    put("isSettled", d.isSettled)
                    put("loanType", d.loanType.name)
                    put("monthlyInstallment", d.monthlyInstallment)
                    put("dueDayOfMonth", d.dueDayOfMonth)
                    put("tenorRemainingMonths", d.tenorRemainingMonths)
                    put("totalTenorMonths", d.totalTenorMonths)
                    put("institutionName", d.institutionName)
                    put("autoDebitWalletId", d.autoDebitWalletId)
                    put("annualInterestRate", d.annualInterestRate)
                }
                array.put(obj)
            }
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
                .putString(KEY_DEBTS, array.toString()).apply()
        } catch (ignored: Exception) {}
    }
}
