package com.example.modules.dashboard.management

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.window.Dialog
import com.example.shared.models.CategoryGroup
import com.example.shared.theme.DesignTokens
import java.util.UUID

@Composable
fun AddEditCategoryGroupDialog(
    group: CategoryGroup? = null,
    onDismiss: () -> Unit,
    onSave: (CategoryGroup) -> Unit,
    onDelete: ((CategoryGroup) -> Unit)? = null
) {
    var name by remember { mutableStateOf(group?.name ?: "") }
    var icon by remember { mutableStateOf(group?.iconName ?: "📁") }
    var selectedColor by remember { mutableStateOf(group?.colorHex ?: "#3B82F6") }

    val colors = listOf("#3B82F6", "#EF4444", "#10B981", "#F59E0B", "#8B5CF6", "#EC4899", "#06B6D4")
    val icons = listOf("🏠", "📑", "❤️", "👤", "💰", "🚗", "🛒", "⚡", "🎁", "🌴")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(if (group == null) "Tambah Grup Kategori" else "Edit Grup Kategori", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Grup") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Pilih Icon:", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(icons) { ic ->
                        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(if (icon == ic) DesignTokens.CobaltAccent else DesignTokens.SurfaceGlass).clickable { icon = ic }, contentAlignment = Alignment.Center) {
                            Text(ic, fontSize = 16.sp)
                        }
                    }
                }
                Text("Pilih Warna:", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(colors) { hex ->
                        val col = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color.Blue }
                        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(col).clickable { selectedColor = hex }.padding(4.dp)) {
                            if (selectedColor == hex) Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color.White.copy(alpha = 0.4f)))
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    if (group != null && onDelete != null) {
                        TextButton(onClick = { onDelete(group) }) { Text("Hapus", color = DesignTokens.CrimsonAccent) }
                    } else Spacer(Modifier.width(1.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) }
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val newGroup = group?.copy(name = name.trim(), iconName = icon, colorHex = selectedColor, updatedAt = System.currentTimeMillis())
                                        ?: CategoryGroup(id = "cg_" + UUID.randomUUID().toString().take(8), name = name.trim(), iconName = icon, colorHex = selectedColor)
                                    onSave(newGroup)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
                        ) { Text("Simpan", color = Color.White) }
                    }
                }
            }
        }
    }
}
