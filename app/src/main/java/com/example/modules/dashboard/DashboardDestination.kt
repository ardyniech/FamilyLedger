package com.example.modules.dashboard

sealed class DashboardDestination {
    object Dashboard : DashboardDestination()
    object NetWorthDetail : DashboardDestination()
    data class WalletDetail(val walletId: String) : DashboardDestination()
    object Analytics : DashboardDestination()
    object MonthlyReport : DashboardDestination()
    object RecurringBills : DashboardDestination()
    object ExpenseList : DashboardDestination()
    object MonthlyTransactionHistory : DashboardDestination()
    object CategoryManagement : DashboardDestination()
    object WalletManagement : DashboardDestination()
    object Transfer : DashboardDestination()
    object Pairing : DashboardDestination()
    object GoalsAndBudget : DashboardDestination()
    object CategoryGroupDashboard : DashboardDestination()
    object SmartCsvImport : DashboardDestination()
}
