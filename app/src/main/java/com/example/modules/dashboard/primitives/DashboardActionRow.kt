package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun DashboardActionRow(
    onTransferClick: () -> Unit,
    onWalletsClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onPairingClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Button(
            onClick = onTransferClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.Surface),
            border = BorderStroke(1.dp, DesignTokens.BorderGlass),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("Transfer", color = DesignTokens.TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onWalletsClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.Surface),
            border = BorderStroke(1.dp, DesignTokens.BorderGlass),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("Wallets", color = DesignTokens.TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onCategoriesClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.Surface),
            border = BorderStroke(1.dp, DesignTokens.BorderGlass),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("Kategori", color = DesignTokens.TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onPairingClick,
            modifier = Modifier.weight(1.1f),
            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.Surface),
            border = BorderStroke(1.dp, DesignTokens.BorderGlass),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("🔗 Pasangan", color = DesignTokens.TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
