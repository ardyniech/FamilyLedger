package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionDetailDialog(
    transaction: Transaction,
    wallet: WalletAccount?,
    category: Category?,
    member: Member?,
    financialGoal: com.example.shared.models.FinancialGoal? = null,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val currencyFmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val dateFmt = SimpleDateFormat("EEEE, dd MMMM yyyy • HH:mm", Locale("id", "ID"))
    val isIncome = transaction.amount > 0
    val amountColor = if (isIncome) DesignTokens.EmeraldGlow else Color(0xFFFF5252)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Detail Transaksi", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 16.sp)
                Box(
                    modifier = Modifier.background(amountColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(if (isIncome) "Pemasukan" else "Pengeluaran", color = amountColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "${if (isIncome) "+" else ""}${currencyFmt.format(transaction.amount)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = amountColor
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
                    border = BorderStroke(1.dp, DesignTokens.BorderGlass)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailItemRow(label = "Catatan", value = transaction.note.ifBlank { "-" })
                        DetailItemRow(label = "Kategori", value = category?.name ?: "-")
                        financialGoal?.let { DetailItemRow(label = "Target Impian", value = "${it.iconEmoji} ${it.title}") }
                        DetailItemRow(label = "Dompet / Akun", value = wallet?.name ?: "-")
                        DetailItemRow(label = "Dicatat Oleh", value = member?.name ?: "Suami")
                        DetailItemRow(label = "Waktu", value = dateFmt.format(Date(transaction.timestamp)))
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onDeleteClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus", fontSize = 12.sp)
                }

                Button(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Tutup", color = DesignTokens.TextSecondary) }
        }
    )
}

@Composable
private fun DetailItemRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = DesignTokens.TextSecondary, fontSize = 12.sp)
        Text(value, color = DesignTokens.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
