package com.example.modules.dashboard.primitives

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.sync.p2p.HardwareSupportChecker
import com.example.core.sync.p2p.P2PImportResult
import com.example.core.sync.p2p.P2POfflineSyncManager
import com.example.core.sync.p2p.P2PSyncPackage
import com.example.shared.theme.DesignTokens
import kotlinx.coroutines.launch

@Composable
fun P2POfflineSyncCard(
    p2pManager: P2POfflineSyncManager,
    pairCode: String,
    activeRole: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hardwareInfo = remember(context) { HardwareSupportChecker.checkHardwareSupport(context) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Wi-Fi/Hotspot, 1: QR & Code, 2: NFC / Bluetooth
    var hostIpInput by remember { mutableStateOf("192.168.43.1") }
    var pastePayloadInput by remember { mutableStateOf("") }
    var isHostRunning by remember { mutableStateOf(false) }
    var syncResultMsg by remember { mutableStateOf<String?>(null) }
    var isSyncing by remember { mutableStateOf(false) }

    val senderName = if (activeRole == "Husband") "Suami" else "Istri"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
    ) {
        Column(
            modifier = Modifier.padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DesignTokens.CobaltAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.WifiTethering, contentDescription = null, tint = DesignTokens.CobaltAccent, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text("Sinkronisasi Mandiri Offline (P2P)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DesignTokens.TextPrimary)
                        Text("Tanpa Internet & Tanpa Server Cloud", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DesignTokens.EmeraldGlow.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("Off-Grid Active", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = DesignTokens.EmeraldGlow)
                }
            }

            // Hardware capability pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HardwareChip("Bluetooth", hardwareInfo.hasBluetooth && hardwareInfo.isBluetoothEnabled)
                HardwareChip("Wi-Fi Direct", hardwareInfo.hasWifiDirect)
                HardwareChip("NFC", hardwareInfo.hasNfc && hardwareInfo.isNfcEnabled)
            }

            HorizontalDivider(color = DesignTokens.BorderGlass, thickness = 1.dp)

            // Method Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DesignTokens.SurfaceGlass)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabButton("Wi-Fi / Hotspot", selectedTab == 0) { selectedTab = 0 }
                TabButton("Kode Payload / QR", selectedTab == 1) { selectedTab = 1 }
                TabButton("NFC & Bluetooth", selectedTab == 2) { selectedTab = 2 }
            }

            // TAB 0: WI-FI / HOTSPOT DIRECT
            if (selectedTab == 0) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Hubungkan kedua HP ke Wi-Fi / Hotspot Lokal yang sama (misal Hotspot HP Suami / Istri).",
                        fontSize = 11.sp,
                        color = DesignTokens.TextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (isHostRunning) {
                                    p2pManager.stopLocalWifiHost()
                                    isHostRunning = false
                                } else {
                                    isHostRunning = true
                                    scope.launch {
                                        p2pManager.startLocalWifiHost(
                                            port = 8888,
                                            pairCode = pairCode,
                                            senderName = senderName,
                                            senderRole = activeRole,
                                            onClientSynced = { res ->
                                                syncResultMsg = res.message
                                                Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isHostRunning) Color(0xFFFF5252) else DesignTokens.CobaltAccent
                            )
                        ) {
                            Text(if (isHostRunning) "Matikan Server Host" else "1. Aktifkan Server Host HP Ini", fontSize = 12.sp)
                        }
                    }

                    if (isHostRunning) {
                        Text(
                            "📍 Status: Server Host Aktif pada Port 8888. Minta pasangan Anda menekan tombol di bawah.",
                            fontSize = 11.sp,
                            color = DesignTokens.EmeraldGlow,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedTextField(
                        value = hostIpInput,
                        onValueChange = { hostIpInput = it },
                        label = { Text("IP Address HP Pasangan", fontSize = 11.sp) },
                        placeholder = { Text("Contoh: 192.168.43.1") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            isSyncing = true
                            scope.launch {
                                val res = p2pManager.syncWithLocalWifiHost(
                                    hostIp = hostIpInput.trim(),
                                    port = 8888,
                                    pairCode = pairCode,
                                    senderName = senderName,
                                    senderRole = activeRole
                                )
                                isSyncing = false
                                syncResultMsg = res.message
                                Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                            }
                        },
                        enabled = !isSyncing && hostIpInput.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.AmberAccent)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("2. Hubungkan & Tarik Data dari HP Pasangan", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // TAB 1: KODE PAYLOAD / QR
            if (selectedTab == 1) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Salin paket data terenkripsi dan kirimkan via WhatsApp / SMS / Bluetooth / QR tanpa internet.",
                        fontSize = 11.sp,
                        color = DesignTokens.TextSecondary
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    val pkg = p2pManager.createSyncPackage(pairCode, senderName, activeRole)
                                    val compressed = pkg.toCompressedBase64()
                                    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cb.setPrimaryClip(ClipData.newPlainText("P2P Ledger Package", compressed))
                                    Toast.makeText(context, "Paket Data Catatan Keuangan berhasil disalin! 📋", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Salin Kode HP Ini", fontSize = 11.sp)
                        }
                    }

                    OutlinedTextField(
                        value = pastePayloadInput,
                        onValueChange = { pastePayloadInput = it },
                        label = { Text("Tempel Kode Data dari HP Pasangan", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        maxLines = 3
                    )

                    Button(
                        onClick = {
                            if (pastePayloadInput.isBlank()) return@Button
                            scope.launch {
                                try {
                                    val pkg = P2PSyncPackage.fromCompressedBase64(pastePayloadInput.trim())
                                    val res = p2pManager.importSyncPackage(pkg)
                                    syncResultMsg = res.message
                                    pastePayloadInput = ""
                                    Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Gagal membaca format kode: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.EmeraldGlow)
                    ) {
                        Text("Impor Data Pasangan", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // TAB 2: NFC & BLUETOOTH
            if (selectedTab == 2) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Dukungan NFC Touch Bump & Transfer Bluetooth Langsung.",
                        fontSize = 11.sp,
                        color = DesignTokens.TextSecondary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DesignTokens.SurfaceGlass)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Nfc,
                            contentDescription = null,
                            tint = if (hardwareInfo.hasNfc) DesignTokens.EmeraldGlow else DesignTokens.TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text("NFC Hardware", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DesignTokens.TextPrimary)
                            Text(
                                if (hardwareInfo.hasNfc) "Hardware NFC siap digunakan. Tempelkan kedua HP." else "Hardware NFC tidak terdeteksi pada perangkat ini.",
                                fontSize = 10.sp,
                                color = DesignTokens.TextSecondary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DesignTokens.SurfaceGlass)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Bluetooth,
                            contentDescription = null,
                            tint = if (hardwareInfo.hasBluetooth) DesignTokens.CobaltAccent else DesignTokens.TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text("Bluetooth Direct", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DesignTokens.TextPrimary)
                            Text(
                                if (hardwareInfo.hasBluetooth) "Gunakan 'Salin Kode' lalu bagikan file via Bluetooth." else "Bluetooth tidak tersedia.",
                                fontSize = 10.sp,
                                color = DesignTokens.TextSecondary
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = syncResultMsg != null) {
                syncResultMsg?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DesignTokens.EmeraldGlow.copy(alpha = 0.15f))
                            .padding(10.dp)
                    ) {
                        Text(msg, fontSize = 11.sp, color = DesignTokens.EmeraldGlow, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun HardwareChip(name: String, supported: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (supported) DesignTokens.EmeraldGlow.copy(alpha = 0.15f) else DesignTokens.SurfaceGlass)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = if (supported) "✓ $name" else "✕ $name",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (supported) DesignTokens.EmeraldGlow else DesignTokens.TextSecondary
        )
    }
}

@Composable
private fun RowScope.TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) DesignTokens.CobaltAccent else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else DesignTokens.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
