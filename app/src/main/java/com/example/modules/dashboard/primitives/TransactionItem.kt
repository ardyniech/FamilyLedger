package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.clickable

@Composable
fun TransactionItem(
    tx: Transaction,
    member: Member?,
    category: Category?,
    onClick: (() -> Unit)? = null
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val tint = if (member?.role == "Husband") DesignTokens.CobaltAccent else DesignTokens.AmberAccent
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(DesignTokens.PaddingMedium),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main Top Row: Avatar, Info & Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .background(tint.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category?.name?.take(1) ?: member?.name?.take(1) ?: "?",
                            color = tint,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = member?.name ?: "Unknown",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = tint
                        )
                        if (category != null) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = DesignTokens.TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Text(
                    text = formatter.format(tx.amount),
                    color = if (tx.amount < 0) Color.Red else DesignTokens.EmeraldGlow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Dedicated, Wrapped Row for Transaction Note
            if (tx.note.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(DesignTokens.SurfaceGlass)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = tx.note,
                        fontSize = 12.sp,
                        color = DesignTokens.TextPrimary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
