package com.example.modules.dashboard.csv.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun CsvInputSection(
    rawText: String,
    onTextChange: (String) -> Unit,
    onAnalyze: () -> Unit,
    onSelectTemplate: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Pilih Format / Tempel Data CSV:", color = DesignTokens.TextSecondary, fontSize = 12.sp)

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CsvSampleTemplates.templates.forEach { t ->
                OutlinedButton(
                    onClick = { onSelectTemplate(t.sampleCsv) },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DesignTokens.BorderGlass),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DesignTokens.Surface)
                ) {
                    Text(t.title, fontSize = 11.sp, color = DesignTokens.TextPrimary)
                }
            }
        }

        OutlinedTextField(
            value = rawText,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth().height(150.dp),
            placeholder = { Text("Paste atau ketik teks CSV di sini...", color = DesignTokens.TextMuted, fontSize = 12.sp) },
            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = DesignTokens.TextPrimary),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DesignTokens.SurfaceElevated,
                unfocusedContainerColor = DesignTokens.Surface,
                focusedBorderColor = DesignTokens.CobaltAccent,
                unfocusedBorderColor = DesignTokens.BorderGlass
            )
        )

        Button(
            onClick = onAnalyze,
            enabled = rawText.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
        ) {
            Text("🔍 Analisis & Deteksi Format", color = DesignTokens.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
