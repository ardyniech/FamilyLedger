package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsAndReferenceDialog(
    selectedCurrency: com.example.shared.utils.MultiCurrencyHelper.Currency,
    onSelectCurrency: (com.example.shared.utils.MultiCurrencyHelper.Currency) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DesignTokens.BackgroundBottom,
        dragHandle = {
            Surface(modifier = Modifier.padding(top = 8.dp), color = DesignTokens.BorderLight, shape = RoundedCornerShape(4.dp)) {
                Box(modifier = Modifier.size(36.dp, 4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Pengaturan & Referensi App ℹ️", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    Text("FinFamily Pro v3.0 • Local-First Architecture", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = DesignTokens.TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Pilihan Mata Uang Acuan:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                com.example.shared.utils.MultiCurrencyHelper.Currency.values().forEach { curr ->
                    val isSel = curr == selectedCurrency
                    FilterChip(
                        selected = isSel,
                        onClick = { onSelectCurrency(curr) },
                        label = { Text("${curr.symbol} ${curr.code}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DesignTokens.CobaltAccent,
                            selectedLabelColor = DesignTokens.TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("Informasi Referensi Arsitektur:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))

            Card(colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Storage, contentDescription = "DB", tint = DesignTokens.EmeraldGlow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Storage: Local-First Room Database + AES-256", fontSize = 11.sp, color = DesignTokens.TextPrimary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Sync, contentDescription = "Sync", tint = DesignTokens.CobaltAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sync: P2P Offline Reconciliation & WebSockets", fontSize = 11.sp, color = DesignTokens.TextPrimary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock", tint = DesignTokens.AmberAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Keamanan: Hardware Enclave PIN App Lock", fontSize = 11.sp, color = DesignTokens.TextPrimary)
                    }
                }
            }
        }
    }
}
