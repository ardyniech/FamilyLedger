package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun TransferConfirmedNotificationDialog(
    notification: TransferNotification,
    onDismiss: () -> Unit
) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(DesignTokens.CornerRadius),
            color = DesignTokens.Surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(DesignTokens.PaddingMedium)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(DesignTokens.CobaltAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(notification.selectedEmoji, fontSize = 32.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Transfer Dikonfirmasi Istri! ${notification.selectedEmoji}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = DesignTokens.TextPrimary
                    )
                    Text(
                        text = "${notification.recipientName} telah menerima transfer dana darimu",
                        fontSize = 12.sp,
                        color = DesignTokens.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = fmt.format(notification.amount),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DesignTokens.CobaltAccent
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Reaction Istri:", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                            Text(notification.selectedEmoji, fontSize = 20.sp)
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Tutup & Sama-Sama! 😊",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
