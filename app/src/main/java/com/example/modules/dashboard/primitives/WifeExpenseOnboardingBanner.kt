package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens
import com.example.shared.theme.RoleThemePalette

@Composable
fun WifeExpenseOnboardingBanner(
    onOpenWifeHub: () -> Unit,
    onQuickAddExpense: () -> Unit
) {
    val palette = RoleThemePalette.istri()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = palette.primaryContainer),
        border = BorderStroke(1.dp, palette.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = palette.primaryAccent
                    ) {
                        Icon(
                            Icons.Default.Kitchen,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp).size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Mode Istri: Pos Belanja & Dapur 🌸",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DesignTokens.TextPrimary
                        )
                        Text(
                            text = "Dompet & pos pengeluaran siap pakai",
                            fontSize = 10.sp,
                            color = DesignTokens.TextSecondary
                        )
                    }
                }
            }

            Text(
                text = "Bunda bisa langsung mencatat belanja sayur harian, sembako bulanan, susu anak, hingga self-care dengan dompet terpisah.",
                fontSize = 11.sp,
                color = DesignTokens.TextPrimary,
                lineHeight = 15.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenWifeHub,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Kitchen, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pusat Istri", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                    onClick = onQuickAddExpense,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, palette.primaryAccent),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp), tint = palette.primaryAccent)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Catat Belanja", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.primaryAccent)
                }
            }
        }
    }
}
