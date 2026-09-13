package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.KitchenPantryItem
import com.example.modules.dashboard.logic.PantryStockLevel
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette
import com.example.shared.utils.MathUtils

@Composable
fun KitchenPantryStockCard(
    pantryItems: List<KitchenPantryItem>,
    onRestockItem: (KitchenPantryItem) -> Unit,
    onToggleStockStatus: (KitchenPantryItem) -> Unit
) {
    val palette = RoleThemePalette.istri()
    val lowStockCount = pantryItems.count { it.isRestockNeeded }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(shape = RoundedCornerShape(8.dp), color = palette.primarySoft) {
                        Icon(
                            Icons.Default.Kitchen,
                            contentDescription = null,
                            tint = palette.primaryAccent,
                            modifier = Modifier.padding(6.dp).size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            "🥣 Stok Dapur & Pengingat Belanja",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DesignTokens.TextPrimary
                        )
                        Text(
                            if (lowStockCount > 0) "$lowStockCount bahan hampir habis perlu restock" else "Semua stok dapur masih aman",
                            fontSize = 11.sp,
                            color = if (lowStockCount > 0) DesignTokens.RoseAccent else DesignTokens.EmeraldAccent
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                pantryItems.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (item.isRestockNeeded) palette.backgroundTint else DesignTokens.Surface,
                        border = BorderStroke(1.dp, if (item.isRestockNeeded) palette.primarySoft else DesignTokens.BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = item.emoji, fontSize = 20.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = item.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DesignTokens.TextPrimary
                                    )
                                    Text(text = item.stockLevel.emoji, fontSize = 10.sp)
                                }
                                Text(
                                    text = "${item.category} • Estimasi ${item.estimatedDaysLeft} hari lagi",
                                    fontSize = 10.sp,
                                    color = DesignTokens.TextSecondary
                                )
                            }

                            if (item.isRestockNeeded) {
                                Button(
                                    onClick = { onRestockItem(item) },
                                    modifier = Modifier.height(32.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Beli ${MathUtils.formatRupiah(item.defaultRestockCost)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { onToggleStockStatus(item) },
                                    modifier = Modifier.height(30.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp)
                                ) {
                                    Text(item.stockLevel.label, fontSize = 10.sp, color = DesignTokens.TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
