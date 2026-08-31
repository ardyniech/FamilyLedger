package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

private val GOAL_PRESETS = listOf(
    Triple("Rumah", "🏡", "#3B82F6"),
    Triple("Darurat", "🛡️", "#10B981"),
    Triple("Liburan", "✈️", "#F59E0B"),
    Triple("Pendidikan", "🎓", "#8B5CF6"),
    Triple("Kendaraan", "🚗", "#EC4899"),
    Triple("Menikah", "💍", "#F43F5E"),
    Triple("Investasi", "📈", "#06B6D4")
)

@Composable
fun AddGoalDialog(
    onConfirm: (title: String, targetAmount: Long, initialAmount: Long, category: String, iconEmoji: String, deadline: String, targetTimestamp: Long, colorHex: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var initialAmount by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var selectedPreset by remember { mutableStateOf(GOAL_PRESETS[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Target Impian", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GOAL_PRESETS.forEach { preset ->
                        val isSelected = selectedPreset.first == preset.first
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DesignTokens.CobaltAccent else DesignTokens.Surface)
                                .clickable { selectedPreset = preset }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("${preset.second} ${preset.first}", color = if (isSelected) Color.White else DesignTokens.TextPrimary, fontSize = 12.sp)
                        }
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Target (misal: Rumah Idaman)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it.filter { c -> c.isDigit() } },
                    label = { Text("Target Dana (Rp)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = initialAmount,
                    onValueChange = { initialAmount = it.filter { c -> c.isDigit() } },
                    label = { Text("Dana Terkumpul Awal (Rp)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Tenggat / Deadline (misal: 31 Des 2026)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val target = targetAmount.toLongOrNull() ?: 10000000L
                        val initial = initialAmount.toLongOrNull() ?: 0L
                        onConfirm(title, target, initial, selectedPreset.first, selectedPreset.second, deadline, 0L, selectedPreset.third)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
            ) {
                Text("Simpan Target")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) }
        }
    )
}
