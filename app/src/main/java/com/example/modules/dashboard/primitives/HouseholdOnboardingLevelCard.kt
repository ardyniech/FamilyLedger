package com.example.modules.dashboard.primitives

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.dialogs.LevelUpCelebrationDialog
import com.example.modules.dashboard.logic.HouseholdMaturityStatus
import com.example.shared.theme.DesignTokens

@Composable
fun HouseholdOnboardingLevelCard(
    status: HouseholdMaturityStatus,
    onQuickAddTransaction: () -> Unit,
    onAddWallet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCelebrationDialog by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = status.progressToNextLevel,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "onboarding_progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Stars, contentDescription = "Level", tint = DesignTokens.AmberAccent, modifier = Modifier.size(18.dp))
                    Text(status.level.title, color = DesignTokens.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(DesignTokens.AmberAccent.copy(alpha = 0.15f)).clickable { showCelebrationDialog = true }.padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text(status.level.unlockedBadge, color = DesignTokens.AmberAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(status.level.description, color = DesignTokens.TextSecondary, fontSize = 11.sp)

            Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(DesignTokens.BorderLight)) {
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(animatedProgress).clip(RoundedCornerShape(3.dp)).background(Brush.horizontalGradient(listOf(DesignTokens.AmberAccent, DesignTokens.EmeraldGlow))))
            }

            Text(status.nextLevelRequirementText, color = DesignTokens.TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)

            if (status.shouldShowGuidedPrompt) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onQuickAddTransaction,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = DesignTokens.TextPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Catat Transaksi", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    }

                    if (status.walletCount == 0) {
                        OutlinedButton(
                            onClick = onAddWallet,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("Tambah Dompet", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                        }
                    }
                }
            }
        }
    }

    if (showCelebrationDialog) {
        LevelUpCelebrationDialog(level = status.level, onDismiss = { showCelebrationDialog = false })
    }
}
