package com.example.modules.dashboard.management

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.theme.DesignTokens

@Composable
fun CategoryFormDialog(
    editCategory: Category?,
    defaultType: String,
    onDismiss: () -> Unit,
    onSave: (id: String?, name: String, type: String, budgetLimit: Double) -> Unit
) {
    var name by remember { mutableStateOf(editCategory?.name ?: "") }
    var type by remember { mutableStateOf(editCategory?.type ?: defaultType) }
    var budgetLimitStr by remember { mutableStateOf(if ((editCategory?.budgetLimit ?: 0.0) > 0) editCategory!!.budgetLimit.toInt().toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss, shape = RoundedCornerShape(DesignTokens.CornerRadius), containerColor = DesignTokens.Surface,
        title = { Text(if (editCategory != null) "Ubah Kategori" else "Tambah Kategori Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DesignTokens.TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Kategori", fontSize = 12.sp) }, placeholder = { Text("misal: Transportasi") }, singleLine = true, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().testTag("category_name_field"))
                if (editCategory == null) {
                    Text("Tipe Kategori:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        listOf("Expense", "Income").forEach { t ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { type = t }) {
                                RadioButton(selected = type == t, onClick = { type = t }, colors = RadioButtonDefaults.colors(selectedColor = DesignTokens.CobaltAccent))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (t == "Expense") "Pengeluaran" else "Pemasukan", fontSize = 13.sp, color = DesignTokens.TextPrimary)
                            }
                        }
                    }
                }
                if (type == "Expense") {
                    OutlinedTextField(value = budgetLimitStr, onValueChange = { budgetLimitStr = it.filter { c -> c.isDigit() } }, label = { Text("Batas Anggaran Bulanan (Opsional)", fontSize = 12.sp) }, placeholder = { Text("misal: 1500000") }, singleLine = true, shape = RoundedCornerShape(8.dp), prefix = { Text("Rp ", fontSize = 13.sp, color = DesignTokens.TextSecondary) }, modifier = Modifier.fillMaxWidth().testTag("category_budget_limit_field"))
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onSave(editCategory?.id, name, type, budgetLimitStr.toDoubleOrNull() ?: 0.0) }, colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent), modifier = Modifier.testTag("save_category_button")) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) } }
    )
}

@Composable
fun CategoryDeleteDialog(category: Category, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss, shape = RoundedCornerShape(DesignTokens.CornerRadius), containerColor = DesignTokens.Surface,
        title = { Text("Hapus Kategori?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DesignTokens.RoseAccent) },
        text = { Text("Apakah Anda yakin ingin menghapus kategori '${category.name}'? Langkah ini akan menyembunyikan kategori dari daftar transaksi baru.", fontSize = 13.sp, color = DesignTokens.TextPrimary) },
        confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.RoseAccent), modifier = Modifier.testTag("confirm_delete_category_btn")) { Text("Hapus") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) } }
    )
}
