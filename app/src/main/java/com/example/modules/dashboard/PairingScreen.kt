package com.example.modules.dashboard

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.sync.SyncState
import com.example.modules.dashboard.primitives.*
import com.example.shared.models.AuthUiState
import com.example.shared.models.Member
import com.example.shared.theme.DesignTokens

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.modules.updater.logic.UpdaterManager
import com.example.modules.updater.models.UpdateStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PairingScreen(
    members: List<Member>,
    activeMemberId: String,
    pairCode: String,
    syncState: SyncState,
    authState: AuthUiState,
    p2pManager: com.example.core.sync.p2p.P2POfflineSyncManager,
    updaterManager: UpdaterManager,
    onSelectActiveMember: (String) -> Unit,
    onJoinHousehold: (String) -> Unit,
    onSignInWithGoogle: (Context) -> Unit,
    onSignOut: (Context) -> Unit,
    onClearAuthError: () -> Unit,
    onBack: () -> Unit
) {
    val activeMember = members.find { it.id == activeMemberId }
    val activeRole = activeMember?.role ?: "Husband"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sinkronisasi & Akun Pasangan", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = DesignTokens.TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass), border = BorderStroke(1.dp, DesignTokens.BorderGlass), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(DesignTokens.AmberAccent.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Favorite, contentDescription = "Harmoni", tint = DesignTokens.AmberAccent, modifier = Modifier.size(24.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Keluarga Terbuka & Harmonis", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary, fontSize = 15.sp)
                        Text("Setiap catatan tersinkron langsung ke HP pasangan via Cloud atau Direct Offline.", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                    }
                }
            }

            P2POfflineSyncCard(
                p2pManager = p2pManager,
                pairCode = pairCode,
                activeRole = activeRole
            )

            GoogleAuthCard(
                authState = authState,
                onSignIn = onSignInWithGoogle,
                onSignOut = onSignOut,
                onClearError = onClearAuthError
            )

            PairingRoleSelector(members = members, activeMemberId = activeMemberId, onSelectActiveMember = onSelectActiveMember)

            PairingCodeCard(pairCode = pairCode)

            JoinHouseholdCard(onJoinHousehold = onJoinHousehold)

            SyncStatusCard(syncState = syncState)

            Card(
                colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass),
                border = BorderStroke(1.dp, DesignTokens.BorderGlass),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Pembaruan Aplikasi (OTA)",
                        fontWeight = FontWeight.Bold,
                        color = DesignTokens.TextPrimary,
                        fontSize = 15.sp
                    )
                    Text(
                        "Periksa dan unduh rilis versi terbaru langsung secara mandiri dari GitHub.",
                        fontSize = 12.sp,
                        color = DesignTokens.TextSecondary
                    )
                    
                    val status by updaterManager.status.collectAsState()
                    val scope = rememberCoroutineScope()
                    
                    Button(
                        onClick = {
                            updaterManager.checkForUpdates(scope)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent)
                    ) {
                        Text(
                            when (status) {
                                is UpdateStatus.Checking -> "Memeriksa..."
                                is UpdateStatus.Downloading -> "Mengunduh..."
                                is UpdateStatus.Verifying -> "Memverifikasi..."
                                is UpdateStatus.ReadyToInstall -> "Siap Pasang"
                                else -> "Periksa Pembaruan"
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
