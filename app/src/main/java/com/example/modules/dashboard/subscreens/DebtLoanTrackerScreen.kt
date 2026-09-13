package com.example.modules.dashboard.subscreens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.dialogs.AddDebtDialog
import com.example.modules.dashboard.logic.*
import com.example.modules.dashboard.primitives.*
import com.example.modules.dashboard.worker.BillClusterWorkScheduler
import com.example.shared.models.*
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtLoanTrackerScreen(
    debts: List<DebtRecord>,
    report: FinancialIntegrityReport? = null,
    selectedCadence: CashflowCadence = CashflowCadence.DAILY,
    onSelectCadence: (CashflowCadence) -> Unit = {},
    onAddDebt: (DebtRecord) -> Unit,
    onPayDebt: (String, Long) -> Unit,
    onDeleteDebt: (String) -> Unit,
    onOpenEarlyPayoffSimulator: (String?) -> Unit = {},
    onOpenKprSimulator: () -> Unit = {},
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val portfolio = remember(debts) { LoanProgressCalculator.calculatePortfolio(debts) }
    val bankLoans = remember(debts) { debts.filter { it.isBankLoanOrInstallment } }
    val personalDebts = remember(debts) { debts.filter { !it.isBankLoanOrInstallment } }
    val kprLoan = bankLoans.find { it.loanType == LoanType.KPR_MORTGAGE } ?: bankLoans.firstOrNull()
    val kprAnalysis = remember(kprLoan) {
        kprLoan?.let {
            val rate = if (it.annualInterestRate > 0.0) it.annualInterestRate.toFloat() else 5.5f
            val tenor = if (it.tenorRemainingMonths > 0) it.tenorRemainingMonths else 180
            KprFloatingRateCalculator.analyze(it.personName, it.remainingAmount, rate, 11.5f, tenor, 8)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kredit Bank, KPR & Cicilan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DesignTokens.TextPrimary) } },
                actions = {
                    IconButton(onClick = { BillClusterWorkScheduler.triggerImmediateCheck(context); Toast.makeText(context, "Worker pengingat tagihan H-3 & H-1 dijalankan!", Toast.LENGTH_SHORT).show() }) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = "Test Notification", tint = DesignTokens.AmberAccent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DesignTokens.BackgroundBottom)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = DesignTokens.CobaltAccent) {
                Icon(Icons.Filled.Add, contentDescription = "Add Debt", tint = DesignTokens.TextPrimary)
            }
        },
        containerColor = DesignTokens.BackgroundBottom
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            report?.let { FinancialIntegrityCard(report = it, selectedCadence = selectedCadence, onSelectCadence = onSelectCadence, onOpenLoans = {}) }

            if (selectedTab == 0 && bankLoans.isNotEmpty()) {
                BankLoanProgressTrackerCard(portfolio = portfolio, onOpenEarlyPayoffSimulator = { onOpenEarlyPayoffSimulator(null) })
                kprAnalysis?.let { KprFloatingRateAlertCard(analysis = it, onOpenSimulator = onOpenKprSimulator) }
            } else if (selectedTab == 1) {
                DebtTrackerSummaryCard(monthlyInstallmentSum = 0L, totalRemainingLoan = personalDebts.sumOf { it.remainingAmount }, onOpenEarlyPayoffSimulator = { onOpenEarlyPayoffSimulator(null) })
            }

            TabRow(selectedTabIndex = selectedTab, containerColor = DesignTokens.SurfaceElevated) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Kredit & KPR (${bankLoans.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Personal (${personalDebts.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) })
            }

            val currentList = if (selectedTab == 0) bankLoans else personalDebts
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                items(currentList, key = { it.id }) { debt ->
                    DebtItemCard(debt = debt, onPay = { amt -> onPayDebt(debt.id, amt) }, onDelete = { onDeleteDebt(debt.id) }, onSimulatePayoff = { debtId -> onOpenEarlyPayoffSimulator(debtId) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddDebtDialog(onDismiss = { showAddDialog = false }, onSubmit = { newDebt -> onAddDebt(newDebt); showAddDialog = false })
    }
}
