package com.example.modules.dashboard.csv.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.csv.ParsedTransaction
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CsvPreviewItemRow(item: ParsedTransaction) {
    val currencyFmt = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }
    val dateFmt = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID"))
    val typeColor = when (item.rawType) {
        "Income" -> DesignTokens.EmeraldGlow
        "Transfer" -> DesignTokens.CobaltAccent
        else -> Color(0xFFFF5252)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(0.5.dp, if (item.isDuplicate) DesignTokens.AmberAccent.copy(alpha = 0.5f) else DesignTokens.BorderGlass)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (item.rawCategory.isNotBlank()) item.rawCategory.replaceFirstChar { it.uppercase() } else item.rawType,
                        color = DesignTokens.TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.isDuplicate) {
                        Box(
                            modifier = Modifier.background(DesignTokens.AmberAccent.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Sudah Ada", color = DesignTokens.AmberAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("💳 ${item.rawAccount}", color = DesignTokens.TextSecondary, fontSize = 11.sp)
                    Text("•", color = DesignTokens.TextMuted, fontSize = 11.sp)
                    Text(dateFmt.format(Date(item.timestamp)), color = DesignTokens.TextMuted, fontSize = 11.sp)
                }
                if (item.note.isNotBlank()) {
                    Text("📝 ${item.note}", color = DesignTokens.TextSecondary, fontSize = 10.sp, maxLines = 1)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val prefix = if (item.rawType == "Income") "+" else if (item.rawType == "Transfer") "⇄ " else "-"
                Text(
                    text = "$prefix${currencyFmt.format(kotlin.math.abs(item.amount))}",
                    color = typeColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(item.rawType, color = DesignTokens.TextMuted, fontSize = 10.sp)
            }
        }
    }
}
