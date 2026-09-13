package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.WifeExpensePresetDefaults
import com.example.modules.dashboard.logic.WifeExpensePresetItem
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette
import com.example.shared.utils.MathUtils

@Composable
fun WifeQuickExpensePresetsCard(
    onSelectPreset: (WifeExpensePresetItem) -> Unit,
    onEditPreset: (WifeExpensePresetItem) -> Unit = {}
) {
    val palette = RoleThemePalette.istri()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated),
        border = BorderStroke(1.dp, palette.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("⚡ Catat Cepat Belanja Dapur 1-Ketuk", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    Text("Ketuk item atau ikon pensil untuk custom harga", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().height(260.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(WifeExpensePresetDefaults.items) { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectPreset(item) },
                        shape = RoundedCornerShape(12.dp),
                        color = palette.backgroundTint,
                        border = BorderStroke(1.dp, palette.primarySoft)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = item.emoji, fontSize = 20.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DesignTokens.TextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = MathUtils.formatRupiah(item.defaultAmount),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = palette.primaryAccent
                                )
                            }
                            IconButton(
                                onClick = { onEditPreset(item) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Nominal",
                                    tint = DesignTokens.TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
