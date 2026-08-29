package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.shared.models.TransferNotification
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun IncomingTransferNotificationDialog(
    notification: TransferNotification,
    onConfirm: (notificationId: String, selectedEmoji: String) -> Unit,
    onDismiss: () -> Unit
) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val emojiOptions = listOf("❤️", "😘", "🤲", "🙏", "🥰", "💸", "🎁", "💖")
    var selectedEmoji by remember { mutableStateOf("❤️") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(DesignTokens.CornerRadius), color = DesignTokens.Surface, tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
            Column(modifier = Modifier.padding(DesignTokens.PaddingMedium).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(DesignTokens.EmeraldGlow.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                    Text("💸", fontSize = 28.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Transfer Masuk dari Suami!", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = DesignTokens.TextPrimary)
                    Text("${notification.senderName} mengirimkan dana ke rekeningmu", fontSize = 12.sp, color = DesignTokens.TextSecondary, textAlign = TextAlign.Center)
                }

                Card(colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass), border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(fmt.format(notification.amount), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = DesignTokens.EmeraldGlow)
                        if (notification.note.isNotBlank()) {
                            Text("\"${notification.note}\"", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DesignTokens.TextPrimary, textAlign = TextAlign.Center)
                        }
                        Text("${notification.fromWalletName} ➔ ${notification.toWalletName}", fontSize = 11.sp, color = DesignTokens.TextMuted)
                    }
                }

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Pilih Emoji Balasan untuk Suami:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextPrimary)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        emojiOptions.take(4).forEach { emoji ->
                            val isSelected = selectedEmoji == emoji
                            Box(
                                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(if (isSelected) DesignTokens.CobaltAccent.copy(alpha = 0.2f) else DesignTokens.SurfaceGlass)
                                    .border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) DesignTokens.CobaltAccent else DesignTokens.BorderGlass, shape = RoundedCornerShape(12.dp))
                                    .clickable { selectedEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) { Text(emoji, fontSize = 20.sp) }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        emojiOptions.drop(4).take(4).forEach { emoji ->
                            val isSelected = selectedEmoji == emoji
                            Box(
                                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(if (isSelected) DesignTokens.CobaltAccent.copy(alpha = 0.2f) else DesignTokens.SurfaceGlass)
                                    .border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) DesignTokens.CobaltAccent else DesignTokens.BorderGlass, shape = RoundedCornerShape(12.dp))
                                    .clickable { selectedEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) { Text(emoji, fontSize = 20.sp) }
                        }
                    }
                }

                Button(
                    onClick = { onConfirm(notification.id, selectedEmoji); onDismiss() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.EmeraldGlow),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Konfirmasi Terima $selectedEmoji", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White) }
            }
        }
    }
}
