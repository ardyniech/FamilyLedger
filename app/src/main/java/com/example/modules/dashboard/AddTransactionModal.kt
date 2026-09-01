package com.example.modules.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.modules.dashboard.primitives.*
import com.example.shared.models.Category
import com.example.shared.models.FinancialGoal
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionModal(
    wallets: List<WalletAccount>,
    categories: List<Category>,
    goals: List<FinancialGoal> = emptyList(),
    transactionState: TransactionState = TransactionState.Idle,
    onDismiss: () -> Unit,
    onSubmit: (amount: Long, note: String, walletId: String, categoryId: String, isIncome: Boolean, timestamp: Long, goalId: String?) -> Unit,
    onResetState: () -> Unit = {}
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var selectedWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    var selectedTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedGoalId by remember { mutableStateOf<String?>(null) }
    val filteredCategories = remember(isIncome, categories) {
        categories.filter { if (isIncome) it.type == "Income" else it.type == "Expense" }
    }
    var selectedCategoryId by remember { mutableStateOf("") }
    LaunchedEffect(isIncome, filteredCategories) { selectedCategoryId = filteredCategories.firstOrNull()?.id ?: "" }
    
    LaunchedEffect(transactionState) {
        if (transactionState is TransactionState.Success || transactionState is TransactionState.Error) {
            onResetState()
            if (transactionState is TransactionState.Success) onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(DesignTokens.Surface, DesignTokens.BackgroundBottom)))) {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(DesignTokens.PaddingLarge).padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Transaksi Baru", style = MaterialTheme.typography.titleLarge, color = DesignTokens.TextPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(DesignTokens.PaddingMedium))

                TransactionTypeToggle(isIncome = isIncome, onToggle = { isIncome = it })
                Spacer(modifier = Modifier.height(DesignTokens.PaddingMedium))

                DateTimePickerRow(selectedTimestamp = selectedTimestamp, onTimestampChanged = { selectedTimestamp = it })
                Spacer(modifier = Modifier.height(DesignTokens.PaddingLarge))

                WalletSelectorRow(wallets = wallets, selectedWalletId = selectedWalletId, onSelectWallet = { selectedWalletId = it })
                Spacer(modifier = Modifier.height(DesignTokens.PaddingMedium))

                CategorySelectorRow(categories = filteredCategories, selectedCategoryId = selectedCategoryId, isIncome = isIncome, onSelectCategory = { selectedCategoryId = it })
                Spacer(modifier = Modifier.height(DesignTokens.PaddingMedium))

                GoalSelectorRow(goals = goals, selectedGoalId = selectedGoalId, onSelectGoal = { selectedGoalId = it })
                Spacer(modifier = Modifier.height(DesignTokens.PaddingLarge))

                Text(
                    text = if (amount.isEmpty()) "Rp 0" else "Rp $amount",
                    style = MaterialTheme.typography.displayMedium,
                    color = if (isIncome) DesignTokens.EmeraldGlow else Color.Red,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(DesignTokens.PaddingMedium))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("Untuk kebutuhan apa?", color = DesignTokens.TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DesignTokens.TextPrimary,
                        unfocusedTextColor = DesignTokens.TextPrimary,
                        focusedBorderColor = DesignTokens.CobaltAccent,
                        unfocusedBorderColor = DesignTokens.BorderLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(DesignTokens.PaddingLarge))

                CustomKeypad(onKeyPress = { key ->
                    when (key) {
                        "C" -> amount = ""
                        "Del" -> if (amount.isNotEmpty()) amount = amount.dropLast(1)
                        "=" -> MathUtils.evaluateMath(amount)?.let { res -> amount = if (res % 1.0 == 0.0) res.toLong().toString() else res.toString() }
                        "" -> {}
                        else -> if (amount.length < 24) amount += key
                    }
                })
                Spacer(modifier = Modifier.height(DesignTokens.PaddingLarge))

                TransactionSubmitButton(
                    amount = amount,
                    note = note,
                    isLoading = transactionState is TransactionState.Loading,
                    onValidatedSubmit = { validAmt ->
                        onSubmit(validAmt, note, selectedWalletId, selectedCategoryId, isIncome, selectedTimestamp, selectedGoalId)
                    }
                )
            }
        }
    }
}
