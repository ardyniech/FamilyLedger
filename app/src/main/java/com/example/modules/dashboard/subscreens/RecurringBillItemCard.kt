package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.RecurringBill
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat

@Composable
fun RecurringBillItemCard(
    bill: RecurringBill,
    targetWallet: WalletAccount?,
    formatter: NumberFormat,
    onDeleteBill: (String) -> Unit,
    onPayClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = bill.name, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 15.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 2.dp)) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(DesignTokens.TextSecondary.copy(alpha = 0.1f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(text = bill.frequency, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextSecondary)
                        }
                        Text(text = "Due: ${bill.dueDate}", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = formatter.format(bill.amount), fontWeight = FontWeight.Bold, color = if (bill.isPaid) DesignTokens.TextSecondary else Color.Red, fontSize = 14.sp)
                    IconButton(onClick = { onDeleteBill(bill.id) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }
                }
            }
            HorizontalDivider(color = DesignTokens.BorderGlass.copy(alpha = 0.5f), thickness = 1.dp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                if (bill.autoPay && targetWallet != null) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(DesignTokens.EmeraldGlow.copy(alpha = 0.1f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(text = "⚡ Auto-Deduct: ${targetWallet.name}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldGlow)
                    }
                } else {
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(DesignTokens.TextSecondary.copy(alpha = 0.1f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(text = "Manual Payment Required", fontSize = 10.sp, color = DesignTokens.TextSecondary, fontWeight = FontWeight.Medium)
                    }
                }
                if (bill.isPaid) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(DesignTokens.EmeraldGlow.copy(alpha = 0.15f)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                        Text("Paid ✓", color = DesignTokens.EmeraldGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { onPayClick(bill.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Pay Now", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
