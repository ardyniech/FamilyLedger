package com.example.modules.dashboard.csv.primitives

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
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
    onSelectTemplate: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val text = stream.bufferedReader().readText()
                    if (text.isNotBlank()) {
                        onTextChange(text)
                        Toast.makeText(context, "Berhasil memuat file CSV!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membaca file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
            ) {
                Icon(Icons.Filled.FolderOpen, contentDescription = "File", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pilih File CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    val clip = clipboardManager.getText()?.text
                    if (!clip.isNullOrBlank()) {
                        onTextChange(clip)
                        Toast.makeText(context, "Teks dari clipboard berhasil ditempel!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Clipboard kosong", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, DesignTokens.CobaltAccent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DesignTokens.CobaltAccent)
            ) {
                Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tempel Teks", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Text("Atau Gunakan Template Contoh:", color = DesignTokens.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)

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
            modifier = Modifier.fillMaxWidth().height(140.dp),
            placeholder = { Text("Isi data CSV akan muncul di sini secara otomatis...", color = DesignTokens.TextMuted, fontSize = 11.sp) },
            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = DesignTokens.TextPrimary),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DesignTokens.SurfaceElevated,
                unfocusedContainerColor = DesignTokens.Surface,
                focusedBorderColor = DesignTokens.CobaltAccent,
                unfocusedBorderColor = DesignTokens.BorderGlass
            )
        )
    }
}
