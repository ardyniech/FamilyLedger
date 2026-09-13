package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.atoms.springClickable
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleTheme

@Composable
fun SmartHeaderActionSubBar(
    onOpenRolePersonalize: () -> Unit = {},
    onOpenPersonalize: () -> Unit,
    onOpenAppReference: () -> Unit
) {
    val palette = RoleTheme.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = palette.primarySoft,
            border = BorderStroke(1.dp, palette.cardBorder),
            modifier = Modifier.weight(1f).springClickable { onOpenRolePersonalize() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(imageVector = Icons.Default.Palette, contentDescription = "Role", tint = palette.primaryAccent, modifier = Modifier.size(14.dp))
                Text("🎭 Peran & Template", fontSize = 11.sp, color = palette.primaryAccent, fontWeight = FontWeight.Bold, maxLines = 1)
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DesignTokens.SurfaceCard,
            border = BorderStroke(1.dp, DesignTokens.BorderGlass),
            modifier = Modifier.springClickable { onOpenPersonalize() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Layout", tint = DesignTokens.TextSecondary, modifier = Modifier.size(14.dp))
                Text("Card", fontSize = 11.sp, color = DesignTokens.TextPrimary, fontWeight = FontWeight.SemiBold)
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DesignTokens.SurfaceCard,
            border = BorderStroke(1.dp, DesignTokens.BorderGlass),
            modifier = Modifier.springClickable { onOpenAppReference() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Reference", tint = DesignTokens.TextSecondary, modifier = Modifier.size(14.dp))
                Text("Info", fontSize = 11.sp, color = DesignTokens.TextSecondary)
            }
        }
    }
}
