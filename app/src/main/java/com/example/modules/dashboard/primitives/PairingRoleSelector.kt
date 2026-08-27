package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Member
import com.example.shared.theme.DesignTokens

@Composable
fun PairingRoleSelector(
    members: List<Member>,
    activeMemberId: String,
    onSelectActiveMember: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Perangkat ini digunakan oleh:", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            members.forEach { member ->
                val isSelected = member.id == activeMemberId
                val isHusband = member.role == "Husband"
                val accentColor = if (isHusband) DesignTokens.CobaltAccent else DesignTokens.AmberAccent

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelectActiveMember(member.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) accentColor.copy(alpha = 0.15f) else DesignTokens.Surface
                    ),
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) accentColor else DesignTokens.BorderGlass
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = if (isHusband) "👨" else "👩", fontSize = 32.sp)
                        Text(text = member.name, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) accentColor else DesignTokens.BorderLight
                        ) {
                            Text(
                                text = if (isSelected) "Sedang Aktif ✓" else member.role,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else DesignTokens.TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
