package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.DebtRecord
import com.example.shared.models.LoanType
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtDialog(onDismiss: () -> Unit, onSubmit: (DebtRecord) -> Unit) {
    var selectedType by remember { mutableStateOf(LoanType.KPR_MORTGAGE) }
    var name by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var installmentText by remember { mutableStateOf("") }
    var dueDayText by remember { mutableStateOf("7") }
    var remainingTenorText by remember { mutableStateOf("") }
    var totalTenorText by remember { mutableStateOf("") }
    var isHutang by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Kredit / KPR / Hutang", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ScrollableTabRow(selectedTabIndex = selectedType.ordinal, edgePadding = 0.dp) {
                    LoanType.entries.forEach { type ->
                        Tab(selected = selectedType == type, onClick = { selectedType = type }, text = { Text(type.label, fontSize = 11.sp) })
                    }
                }
                if (selectedType != LoanType.PERSONAL_DEBT) {
                    OutlinedTextField(value = institution, onValueChange = { institution = it }, label = { Text("Nama Bank / Leasing (misal BTN/BCA)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Keterangan Pinjaman (misal KPR Rumah C3)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = installmentText, onValueChange = { installmentText = it.filter { c -> c.isDigit() } }, label = { Text("Cicilan Per Bulan (Rp)") }, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = dueDayText, onValueChange = { dueDayText = it.filter { c -> c.isDigit() }.take(2) }, label = { Text("Tgl Jatuh Tempo (1-31)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = remainingTenorText, onValueChange = { remainingTenorText = it.filter { c -> c.isDigit() } }, label = { Text("Sisa Tenor (Bln)") }, modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = amountText, onValueChange = { amountText = it.filter { c -> c.isDigit() } }, label = { Text("Total Pokok Pinjaman (Rp)") }, modifier = Modifier.fillMaxWidth())
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = isHutang, onClick = { isHutang = true }, label = { Text("Hutang (Saya Utang)") })
                        FilterChip(selected = !isHutang, onClick = { isHutang = false }, label = { Text("Piutang (Dia Utang)") })
                    }
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Orang / Rekan") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = amountText, onValueChange = { amountText = it.filter { c -> c.isDigit() } }, label = { Text("Jumlah (Rp)") }, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toLongOrNull() ?: 0L
                    val installment = installmentText.toLongOrNull() ?: 0L
                    val dueDay = dueDayText.toIntOrNull() ?: 1
                    val finalAmt = if (amt > 0L) amt else installment * (remainingTenorText.toIntOrNull() ?: 12)
                    if (name.isNotBlank() && (finalAmt > 0L || installment > 0L)) {
                        onSubmit(
                            DebtRecord(
                                personName = name,
                                isHutang = if (selectedType != LoanType.PERSONAL_DEBT) true else isHutang,
                                amount = finalAmt,
                                dueDate = System.currentTimeMillis() + 30 * 86400000L,
                                loanType = selectedType,
                                monthlyInstallment = installment,
                                dueDayOfMonth = dueDay,
                                tenorRemainingMonths = remainingTenorText.toIntOrNull() ?: 0,
                                totalTenorMonths = totalTenorText.toIntOrNull() ?: (remainingTenorText.toIntOrNull() ?: 0),
                                institutionName = institution
                            )
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
            ) { Text("Simpan", color = DesignTokens.TextPrimary) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) } }
    )
}
