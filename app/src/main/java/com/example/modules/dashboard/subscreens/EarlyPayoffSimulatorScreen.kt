package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.EarlyPayoffCalculator
import com.example.shared.models.DebtRecord
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarlyPayoffSimulatorScreen(
    debts: List<DebtRecord> = emptyList(),
    initialDebtId: String? = null,
    onBack: () -> Unit
) {
    val initialDebt = remember(debts, initialDebtId) { debts.find { it.id == initialDebtId } ?: debts.firstOrNull { it.isBankLoanOrInstallment } }
    var state by remember {
        mutableStateOf(
            if (initialDebt != null) {
                EarlyPayoffSimulatorState(
                    selectedDebt = initialDebt,
                    principalInput = initialDebt.remainingAmount.toString(),
                    annualRateInput = if (initialDebt.annualInterestRate > 0) initialDebt.annualInterestRate.toString() else "8.5",
                    remainingTenorInput = if (initialDebt.tenorRemainingMonths > 0) initialDebt.tenorRemainingMonths.toString() else "120",
                    monthlyInstallmentInput = initialDebt.monthlyInstallment.toString(),
                    extraMonthlyInput = "1000000"
                )
            } else {
                EarlyPayoffSimulatorState()
            }
        )
    }

    val calculationResult = remember(state) {
        EarlyPayoffCalculator.calculate(state.toInput())
    }
    var showScheduleDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simulasi Pelunasan Dipercepat", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DesignTokens.TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DesignTokens.BackgroundBottom)
            )
        },
        containerColor = DesignTokens.BackgroundBottom
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            val loanDebts = debts.filter { it.isBankLoanOrInstallment }
            if (loanDebts.isNotEmpty()) {
                item {
                    Text("Pilih Pinjaman yang Ingin Disimulasikan:", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(loanDebts) { d ->
                            val isSelected = state.selectedDebt?.id == d.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    state = state.copy(
                                        selectedDebt = d,
                                        principalInput = d.remainingAmount.toString(),
                                        annualRateInput = if (d.annualInterestRate > 0) d.annualInterestRate.toString() else "8.5",
                                        remainingTenorInput = if (d.tenorRemainingMonths > 0) d.tenorRemainingMonths.toString() else "120",
                                        monthlyInstallmentInput = d.monthlyInstallment.toString()
                                    )
                                },
                                label = { Text(d.personName, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            item {
                EarlyPayoffInputCard(state = state, onStateChange = { state = it })
            }

            item {
                EarlyPayoffResultCard(
                    result = calculationResult,
                    strategy = state.strategy,
                    onViewSchedule = { showScheduleDialog = true }
                )
            }
        }
    }

    if (showScheduleDialog) {
        val input = state.toInput()
        val schedule = remember(input, calculationResult) {
            val p0 = (input.principalRemaining - input.lumpSumExtraPayment).coerceAtLeast(0L)
            com.example.modules.dashboard.logic.AmortizationScheduleHelper.generateSchedule(
                principal = p0,
                annualRate = input.annualInterestRate,
                monthlyPayment = calculationResult.newMonthlyPayment
            )
        }
        com.example.modules.dashboard.dialogs.AmortizationScheduleDialog(
            schedule = schedule,
            onDismiss = { showScheduleDialog = false }
        )
    }
}
