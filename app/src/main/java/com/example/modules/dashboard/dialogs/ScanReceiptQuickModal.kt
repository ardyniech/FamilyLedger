package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.modules.dashboard.logic.ParsedReceiptResult
import com.example.modules.dashboard.logic.SmartReceiptParserEngine
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette
import com.example.shared.utils.MathUtils

@Composable
fun ScanReceiptQuickModal(
    onDismiss: () -> Unit,
    onConfirmRecord: (amount: Long, note: String, categoryKeyword: String, walletType: String) -> Unit
) {
    val palette = RoleThemePalette.istri()
    var receiptRawText by remember { mutableStateOf("") }
    var parsedResult by remember { mutableStateOf<ParsedReceiptResult?>(null) }
    var editableAmount by remember { mutableStateOf("") }
    var editableNote by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated),
            border = BorderStroke(1.dp, palette.cardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = palette.primarySoft
                        ) {
                            Icon(
                                Icons.Default.DocumentScanner,
                                contentDescription = null,
                                tint = palette.primaryAccent,
                                modifier = Modifier.padding(6.dp).size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                "🧾 Smart Struk Dapur OCR",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DesignTokens.TextPrimary
                            )
                            Text(
                                "Ekstraksi belanja pasar & minimarket instan",
                                fontSize = 11.sp,
                                color = DesignTokens.TextSecondary
                            )
                        }
                    }
                }

                if (parsedResult == null) {
                    Text(
                        "Tempel teks hasil foto struk (Indomaret, Alfamart, Superindo, atau Pasar):",
                        fontSize = 12.sp,
                        color = DesignTokens.TextSecondary
                    )

                    OutlinedTextField(
                        value = receiptRawText,
                        onValueChange = { receiptRawText = it },
                        placeholder = { Text("Contoh:\nINDOMARET\nMINYAK GORENG 2L  Rp 34.000\nBERAS 5KG  Rp 72.500\nTOTAL = 106.500", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = palette.primaryAccent)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Tutup", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                        }

                        Button(
                            onClick = {
                                val result = SmartReceiptParserEngine.parseReceiptText(receiptRawText)
                                parsedResult = result
                                editableAmount = if (result.totalAmount > 0) result.totalAmount.toString() else ""
                                editableNote = "${result.merchantName}: ${result.itemsSummary}"
                            },
                            enabled = receiptRawText.isNotBlank(),
                            modifier = Modifier.weight(1.3f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent)
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ekstrak", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        }
                    }
                } else {
                    val res = parsedResult!!
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = palette.backgroundTint,
                        border = BorderStroke(1.dp, palette.primarySoft),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Toko: ${res.merchantName} • Pos: ${res.detectedCategoryKeyword.uppercase()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.primaryAccent)
                            Text("Ringkasan: ${res.itemsSummary}", fontSize = 11.sp, color = DesignTokens.TextPrimary)
                            Text("Saran Dompet: ${res.suggestedWalletType}", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                        }
                    }

                    OutlinedTextField(
                        value = editableAmount,
                        onValueChange = { input -> editableAmount = input.filter { it.isDigit() } },
                        label = { Text("Total Belanja (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = editableNote,
                        onValueChange = { editableNote = it },
                        label = { Text("Catatan Belanja") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { parsedResult = null },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Ulangi", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val amount = editableAmount.toLongOrNull() ?: res.totalAmount
                                if (amount > 0) {
                                    onConfirmRecord(amount, editableNote.ifBlank { "Belanja Struk" }, res.detectedCategoryKeyword, res.suggestedWalletType)
                                }
                            },
                            modifier = Modifier.weight(1.4f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent)
                        ) {
                            Text("Simpan Belanja", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
