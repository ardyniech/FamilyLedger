package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.primitives.RolePersonalizationPreviewCard
import com.example.shared.models.HouseholdRole
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolePersonalizationDialog(
    currentRole: HouseholdRole,
    onApplyRole: (HouseholdRole) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(currentRole) }
    val previewPalette = RoleThemePalette.forRole(selectedRole)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DesignTokens.BackgroundBottom,
        dragHandle = {
            Surface(modifier = Modifier.padding(top = 8.dp), color = DesignTokens.BorderLight, shape = RoundedCornerShape(4.dp)) {
                Box(modifier = Modifier.size(36.dp, 4.dp))
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Palette, contentDescription = "Role", tint = previewPalette.primaryAccent)
                    Column {
                        Text("Personalisasi Peran & Template", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                        Text("Pilih peran untuk menyesuaikan warna & template pos keuangan", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    }
                }
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Tutup", tint = DesignTokens.TextSecondary) }
            }

            // Clean Role Selector
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HouseholdRole.entries.forEach { role ->
                    val isSelected = selectedRole == role
                    val roleAccent = RoleThemePalette.forRole(role).primaryAccent
                    Card(
                        onClick = { selectedRole = role },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) roleAccent.copy(alpha = 0.15f) else DesignTokens.SurfaceElevated),
                        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) roleAccent else DesignTokens.BorderGlass)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(role.emoji, fontSize = 28.sp)
                            Text(role.code, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = if (isSelected) roleAccent else DesignTokens.TextPrimary)
                            Text(role.shortTitle, fontSize = 10.sp, color = DesignTokens.TextSecondary)
                        }
                    }
                }
            }

            // Live Preview Card
            RolePersonalizationPreviewCard(previewRole = selectedRole, isCurrentActive = selectedRole == currentRole)

            // Apply Button
            Button(
                onClick = {
                    onApplyRole(selectedRole)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = previewPalette.primaryAccent)
            ) {
                Text(
                    "Terapkan Peran ${selectedRole.code} & Tema Warna",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
