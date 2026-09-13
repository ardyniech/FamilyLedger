package com.example.modules.dashboard.logic

data class ObligationItem(
    val id: String,
    val title: String,
    val amount: Long,
    val dueTimestamp: Long,
    val dueDayOfMonth: Int,
    val isLoan: Boolean,
    val categoryOrType: String,
    val walletId: String? = null
)

data class BillCluster(
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val startDay: Int,
    val endDay: Int,
    val totalAmount: Long,
    val items: List<ObligationItem>,
    val daysUntilStart: Int
)
