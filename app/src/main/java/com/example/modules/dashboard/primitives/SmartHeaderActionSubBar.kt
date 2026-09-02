package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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

@Composable
fun SmartHeaderActionSubBar(
    onOpenPersonalize: () -> Unit,
    onOpenAppReference: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DesignTokens.SurfaceCard,
            border = BorderStroke(1.dp, DesignTokens.BorderGlass),
            modifier = Modifier.springClickable { onOpenPersonalize() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Layout", tint = DesignTokens.CobaltAccent, modifier = Modifier.size(14.dp))
                Text("⚙️ Atur Card Dashboard", fontSize = 11.sp, color = DesignTokens.TextPrimary, fontWeight = FontWeight.SemiBold)
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DesignTokens.SurfaceCard,
            border = BorderStroke(1.dp, DesignTokens.BorderGlass),
            modifier = Modifier.springClickable { onOpenAppReference() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Reference", tint = DesignTokens.TextSecondary, modifier = Modifier.size(14.dp))
                Text("ℹ️ Referensi & Setting", fontSize = 11.sp, color = DesignTokens.TextSecondary)
            }
        }
    }
}
