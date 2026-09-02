package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.dialogs.AddDebtDialog
import com.example.modules.dashboard.primitives.DebtItemCard
import com.example.shared.models.DebtRecord
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtLoanTrackerScreen(
    debts: List<DebtRecord>,
    onAddDebt: (DebtRecord) -> Unit,
    onPayDebt: (String, Long) -> Unit,
    onDeleteDebt: (String) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val totalHutang = remember(debts) { debts.filter { it.isHutang && !it.isSettled }.sumOf { it.remainingAmount } }
    val totalPiutang = remember(debts) { debts.filter { !it.isHutang && !it.isSettled }.sumOf { it.remainingAmount } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pencatatan Hutang & Piutang", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DesignTokens.TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DesignTokens.BackgroundBottom)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = DesignTokens.CobaltAccent
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Debt", tint = DesignTokens.TextPrimary)
            }
        },
        containerColor = DesignTokens.BackgroundBottom
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceElevated)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Total Hutang Saya", color = DesignTokens.TextSecondary, fontSize = 12.sp)
                        Text(MathUtils.formatRupiah(totalHutang), color = DesignTokens.RoseAccent, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Piutang Saya", color = DesignTokens.TextSecondary, fontSize = 12.sp)
                        Text(MathUtils.formatRupiah(totalPiutang), color = DesignTokens.EmeraldGlow, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(debts) { debt ->
                    DebtItemCard(
                        debt = debt,
                        onPay = { amt -> onPayDebt(debt.id, amt) },
                        onDelete = { onDeleteDebt(debt.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddDebtDialog(
            onDismiss = { showAddDialog = false },
            onSubmit = { newDebt ->
                onAddDebt(newDebt)
                showAddDialog = false
            }
        )
    }
}
