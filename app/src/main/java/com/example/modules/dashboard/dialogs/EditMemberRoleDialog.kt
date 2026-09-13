package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.shared.models.HouseholdRole
import com.example.shared.models.Member
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette

@Composable
fun EditMemberRoleDialog(
    member: Member,
    allMembers: List<Member>,
    onDismiss: () -> Unit,
    onSave: (Member) -> Unit,
    onOpenFabSettings: () -> Unit = {}
) {
    var name by remember { mutableStateOf(member.name) }
    var selectedRole by remember { mutableStateOf(HouseholdRole.fromString(member.role)) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard),
            border = BorderStroke(1.dp, DesignTokens.BorderLight)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    "Ubah Profil Peran Keluarga",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DesignTokens.TextPrimary
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Panggilan") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DesignTokens.TextPrimary,
                        unfocusedTextColor = DesignTokens.TextPrimary,
                        focusedBorderColor = DesignTokens.CobaltAccent,
                        unfocusedBorderColor = DesignTokens.BorderLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Pilih Peran Rumah Tangga:", fontSize = 12.sp, color = DesignTokens.TextSecondary, fontWeight = FontWeight.SemiBold)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HouseholdRole.entries.forEach { role ->
                        val isSelected = selectedRole == role
                        val palette = RoleThemePalette.forRole(role)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) palette.primaryAccent.copy(alpha = 0.15f) else DesignTokens.SurfaceGlass)
                                .clickable { selectedRole = role }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(role.emoji, fontSize = 24.sp)
                                Text(
                                    role.code,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) palette.primaryAccent else DesignTokens.TextPrimary
                                )
                                Text(role.shortTitle, fontSize = 10.sp, color = DesignTokens.TextSecondary)
                            }
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
                    Text("⚙️ Setting Posisi Tombol Cepat (One-Hand)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss) { Text("Batal", color = DesignTokens.TextSecondary) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(member.copy(name = name.trim(), role = selectedRole.code, updatedAt = System.currentTimeMillis()))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoleThemePalette.forRole(selectedRole).primaryAccent),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Simpan", color = Color.White) }
                }
            }
        }
    }
}
