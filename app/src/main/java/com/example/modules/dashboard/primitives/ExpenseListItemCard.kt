package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

import androidx.compose.foundation.clickable

@Composable
fun ExpenseListItemCard(
    expense: Transaction,
    category: Category?,
    wallet: WalletAccount?,
    member: Member?,
    onClick: (() -> Unit)? = null
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(DesignTokens.PaddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(DesignTokens.AmberAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(category?.name?.take(1) ?: "🛒", fontWeight = FontWeight.Bold, color = DesignTokens.AmberAccent, fontSize = 18.sp)
                }
                Column {
                    Text(expense.note, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(category?.name ?: "Pengeluaran", style = MaterialTheme.typography.bodySmall, color = DesignTokens.TextSecondary)
                        Text("•", color = DesignTokens.TextSecondary.copy(alpha = 0.5f))
                        Text(wallet?.name ?: "Dompet", style = MaterialTheme.typography.bodySmall, color = DesignTokens.TextSecondary)
                        member?.let {
                            Text("•", color = DesignTokens.TextSecondary.copy(alpha = 0.5f))
                            Text(it.name, style = MaterialTheme.typography.bodySmall, color = DesignTokens.CobaltAccent, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            Text(formatter.format(-expense.amount), fontWeight = FontWeight.ExtraBold, color = Color.Red, style = MaterialTheme.typography.titleMedium)
        }
    }
}
