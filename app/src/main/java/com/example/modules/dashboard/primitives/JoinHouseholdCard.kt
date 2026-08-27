package com.example.modules.dashboard.primitives

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun JoinHouseholdCard(
    onJoinHousehold: (String) -> Unit
) {
    val context = LocalContext.current
    var inputCode by remember { mutableStateOf("") }
    var showJoinSuccess by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Gabung ke Kode Pasangan", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
            
            OutlinedTextField(
                value = inputCode,
                onValueChange = { inputCode = it.uppercase() },
                placeholder = { Text("Contoh: HMY-8821") },
                label = { Text("Masukkan Kode dari HP Pasangan") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DesignTokens.CobaltAccent)
            )

            Button(
                onClick = {
                    if (inputCode.isNotBlank()) {
                        onJoinHousehold(inputCode.trim())
                        showJoinSuccess = true
                        Toast.makeText(context, "Berhasil terhubung ke keluarga!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent),
                shape = RoundedCornerShape(12.dp),
                enabled = inputCode.length >= 4
            ) {
                Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hubungkan Perangkat", fontWeight = FontWeight.Bold)
            }

            AnimatedVisibility(visible = showJoinSuccess) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DesignTokens.EmeraldGlow.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DesignTokens.EmeraldGlow)
                    Text("Perangkat berhasil terhubung!", color = DesignTokens.EmeraldGlow, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}
