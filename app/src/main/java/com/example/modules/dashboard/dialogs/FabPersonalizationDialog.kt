package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.FabPosition
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FabPersonalizationDialog(
    currentPosition: FabPosition,
    onSelectPosition: (FabPosition) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DesignTokens.BackgroundBottom,
        dragHandle = {
            Surface(
                modifier = Modifier.padding(top = 8.dp),
                color = DesignTokens.BorderLight,
                shape = RoundedCornerShape(4.dp)
            ) {
                Box(modifier = Modifier.size(width = 36.dp, height = 4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ergonomi One-Hand Operation",
                        color = DesignTokens.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Posisi Tombol Floating Tambah Transaksi",
                        color = DesignTokens.TextSecondary,
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = DesignTokens.TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FabPosition.entries.forEach { pos ->
                FabPositionOptionCard(
                    position = pos,
                    isSelected = pos == currentPosition,
                    onSelect = {
                        onSelectPosition(pos)
                        onDismiss()
                    }
                )
            }
        }
    }
}
