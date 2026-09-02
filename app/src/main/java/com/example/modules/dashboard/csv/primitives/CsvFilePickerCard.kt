package com.example.modules.dashboard.csv.primitives

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@Composable
fun CsvFilePickerCard(
    fileName: String?,
    fileSizeText: String?,
    lineCount: Int,
    onFileLoaded: (name: String, sizeText: String, content: String) -> Unit,
    onClearFile: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                var name = "file_import.csv"
                context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIdx >= 0) name = cursor.getString(nameIdx)
                }

                context.contentResolver.openInputStream(it)?.use { stream ->
                    val bytes = stream.readBytes()
                    val text = String(bytes, Charsets.UTF_8)
                    val sizeKb = bytes.size / 1024.0
                    val sizeStr = if (sizeKb > 1024) String.format("%.2f MB", sizeKb / 1024.0) else String.format("%.1f KB", sizeKb)
                    if (text.isNotBlank()) {
                        onFileLoaded(name, sizeStr, text)
                        Toast.makeText(context, "File $name berhasil dimuat!", Toast.LENGTH_SHORT).show()
                    } else Toast.makeText(context, "File CSV kosong", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membaca file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (fileName != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(shape = RoundedCornerShape(10.dp), color = DesignTokens.CobaltAccent.copy(alpha = 0.15f), modifier = Modifier.size(38.dp)) {
                            Icon(Icons.Filled.Description, contentDescription = "CSV", tint = DesignTokens.CobaltAccent, modifier = Modifier.padding(8.dp))
                        }
                        Column {
                            Text(fileName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                            Text("${fileSizeText ?: "0 KB"} • $lineCount Baris Data", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                        }
                    }
                    IconButton(onClick = onClearFile) { Icon(Icons.Filled.Delete, contentDescription = "Clear", tint = DesignTokens.TextSecondary, modifier = Modifier.size(20.dp)) }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.FolderOpen, contentDescription = "Folder", tint = DesignTokens.CobaltAccent, modifier = Modifier.size(32.dp))
                    Text("Belum ada file CSV yang dipilih", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    Text("Pilih file .csv dari penyimpanan HP Anda untuk memulai validasi.", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { filePickerLauncher.launch("*/*") },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
                ) {
                    Icon(Icons.Filled.FolderOpen, contentDescription = "Pick File", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (fileName == null) "Pilih File CSV" else "Ganti File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val clip = clipboardManager.getText()?.text
                        if (!clip.isNullOrBlank()) {
                            onFileLoaded("Clipboard_Content.csv", "${clip.length} chars", clip)
                            Toast.makeText(context, "Teks clipboard berhasil ditempel!", Toast.LENGTH_SHORT).show()
                        } else Toast.makeText(context, "Clipboard kosong", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, DesignTokens.BorderGlass)
                ) {
                    Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
