package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.DashboardCardType
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardPersonalizationDialog(
    cardOrder: List<DashboardCardType>,
    hiddenCards: Set<DashboardCardType>,
    onMoveUp: (DashboardCardType) -> Unit,
    onMoveDown: (DashboardCardType) -> Unit,
    onToggleVisibility: (DashboardCardType) -> Unit,
    onResetDefault: () -> Unit,
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
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 28.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Personalisasi Card Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    Text("Atur urutan dan tampilkan card sesuai selera Anda", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                }
                IconButton(onClick = onDismiss) { Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = DesignTokens.TextSecondary) }
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().weight(1f, fill = false)) {
                items(cardOrder) { card ->
                    val isHidden = hiddenCards.contains(card)
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (isHidden) DesignTokens.Surface.copy(alpha = 0.5f) else DesignTokens.Surface), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Checkbox(checked = !isHidden, onCheckedChange = { onToggleVisibility(card) }, colors = CheckboxDefaults.colors(checkedColor = DesignTokens.CobaltAccent))
                                Column {
                                    Text(card.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isHidden) DesignTokens.TextSecondary else DesignTokens.TextPrimary)
                                    Text(card.description, fontSize = 10.sp, color = DesignTokens.TextSecondary)
                                }
                            }
                            Row {
                                IconButton(onClick = { onMoveUp(card) }, enabled = cardOrder.indexOf(card) > 0) { Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Up", tint = DesignTokens.TextPrimary, modifier = Modifier.size(18.dp)) }
                                IconButton(onClick = { onMoveDown(card) }, enabled = cardOrder.indexOf(card) < cardOrder.size - 1) { Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Down", tint = DesignTokens.TextPrimary, modifier = Modifier.size(18.dp)) }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onResetDefault, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "Reset", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset Urutan Ke Default", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
