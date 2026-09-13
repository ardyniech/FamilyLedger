package com.example.modules.dashboard.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun QuickNavDatabaseResetSection(
    onConfirmClearDatabase: () -> Unit
) {
    var showConfirmClear by remember { mutableStateOf(false) }

    if (!showConfirmClear) {
        Button(
            onClick = { showConfirmClear = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = DesignTokens.AmberAccent.copy(alpha = 0.15f),
                contentColor = DesignTokens.AmberAccent
            ),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Clear DB")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Hapus Seluruh Database", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    } else {
        Card(
            colors = CardDefaults.cardColors(containerColor = DesignTokens.AmberAccent.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🚨 KONFIRMASI RESET DATABASE", fontWeight = FontWeight.Black, color = DesignTokens.AmberAccent, fontSize = 12.sp)
                Text("Apakah Anda yakin ingin mengosongkan seluruh isi database? Tindakan ini tidak dapat dibatalkan.", color = DesignTokens.TextPrimary, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showConfirmClear = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DesignTokens.TextPrimary)
                    ) {
                        Text("Batal", fontSize = 11.sp)
                    }
                    Button(
                        onClick = {
                            onConfirmClearDatabase()
                            showConfirmClear = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.AmberAccent),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ya, Hapus Semua", fontSize = 11.sp, color = DesignTokens.BackgroundBottom, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
