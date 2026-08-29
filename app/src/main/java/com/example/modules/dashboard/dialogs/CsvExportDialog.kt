package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.shared.models.*
import com.example.shared.theme.DesignTokens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CsvExportDialog(
    transactions: List<Transaction>,
    wallets: List<WalletAccount>,
    categories: List<Category>,
    members: List<Member>,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    val csvText = remember(transactions, wallets, categories, members) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val sb = java.lang.StringBuilder()
        sb.append("Tanggal,Anggota,Kategori,Tipe,Nominal,Catatan,Rekening\n")
        transactions.sortedByDescending { it.timestamp }.forEach { tx ->
            val dateStr = sdf.format(Date(tx.timestamp))
            val memberName = members.find { it.id == tx.memberId }?.name ?: "Unknown"
            val category = categories.find { it.id == tx.categoryId }
            val categoryName = category?.name ?: "Umum"
            val type = category?.type ?: if (tx.amount < 0) "Expense" else "Income"
            val walletName = wallets.find { it.id == tx.walletId }?.name ?: "Kas"
            val cleanNote = tx.note.replace(",", ";").replace("\n", " ")
            sb.append("$dateStr,$memberName,$categoryName,$type,${tx.amount},$cleanNote,$walletName\n")
        }
        sb.toString()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(DesignTokens.CornerRadius),
            color = DesignTokens.Surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(DesignTokens.PaddingMedium).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("📤 Ekspor Data Transaksi (CSV)", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = DesignTokens.TextPrimary)
                Text("Total ${transactions.size} transaksi siap diekspor.", fontSize = 12.sp, color = DesignTokens.TextSecondary)

                Card(
                    colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass),
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                ) {
                    Box(modifier = Modifier.padding(10.dp).verticalScroll(rememberScrollState())) {
                        Text(text = csvText, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = DesignTokens.TextPrimary)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                        Text("Tutup", color = DesignTokens.TextSecondary, fontSize = 13.sp)
                    }
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(csvText))
                            copied = true
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.EmeraldGlow),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (copied) "✓ Tersalin!" else "📋 Salin Teks CSV", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

