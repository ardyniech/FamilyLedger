package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.GoalProgressCalculator
import com.example.shared.models.FinancialGoal
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GoalDetailDialog(
    goal: FinancialGoal,
    transactions: List<Transaction>,
    onDeposit: (Long) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val currencyFmt = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val progress = remember(goal, transactions) { GoalProgressCalculator.calculate(goal, transactions) }
    var depositAmount by remember { mutableStateOf("") }
    var showDepositInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(goal.iconEmoji, fontSize = 24.sp)
                    Column {
                        Text(goal.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DesignTokens.TextPrimary)
                        Text(progress.deadlineStatusText, fontSize = 12.sp, color = DesignTokens.CobaltAccent)
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LinearProgressIndicator(
                    progress = { progress.progressFraction },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = DesignTokens.EmeraldGlow,
                    trackColor = DesignTokens.BorderLight
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${progress.percentage}% Tercapai", fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldGlow, fontSize = 13.sp)
                    Text("Sisa: ${currencyFmt.format(progress.remainingAmount)}", color = DesignTokens.TextSecondary, fontSize = 12.sp)
                }
                Text("Terkumpul: ${currencyFmt.format(progress.totalAccumulated)} / ${currencyFmt.format(goal.targetAmount)}", fontSize = 13.sp, color = DesignTokens.TextPrimary, fontWeight = FontWeight.Medium)

                if (showDepositInput) {
                    OutlinedTextField(
                        value = depositAmount,
                        onValueChange = { depositAmount = it.filter { c -> c.isDigit() } },
                        label = { Text("Jumlah Setor Tabungan (Rp)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text("Riwayat Alokasi Tabungan (${progress.taggedTransactions.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DesignTokens.TextPrimary)
                if (progress.taggedTransactions.isEmpty()) {
                    Text("Belum ada transaksi terhubung ke impian ini.", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 140.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(progress.taggedTransactions) { tx ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(tx.note.ifBlank { "Alokasi Target" }, fontSize = 12.sp, color = DesignTokens.TextPrimary)
                                Text(currencyFmt.format(if (tx.amount < 0) -tx.amount else tx.amount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldGlow)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (showDepositInput) {
                Button(onClick = { depositAmount.toLongOrNull()?.let { if (it > 0) { onDeposit(it); showDepositInput = false; depositAmount = "" } } }, colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.EmeraldGlow)) {
                    Text("Simpan Setoran")
                }
            } else {
                Button(onClick = { showDepositInput = true }, colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)) {
                    Text("+ Setor Tabungan")
                }
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Hapus Target", tint = Color(0xFFFF5252)) }
                TextButton(onClick = onDismiss) { Text("Tutup", color = DesignTokens.TextSecondary) }
            }
        }
    )
}
