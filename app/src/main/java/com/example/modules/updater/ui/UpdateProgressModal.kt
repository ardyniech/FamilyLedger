package com.example.modules.updater.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.modules.updater.logic.PackageInstallerManager
import com.example.modules.updater.logic.UpdaterManager
import com.example.modules.updater.models.UpdateStatus
import com.example.shared.theme.DesignTokens
import java.util.Locale

@Composable
fun UpdateProgressModal(updaterManager: UpdaterManager, onDismiss: () -> Unit) {
    val status by updaterManager.status.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val isMandatory = status is UpdateStatus.MandatoryUpdate

    Dialog(onDismissRequest = { if (!isMandatory && status !is UpdateStatus.Downloading) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Pembaruan Tersedia", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                
                when (val currentStatus = status) {
                    is UpdateStatus.UpdateAvailable -> {
                        val info = currentStatus.info
                        Text(info.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DesignTokens.CobaltAccent)
                        Text(info.body.ifEmpty { "Pembaruan performa dan keamanan sistem." }, fontSize = 12.sp, color = DesignTokens.TextSecondary)
                        Button(
                            onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); updaterManager.startDownload(context, scope) },
                            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
                        ) { Text("Unduh & Pasang Sekarang") }
                    }
                    is UpdateStatus.Downloading -> {
                        Text("Mengunduh payload...", fontSize = 13.sp, color = DesignTokens.TextPrimary)
                        LinearProgressIndicator(
                            progress = { currentStatus.progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = DesignTokens.CobaltAccent,
                            trackColor = DesignTokens.BorderLight
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${(currentStatus.progress * 100).toInt()}%", fontSize = 11.sp, color = DesignTokens.TextSecondary)
                            Text(String.format(Locale.US, "%.2f MB/s", currentStatus.speedMbSec), fontSize = 11.sp, color = DesignTokens.TextSecondary)
                        }
                    }
                    is UpdateStatus.Verifying -> {
                        CircularProgressIndicator(color = DesignTokens.CobaltAccent)
                        Text(currentStatus.message, fontSize = 12.sp, color = DesignTokens.TextSecondary)
                    }
                    is UpdateStatus.ReadyToInstall -> {
                        LaunchedEffect(Unit) { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
                        Text("Siap Memasang Pembaruan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldGlow)
                        if (!PackageInstallerManager.canInstallPackages(context)) {
                            Text("Aktifkan izin 'Install unknown apps' di Pengaturan Sistem.", fontSize = 11.sp, color = DesignTokens.RoseAccent, fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); updaterManager.installUpdate(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.EmeraldGlow)
                        ) { Text("Pasang Pembaruan") }
                    }
                    is UpdateStatus.MandatoryUpdate -> {
                        val info = currentStatus.info
                        Text("Pembaruan Keamanan Wajib", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.RoseAccent)
                        Text(info.body.ifEmpty { "Pembaruan keamanan ini wajib dipasang untuk menjaga keamanan data Anda." }, fontSize = 12.sp, color = DesignTokens.TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Text("Pembaruan ini tidak dapat ditunda.", fontSize = 12.sp, color = DesignTokens.RoseAccent, fontWeight = FontWeight.Medium)
                        Button(
                            onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); updaterManager.installUpdate(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.RoseAccent)
                        ) { Text("Pasang Sekarang (Wajib)") }
                    }
                    is UpdateStatus.UpToDate -> {
                        Text("Aplikasi Sudah Terbaru", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.EmeraldGlow)
                        Text("FamilyLedger sudah versi terbaru (v1.0).", fontSize = 12.sp, color = DesignTokens.TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Button(onClick = { updaterManager.resetToIdle(); onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)) { Text("Selesai") }
                    }
                    is UpdateStatus.Failed -> {
                        Text("Gagal Memeriksa", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.RoseAccent)
                        Text(currentStatus.errorMsg, fontSize = 12.sp, color = DesignTokens.TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Button(onClick = { updaterManager.resetToIdle(); onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.RoseAccent)) { Text("Tutup") }
                    }
                    else -> CircularProgressIndicator(color = DesignTokens.CobaltAccent)
                }
            }
        }
    }
}
