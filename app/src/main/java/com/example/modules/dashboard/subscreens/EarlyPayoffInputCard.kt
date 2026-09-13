package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.PayoffStrategy
import com.example.shared.theme.DesignTokens

@Composable
fun EarlyPayoffInputCard(
    state: EarlyPayoffSimulatorState,
    onStateChange: (EarlyPayoffSimulatorState) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Parameter Pinjaman & Ekstra Bayar", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.principalInput,
                    onValueChange = { onStateChange(state.copy(principalInput = it)) },
                    label = { Text("Sisa Pokok (Rp)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1.2f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.annualRateInput,
                    onValueChange = { onStateChange(state.copy(annualRateInput = it)) },
                    label = { Text("Bunga (%/thn)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(0.8f),
                    singleLine = true
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.remainingTenorInput,
                    onValueChange = { onStateChange(state.copy(remainingTenorInput = it)) },
                    label = { Text("Sisa Tenor (bln)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.monthlyInstallmentInput,
                    onValueChange = { onStateChange(state.copy(monthlyInstallmentInput = it)) },
                    label = { Text("Cicilan Saat Ini", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Divider(color = DesignTokens.BorderGlass, thickness = 1.dp)

            Text("Opsi Pembayaran Ekstra", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.extraMonthlyInput,
                    onValueChange = { onStateChange(state.copy(extraMonthlyInput = it)) },
                    label = { Text("Ekstra / Bulan (Rp)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.lumpSumInput,
                    onValueChange = { onStateChange(state.copy(lumpSumInput = it)) },
                    label = { Text("Lump Sum 1x (Rp)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Text("Strategi Pelunasan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextSecondary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PayoffStrategy.entries.forEach { strat ->
                    val isSelected = state.strategy == strat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onStateChange(state.copy(strategy = strat)) },
                        label = { Text(strat.label, fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
