package com.example.modules.dashboard.primitives

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiTethering
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
import com.example.core.sync.p2p.P2POfflineSyncManager
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
    var selectedTab by remember { mutableIntStateOf(0) }
    var hostIpInput by remember { mutableStateOf("192.168.43.1") }
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
        Column(modifier = Modifier.padding(DesignTokens.PaddingMedium), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(DesignTokens.CobaltAccent.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.WifiTethering, contentDescription = null, tint = DesignTokens.CobaltAccent, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text("Sinkronisasi Mandiri Offline (P2P)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DesignTokens.TextPrimary)
                        Text("Tanpa Internet & Tanpa Server Cloud", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                    }
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(DesignTokens.EmeraldGlow.copy(alpha = 0.2f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text("Off-Grid Active", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = DesignTokens.EmeraldGlow)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                HardwareChip("Bluetooth", hardwareInfo.hasBluetooth && hardwareInfo.isBluetoothEnabled)
                HardwareChip("Wi-Fi Direct", hardwareInfo.hasWifiDirect)
                HardwareChip("NFC", hardwareInfo.hasNfc && hardwareInfo.isNfcEnabled)
            }
            HorizontalDivider(color = DesignTokens.BorderGlass, thickness = 1.dp)
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(DesignTokens.SurfaceGlass).padding(4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                TabButton("Wi-Fi / Hotspot", selectedTab == 0) { selectedTab = 0 }
                TabButton("Kode Payload / QR", selectedTab == 1) { selectedTab = 1 }
                TabButton("NFC & Bluetooth", selectedTab == 2) { selectedTab = 2 }
            }
            when (selectedTab) {
                0 -> P2PWifiTab(
                    isHostRunning = isHostRunning,
                    onToggleHost = {
                        if (isHostRunning) { p2pManager.stopLocalWifiHost(); isHostRunning = false }
                        else { isHostRunning = true; scope.launch { p2pManager.startLocalWifiHost(port = 8888, pairCode = pairCode, senderName = senderName, senderRole = activeRole, onClientSynced = { res -> syncResultMsg = res.message; Toast.makeText(context, res.message, Toast.LENGTH_LONG).show() }) } }
                    },
                    hostIpInput = hostIpInput, onHostIpChange = { hostIpInput = it }, isSyncing = isSyncing,
                    onSyncWithHost = {
                        isSyncing = true
                        scope.launch {
                            val res = p2pManager.syncWithLocalWifiHost(hostIp = hostIpInput.trim(), port = 8888, pairCode = pairCode, senderName = senderName, senderRole = activeRole)
                            isSyncing = false; syncResultMsg = res.message; Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                        }
                    }
                )
                1 -> P2PPayloadTab(context = context, p2pManager = p2pManager, pairCode = pairCode, senderName = senderName, activeRole = activeRole, onResult = { syncResultMsg = it })
                2 -> P2PNfcBluetoothTab(hardwareInfo = hardwareInfo)
            }
            AnimatedVisibility(visible = syncResultMsg != null) {
                syncResultMsg?.let { msg ->
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DesignTokens.EmeraldGlow.copy(alpha = 0.15f)).padding(10.dp)) {
                        Text(msg, fontSize = 11.sp, color = DesignTokens.EmeraldGlow, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun HardwareChip(name: String, supported: Boolean) {
    Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (supported) DesignTokens.EmeraldGlow.copy(alpha = 0.15f) else DesignTokens.SurfaceGlass).padding(horizontal = 8.dp, vertical = 3.dp)) {
        Text(if (supported) "✓ $name" else "✕ $name", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (supported) DesignTokens.EmeraldGlow else DesignTokens.TextSecondary)
    }
}

@Composable
private fun RowScope.TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(if (selected) DesignTokens.CobaltAccent else Color.Transparent).clickable { onClick() }.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
        Text(text = text, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = if (selected) Color.White else DesignTokens.TextSecondary, textAlign = TextAlign.Center)
    }
}
