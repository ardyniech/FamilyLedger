package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.storage.BalanceBreakdown
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WhyThisNumberDialog(
    breakdown: BalanceBreakdown,
    trustState: String,
    hashChainValid: Boolean,
    onDismiss: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Mengapa Angka Ini?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val badgeColor = when (trustState) {
                        "SYNCED" -> MaterialTheme.colorScheme.primary
                        "PENDING" -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    }
                    Badge(containerColor = badgeColor) {
                        Text(if (trustState == "SYNCED") "🟢 All Devices Synchronized" else "🟡 Pending Sync", modifier = Modifier.padding(4.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (hashChainValid) {
                        Badge(containerColor = MaterialTheme.colorScheme.secondary) {
                            Text("🔒 Audit Chain Verified", modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BreakdownRow("Opening Balance", breakdown.openingBalance, currencyFormat)
                BreakdownRow("Income (Pemasukan)", breakdown.income, currencyFormat, isPositive = true)
                BreakdownRow("Expenses (Pengeluaran)", breakdown.expenses, currencyFormat, isNegative = true)
                BreakdownRow("Internal Transfers", breakdown.internalTransfers, currencyFormat)
                BreakdownRow("Adjustments / Reversals", breakdown.adjustments, currencyFormat)
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                BreakdownRow("Current Net Balance", breakdown.currentBalance, currencyFormat, isBold = true)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun BreakdownRow(label: String, amount: Double, format: NumberFormat, isPositive: Boolean = false, isNegative: Boolean = false, isBold: Boolean = false) {
    val color = when {
        isPositive -> MaterialTheme.colorScheme.primary
        isNegative -> MaterialTheme.colorScheme.error
        isBold -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val prefix = if (isPositive) "+" else if (isNegative) "-" else ""
    val textStyle = if (isBold) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = textStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("$prefix${format.format(kotlin.math.abs(amount))}", style = textStyle, color = color)
    }
}
