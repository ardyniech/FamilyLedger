package com.example.modules.dashboard.primitives

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens
import kotlinx.coroutines.delay

@Composable
fun LoveReactionBanner(
    partnerName: String,
    onSendReaction: (String) -> Unit = {}
) {
    var lastSentMessage by remember { mutableStateOf<String?>(null) }
    val reactions = listOf(
        "❤️" to "Semangat!",
        "👏" to "Hebat!",
        "☕" to "Kopi dulu",
        "🏡" to "Demi Impian",
        "🎁" to "Terima Kasih"
    )

    LaunchedEffect(lastSentMessage) {
        if (lastSentMessage != null) {
            delay(2800)
            lastSentMessage = null
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Apresiasi & Kasih Sayang 💕", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                Text("Kirim ke $partnerName", style = MaterialTheme.typography.bodySmall, color = DesignTokens.AmberAccent, fontWeight = FontWeight.SemiBold)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                reactions.forEach { (emoji, label) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DesignTokens.SurfaceGlass)
                            .clickable {
                                lastSentMessage = "Terkirim $emoji untuk $partnerName: \"$label\""
                                onSendReaction(emoji)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = emoji, fontSize = 20.sp)
                            Text(text = label, style = MaterialTheme.typography.bodySmall, color = DesignTokens.TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = lastSentMessage != null, enter = fadeIn(tween(200)), exit = fadeOut(tween(200))) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DesignTokens.EmeraldGlow.copy(alpha = 0.15f)).padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = lastSentMessage ?: "", style = MaterialTheme.typography.bodySmall, color = DesignTokens.EmeraldGlow, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

