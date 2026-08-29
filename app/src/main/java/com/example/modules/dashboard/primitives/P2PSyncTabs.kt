package com.example.modules.dashboard.primitives

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.sync.p2p.*
import com.example.shared.theme.DesignTokens
import kotlinx.coroutines.launch

@Composable
fun P2PWifiTab(
    isHostRunning: Boolean,
    onToggleHost: () -> Unit,
    hostIpInput: String,
    onHostIpChange: (String) -> Unit,
    isSyncing: Boolean,
    onSyncWithHost: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Hubungkan kedua HP ke Wi-Fi / Hotspot Lokal yang sama (misal Hotspot HP Suami / Istri).", fontSize = 11.sp, color = DesignTokens.TextSecondary)
        Button(
            onClick = onToggleHost, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = if (isHostRunning) Color(0xFFFF5252) else DesignTokens.CobaltAccent)
        ) { Text(if (isHostRunning) "Matikan Server Host" else "1. Aktifkan Server Host HP Ini", fontSize = 12.sp) }

        if (isHostRunning) {
            Text("📍 Status: Server Host Aktif pada Port 8888. Minta pasangan Anda menekan tombol di bawah.", fontSize = 11.sp, color = DesignTokens.EmeraldGlow, fontWeight = FontWeight.SemiBold)
        }
        OutlinedTextField(value = hostIpInput, onValueChange = onHostIpChange, label = { Text("IP Address HP Pasangan", fontSize = 11.sp) }, placeholder = { Text("Contoh: 192.168.43.1") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Button(
            onClick = onSyncWithHost, enabled = !isSyncing && hostIpInput.isNotBlank(), modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.AmberAccent)
        ) {
            if (isSyncing) { CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)) }
            Text("2. Hubungkan & Tarik Data dari HP Pasangan", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun P2PPayloadTab(
    context: Context,
    p2pManager: P2POfflineSyncManager,
    pairCode: String,
    senderName: String,
    activeRole: String,
    onResult: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var pastePayloadInput by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Salin paket data terenkripsi dan kirimkan via WhatsApp / SMS / Bluetooth / QR tanpa internet.", fontSize = 11.sp, color = DesignTokens.TextSecondary)
        Button(
            onClick = {
                scope.launch {
                    val compressed = p2pManager.createSyncPackage(pairCode, senderName, activeRole).toCompressedBase64()
                    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("P2P Ledger Package", compressed))
                    Toast.makeText(context, "Paket Data Catatan Keuangan berhasil disalin! 📋", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Salin Kode HP Ini", fontSize = 11.sp)
        }
        OutlinedTextField(value = pastePayloadInput, onValueChange = { pastePayloadInput = it }, label = { Text("Tempel Kode Data dari HP Pasangan", fontSize = 11.sp) }, modifier = Modifier.fillMaxWidth().height(80.dp), maxLines = 3)
        Button(
            onClick = {
                if (pastePayloadInput.isBlank()) return@Button
                scope.launch {
                    try {
                        val res = p2pManager.importSyncPackage(P2PSyncPackage.fromCompressedBase64(pastePayloadInput.trim()))
                        pastePayloadInput = ""
                        onResult(res.message)
                        Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                    } catch (e: Exception) { Toast.makeText(context, "Gagal membaca kode: ${e.message}", Toast.LENGTH_LONG).show() }
                }
            },
            modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.EmeraldGlow)
        ) { Text("Impor Data Pasangan", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun P2PNfcBluetoothTab(hardwareInfo: HardwareSupportStatus) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Dukungan NFC Touch Bump & Transfer Bluetooth Langsung.", fontSize = 11.sp, color = DesignTokens.TextSecondary)
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DesignTokens.SurfaceGlass).padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Nfc, contentDescription = null, tint = if (hardwareInfo.hasNfc) DesignTokens.EmeraldGlow else DesignTokens.TextSecondary, modifier = Modifier.size(24.dp))
            Column {
                Text("NFC Hardware", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DesignTokens.TextPrimary)
                Text(if (hardwareInfo.hasNfc) "Hardware NFC siap digunakan. Tempelkan kedua HP." else "Hardware NFC tidak terdeteksi.", fontSize = 10.sp, color = DesignTokens.TextSecondary)
            }
        }
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DesignTokens.SurfaceGlass).padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Bluetooth, contentDescription = null, tint = if (hardwareInfo.hasBluetooth) DesignTokens.CobaltAccent else DesignTokens.TextSecondary, modifier = Modifier.size(24.dp))
            Column {
                Text("Bluetooth Direct", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DesignTokens.TextPrimary)
                Text(if (hardwareInfo.hasBluetooth) "Gunakan 'Salin Kode' lalu bagikan via Bluetooth." else "Bluetooth tidak tersedia.", fontSize = 10.sp, color = DesignTokens.TextSecondary)
            }
        }
    }
}
