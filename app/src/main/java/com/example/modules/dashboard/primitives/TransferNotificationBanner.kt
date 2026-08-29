package com.example.modules.dashboard.primitives

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.atoms.springClickable
import com.example.shared.models.Member
import com.example.shared.models.TransferNotification
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransferNotificationBanner(
    notification: TransferNotification?,
    activeMember: Member?,
    onClickBanner: (TransferNotification) -> Unit,
    onDismiss: () -> Unit
) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val isPendingForActiveUser = notification != null && 
        notification.status == "PENDING_CONFIRMATION" && 
        (activeMember?.role == notification.recipientRole || activeMember?.id == notification.recipientId)

    val isConfirmedForSender = notification != null && 
        notification.status == "CONFIRMED" && 
        (activeMember?.role == notification.senderRole || activeMember?.id == notification.senderId)

    AnimatedVisibility(
        visible = isPendingForActiveUser || isConfirmedForSender,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        if (notification != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isPendingForActiveUser) Color(0xFFECFDF5) else Color(0xFFEFF6FF)),
                border = BorderStroke(1.dp, if (isPendingForActiveUser) DesignTokens.EmeraldGlow else DesignTokens.CobaltAccent),
                shape = RoundedCornerShape(DesignTokens.CornerRadius),
                modifier = Modifier.fillMaxWidth().springClickable { onClickBanner(notification) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(CircleShape).background(if (isPendingForActiveUser) DesignTokens.EmeraldGlow.copy(alpha = 0.2f) else DesignTokens.CobaltAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (isPendingForActiveUser) "💸" else notification.selectedEmoji, fontSize = 20.sp)
                        }

                        Column {
                            val title = if (isPendingForActiveUser) "Transfer Masuk dari ${notification.senderName}" else "Konfirmasi Transfer dari Istri! ${notification.selectedEmoji}"
                            val subtitle = if (isPendingForActiveUser) "Sebesar ${fmt.format(notification.amount)} • Klik untuk Konfirmasi" else "${notification.recipientName} telah menerima ${fmt.format(notification.amount)}"
                            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DesignTokens.TextPrimary)
                            Text(text = subtitle, fontSize = 11.sp, color = DesignTokens.TextSecondary)
                        }
                    }

                    TextButton(onClick = { onClickBanner(notification) }) {
                        Text(
                            text = if (isPendingForActiveUser) "Konfirmasi" else "Lihat",
                            fontWeight = FontWeight.Bold,
                            color = if (isPendingForActiveUser) DesignTokens.EmeraldGlow else DesignTokens.CobaltAccent,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

