package com.example.modules.dashboard.primitives

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.RoleTemplateCategoryData
import com.example.shared.models.HouseholdRole
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette

@Composable
fun RolePersonalizationPreviewCard(
    previewRole: HouseholdRole,
    isCurrentActive: Boolean
) {
    val tempPalette = RoleThemePalette.forRole(previewRole)
    val animatedAccent by animateColorAsState(targetValue = tempPalette.primaryAccent, label = "accent")
    val highlights = RoleTemplateCategoryData.getSampleHighlights(previewRole)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = tempPalette.primaryContainer),
        border = BorderStroke(2.dp, animatedAccent)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(animatedAccent), contentAlignment = Alignment.Center) {
                        Text(previewRole.emoji, fontSize = 24.sp)
                    }
                    Column {
                        Text(previewRole.title, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = DesignTokens.TextPrimary)
                        Text(previewRole.themeDescription, fontSize = 11.sp, color = animatedAccent, fontWeight = FontWeight.Bold)
                    }
                }
                if (isCurrentActive) {
                    Surface(shape = RoundedCornerShape(12.dp), color = DesignTokens.EmeraldGlow.copy(alpha = 0.2f)) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = DesignTokens.EmeraldGlow, modifier = Modifier.size(12.dp))
                            Text("Peran Aktif", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldGlow)
                        }
                    }
                }
            }

            Surface(shape = RoundedCornerShape(12.dp), color = DesignTokens.SurfaceElevated, border = BorderStroke(1.dp, tempPalette.cardBorder)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("📋 Template Pos Pengeluaran Bawaan:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    highlights.forEach { cat ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(animatedAccent))
                            Text(cat, fontSize = 11.sp, color = DesignTokens.TextSecondary)
                        }
                    }
                }
            }

            Surface(shape = RoundedCornerShape(10.dp), color = animatedAccent.copy(alpha = 0.12f)) {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("💡", fontSize = 12.sp)
                    Text(
                        previewRole.tipAdvice,
                        fontSize = 10.sp,
                        color = DesignTokens.TextPrimary
                    )
                }
            }
        }
    }
}
