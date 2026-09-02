package com.example.modules.dashboard.primitives

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.sync.SyncState
import com.example.shared.atoms.springClickable
import com.example.shared.models.Member
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MemberRoleHelper

@Composable
fun SmartHeaderWithQuickNav(
    activeMember: Member?,
    syncState: SyncState,
    onOpenQuickNav: () -> Unit,
    onSyncBadgeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onOpenPersonalize: () -> Unit,
    onOpenAppReference: () -> Unit
) {
    val roleColor = MemberRoleHelper.getRoleColor(activeMember)
    val roleEmoji = MemberRoleHelper.getRoleEmoji(activeMember?.role ?: "")
    var isTitlePressed by remember { mutableStateOf(false) }
    val titleScale by animateFloatAsState(
        targetValue = if (isTitlePressed) 0.94f else 1.0f,
        animationSpec = spring(stiffness = 300f, dampingRatio = 0.5f),
        label = "title_scale"
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.scale(titleScale).springClickable {
                    isTitlePressed = !isTitlePressed
                    onOpenQuickNav()
                }
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DesignTokens.CobaltAccent.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, DesignTokens.CobaltAccent.copy(alpha = 0.3f))
                ) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Side Nav", tint = DesignTokens.CobaltAccent, modifier = Modifier.padding(6.dp).size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("FINFAMILY PRO", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = DesignTokens.TextPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("⚡", fontSize = 14.sp)
                    }
                    Text("Tap untuk navigasi cepat ➔", fontSize = 11.sp, color = DesignTokens.CobaltAccent, fontWeight = FontWeight.Medium)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DesignTokens.SurfaceGlass,
                    border = BorderStroke(1.dp, DesignTokens.BorderGlass),
                    modifier = Modifier.springClickable { onSyncBadgeClick() }
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(if (syncState == SyncState.SYNCING) DesignTokens.AmberAccent else if (syncState == SyncState.ERROR) Color.Red else DesignTokens.EmeraldGlow))
                        Text(text = if (syncState == SyncState.SYNCING) "Sync" else if (syncState == SyncState.ERROR) "Off" else "Online", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = roleColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, roleColor),
                    modifier = Modifier.springClickable { onProfileClick() }
                ) {
                    Text(text = "$roleEmoji ${activeMember?.name ?: "User"}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }

        SmartHeaderActionSubBar(onOpenPersonalize = onOpenPersonalize, onOpenAppReference = onOpenAppReference)
    }
}
