package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat

@Composable
fun AddRecurringBillDialog(
    wallets: List<WalletAccount>,
    categories: List<Category>,
    formatter: NumberFormat,
    onDismiss: () -> Unit,
    onAdd: (name: String, amount: Long, dueDate: String, categoryId: String, autoPay: Boolean, targetWalletId: String?, frequency: String) -> Unit
) {
    var billName by remember { mutableStateOf("") }
    var billAmount by remember { mutableStateOf("") }
    var billDueDate by remember { mutableStateOf("10") }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }
    var autoPay by remember { mutableStateOf(true) }
    var targetWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    var frequency by remember { mutableStateOf("Monthly") }

    val presets = listOf("Sewa Rumah" to 2500000L, "Netflix" to 186000L, "Internet Wifi" to 385000L, "Listrik PLN" to 450000L)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Jadwal Pengeluaran Bulanan Rutin", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Template Cepat:", fontSize = 11.sp, color = DesignTokens.TextSecondary, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    presets.forEach { (name, amt) ->
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(DesignTokens.BorderGlass.copy(alpha = 0.15f)).clickable { billName = name; billAmount = amt.toString() }.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text(name, fontSize = 10.sp, color = DesignTokens.TextPrimary)
                        }
                    }
                }
                OutlinedTextField(value = billName, onValueChange = { billName = it }, label = { Text("Nama Pengeluaran (misal Sewa Rumah)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = billAmount, onValueChange = { billAmount = it.filter { c -> c.isDigit() } }, label = { Text("Nominal (Rp)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = billDueDate, onValueChange = { billDueDate = it }, label = { Text("Tanggal Jatuh Tempo (1-31 setiap bulan)") }, modifier = Modifier.fillMaxWidth())

                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Populate ke Buku Kas", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Otomatis catat pengeluaran saat jatuh tempo", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    }
                    Switch(checked = autoPay, onCheckedChange = { autoPay = it })
                }
                if (autoPay) {
                    Text("Pilih Rekening / Dompet Sumber", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    wallets.take(3).forEach { wallet ->
                        val isSelected = wallet.id == targetWalletId
                        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(if (isSelected) DesignTokens.CobaltAccent.copy(alpha = 0.15f) else Color.Transparent).clickable { targetWalletId = wallet.id }.padding(6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(wallet.name, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 12.sp)
                            Text(formatter.format(wallet.balance), fontSize = 11.sp, color = DesignTokens.TextSecondary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val parsed = billAmount.toLongOrNull() ?: 0L
                val formattedDueDate = if (billDueDate.all { it.isDigit() }) "Tgl $billDueDate Setiap Bulan" else billDueDate
                if (billName.isNotEmpty() && parsed > 0L) {
                    onAdd(billName, parsed, formattedDueDate, selectedCategoryId, autoPay, if (autoPay) targetWalletId else null, frequency)
                }
                onDismiss()
            }) { Text("Simpan", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) } }
    )
}
