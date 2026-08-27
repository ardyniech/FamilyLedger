package com.example.modules.dashboard.primitives

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun PairingCodeCard(pairCode: String) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Kode Pairing Keluarga Anda", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextSecondary)
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                DesignTokens.CobaltAccent.copy(alpha = 0.2f),
                                DesignTokens.AmberAccent.copy(alpha = 0.2f)
                            )
                        )
                    )
                    .border(1.dp, DesignTokens.BorderGlass, RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = pairCode,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    color = DesignTokens.TextPrimary
                )
            }

            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Pairing Code", pairCode)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Kode $pairCode disalin!", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DesignTokens.BorderGlass)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Salin Kode", fontSize = 13.sp, color = DesignTokens.TextPrimary)
            }
            
            Text(
                "Bagikan kode ini ke HP pasangan agar data tersambung otomatis.",
                fontSize = 11.sp,
                color = DesignTokens.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
