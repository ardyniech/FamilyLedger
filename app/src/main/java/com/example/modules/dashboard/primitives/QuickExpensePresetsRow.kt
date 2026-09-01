package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.atoms.springClickable
import com.example.shared.theme.DesignTokens

data class QuickExpensePreset(
    val emoji: String,
    val title: String,
    val amount: Long,
    val categoryName: String,
    val note: String
)

@Composable
fun QuickExpensePresetsRow(
    onSelectPreset: (QuickExpensePreset) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf(
        QuickExpensePreset("☕", "Kopi/Jajan", 25_000L, "Makanan & Minuman", "Kopi & Snack"),
        QuickExpensePreset("⛽", "Bensin", 50_000L, "Transportasi", "Bensin Kendaraan"),
        QuickExpensePreset("🛒", "Belanja", 100_000L, "Belanja Kebutuhan", "Belanja Harian"),
        QuickExpensePreset("🍽️", "Makan Berdua", 75_000L, "Makanan & Minuman", "Makan Bersama Pasangan"),
        QuickExpensePreset("💡", "Pulsa/Token", 50_000L, "Tagihan & Utilitas", "Token Listrik/Pulsa")
    )

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Filled.Bolt, contentDescription = "Quick", tint = DesignTokens.AmberAccent, modifier = Modifier.size(14.dp))
            Text(
                text = "CATAT CEPAT (1-TAP SHORTCUT)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = DesignTokens.TextSecondary,
                letterSpacing = 0.5.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DesignTokens.SurfaceGlass,
                    border = BorderStroke(0.8.dp, DesignTokens.BorderGlass),
                    modifier = Modifier.springClickable { onSelectPreset(preset) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(preset.emoji, fontSize = 14.sp)
                        Column {
                            Text(preset.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                            Text("Rp ${preset.amount / 1000}K", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = DesignTokens.AmberAccent)
                        }
                    }
                }
            }
        }
    }
}
