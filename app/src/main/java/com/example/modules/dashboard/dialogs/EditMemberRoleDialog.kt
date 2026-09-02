package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.shared.models.Member
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MemberRoleHelper

@Composable
fun EditMemberRoleDialog(
    member: Member,
    allMembers: List<Member>,
    onDismiss: () -> Unit,
    onSave: (Member) -> Unit,
    onOpenFabSettings: () -> Unit = {}
) {
    var name by remember { mutableStateOf(member.name) }
    var role by remember { mutableStateOf(member.role) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Ubah Profil & Personalisi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Pasangan/Anggota") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Pilih Preset Peran / Hubungan:", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val presets = listOf("Suami", "Istri", "Partner A", "Partner B", "Pasangan 1", "Pasangan 2", "Spouse")
                    items(presets) { preset ->
                        val isSelected = role.equals(preset, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) DesignTokens.CobaltAccent else DesignTokens.SurfaceGlass)
                                .clickable { role = preset }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(preset, fontSize = 12.sp, color = if (isSelected) Color.White else DesignTokens.TextPrimary)
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onOpenFabSettings()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("⚙️ Setting Posisi Tombol Floating (One-Hand)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(member.copy(name = name.trim(), role = role.trim(), updatedAt = System.currentTimeMillis())) },
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Simpan", color = Color.White) }
                }
            }
        }
    }
}
