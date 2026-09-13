package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.example.modules.dashboard.logic.WifeExpensePresetItem
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette
import com.example.shared.utils.MathUtils

@Composable
fun EditPresetAmountDialog(
    preset: WifeExpensePresetItem,
    onDismiss: () -> Unit,
    onSaveAndRecord: (WifeExpensePresetItem, Long, String) -> Unit
) {
    var amountText by remember { mutableStateOf(preset.defaultAmount.toString()) }
    var noteText by remember { mutableStateOf(preset.defaultNote) }
    val palette = RoleThemePalette.istri()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated),
            border = BorderStroke(1.dp, palette.cardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        Text(text = preset.emoji, fontSize = 24.sp)
                        Column {
                            Text(
                                text = preset.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DesignTokens.TextPrimary
                            )
                            Text(
                                text = "Sesuaikan nominal sebelum dicatat",
                                fontSize = 11.sp,
                                color = DesignTokens.TextSecondary
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input -> amountText = input.filter { it.isDigit() } },
                    label = { Text("Nominal Belanja (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = palette.primaryAccent,
                        focusedLabelColor = palette.primaryAccent
                    )
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Catatan / Keterangan Belanja") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = palette.primaryAccent,
                        focusedLabelColor = palette.primaryAccent
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Batal", color = DesignTokens.TextSecondary, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val parsedAmount = amountText.toLongOrNull() ?: preset.defaultAmount
                            if (parsedAmount > 0) {
                                onSaveAndRecord(preset, parsedAmount, noteText.ifBlank { preset.defaultNote })
                            }
                        },
                        modifier = Modifier.weight(1.3f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent)
                    ) {
                        Text("Catat Sekarang", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
