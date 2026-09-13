package com.example.modules.dashboard.dialogs

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.modules.dashboard.logic.OnboardingMaturityLevel
import com.example.shared.theme.DesignTokens
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun LevelUpCelebrationDialog(
    level: OnboardingMaturityLevel,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration_particles")
    val particleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_anim"
    )

    val particles = remember {
        List(24) {
            val angle = Random.nextDouble(0.0, Math.PI * 2)
            val distance = Random.nextFloat() * 140f + 40f
            val color = listOf(
                DesignTokens.AmberAccent,
                DesignTokens.EmeraldGlow,
                DesignTokens.CobaltAccent,
                Color(0xFFFF6B6B),
                Color(0xFF4ECDC4)
            ).random()
            val size = Random.nextFloat() * 6f + 4f
            Triple(angle, distance, Pair(color, size))
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val center = Offset(size.width / 2, size.height * 0.28f)
                    particles.forEach { (angle, maxDist, style) ->
                        val currentDist = maxDist * particleProgress
                        val alpha = (1f - particleProgress).coerceIn(0f, 1f)
                        val x = center.x + (cos(angle) * currentDist).toFloat()
                        val y = center.y + (sin(angle) * currentDist).toFloat()
                        drawCircle(color = style.first.copy(alpha = alpha), radius = style.second, center = Offset(x, y))
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(DesignTokens.AmberAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Celebration, contentDescription = "Celebration", tint = DesignTokens.AmberAccent, modifier = Modifier.size(36.dp))
                    }

                    Text("Pencapaian Keluarga!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)

                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(DesignTokens.CobaltAccent.copy(alpha = 0.15f)).padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Level ${level.levelNumber}: ${level.title}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent)
                    }

                    Text(level.description, fontSize = 12.sp, color = DesignTokens.TextSecondary, modifier = Modifier.padding(horizontal = 8.dp), lineHeight = 18.sp)

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.AmberAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Lanjutkan Perjalanan Finansial 🚀", color = DesignTokens.BackgroundBottom, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
