package com.example.modules.dashboard.primitives

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.SavingsIntegrityCalculator
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens

@Composable
fun SavingsIntegrityCard(
    transactions: List<Transaction>,
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    val report = remember(transactions, categories) {
        SavingsIntegrityCalculator.calculate(transactions, categories)
    }
    var expanded by remember { mutableStateOf(false) }

    if (report.categoryDetails.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth().clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🛡️", fontSize = 18.sp)
                    Text("Savings Integrity Tracker", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 14.sp)
                }
                if (report.compromisedCount > 0) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(DesignTokens.CrimsonAccent.copy(alpha = 0.2f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("⚠️ Tabungan Terpakai", color = DesignTokens.CrimsonAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(DesignTokens.EmeraldAccent.copy(alpha = 0.2f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("100% Utuh ✓", color = DesignTokens.EmeraldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Alokasi Tersimpan", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text("Rp ${String.format("%,.0f", report.totalSavingsAllocated)}", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 13.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Dana Terpakai / Bocor", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    Text("Rp ${String.format("%,.0f", report.totalSavingsUsed)}", fontWeight = FontWeight.Bold, color = if (report.totalSavingsUsed > 0) DesignTokens.CrimsonAccent else DesignTokens.EmeraldAccent, fontSize = 13.sp)
                }
            }

            LinearProgressIndicator(
                progress = { (report.overallIntegrityRate / 100.0).toFloat() },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (report.compromisedCount > 0) DesignTokens.AmberAccent else DesignTokens.EmeraldAccent,
                trackColor = DesignTokens.SurfaceGlass
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    HorizontalDivider(color = DesignTokens.SurfaceGlass)
                    report.categoryDetails.forEach { detail ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(detail.category.name, fontSize = 12.sp, color = DesignTokens.TextPrimary)
                            Text(
                                if (detail.isCompromised) "Terpakai: Rp ${String.format("%,.0f", detail.totalSpentDiverted)}" else "Utuh (Rp ${String.format("%,.0f", detail.totalSaved)})",
                                fontSize = 11.sp,
                                color = if (detail.isCompromised) DesignTokens.CrimsonAccent else DesignTokens.TextSecondary,
                                fontWeight = if (detail.isCompromised) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
