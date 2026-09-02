package com.example.modules.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.primitives.*
import com.example.shared.models.*
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MathUtils

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
    BackHandler(onBack = onDismiss)

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var selectedWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    var selectedTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedGoalId by remember { mutableStateOf<String?>(null) }
    val filteredCategories = remember(isIncome, categories) { categories.filter { if (isIncome) it.type == "Income" else it.type == "Expense" } }
    var selectedCategoryId by remember { mutableStateOf("") }
    var showWalletDrawer by remember { mutableStateOf(false) }
    var showCategoryDrawer by remember { mutableStateOf(false) }

    LaunchedEffect(isIncome, filteredCategories) { selectedCategoryId = filteredCategories.firstOrNull()?.id ?: "" }
    LaunchedEffect(transactionState) {
        if (transactionState is TransactionState.Success || transactionState is TransactionState.Error) {
            onResetState()
            if (transactionState is TransactionState.Success) onDismiss()
        }
    }

    val parsedEval = remember(amount) { if (amount.isBlank()) null else MathUtils.evaluateMath(amount) }
    val displayAmountText = remember(amount, parsedEval) {
        if (amount.isEmpty()) "Rp 0"
        else if (parsedEval != null && parsedEval.isFinite() && parsedEval > 0) MathUtils.formatRupiah(parsedEval.toLong())
        else "Rp $amount"
    }

    val onKeyPressHandler: (String) -> Unit = remember {
        { key ->
            when (key) {
                "C" -> amount = ""
                "Del" -> if (amount.isNotEmpty()) amount = amount.dropLast(1)
                "=" -> MathUtils.evaluateMath(amount)?.let { res -> amount = if (res % 1.0 == 0.0) res.toLong().toString() else res.toString() }
                else -> if (amount.length < 24) amount += key
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = DesignTokens.BackgroundBottom) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DesignTokens.Surface, DesignTokens.BackgroundBottom))).windowInsetsPadding(WindowInsets.statusBars).windowInsetsPadding(WindowInsets.navigationBars)) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) { Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = DesignTokens.TextPrimary) }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Tambah Transaksi", color = DesignTokens.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    TransactionTypeToggle(isIncome = isIncome, onToggle = { isIncome = it })
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { DateTimePickerRow(selectedTimestamp = selectedTimestamp, onTimestampChanged = { selectedTimestamp = it }) }
                Spacer(modifier = Modifier.height(4.dp))
                WalletSelectorRow(wallets = wallets, selectedWalletId = selectedWalletId, onSelectWallet = { selectedWalletId = it }, onOpenDrawer = { showWalletDrawer = true })
                Spacer(modifier = Modifier.height(4.dp))
                CategorySelectorRow(categories = filteredCategories, selectedCategoryId = selectedCategoryId, isIncome = isIncome, onSelectCategory = { selectedCategoryId = it }, onOpenDrawer = { showCategoryDrawer = true })
                Spacer(modifier = Modifier.height(6.dp))
                AnimatedContent(
                    targetState = displayAmountText,
                    transitionSpec = { (fadeIn(animationSpec = tween(90)) + scaleIn(initialScale = 0.96f, animationSpec = tween(90))).togetherWith(fadeOut(animationSpec = tween(70))) },
                    label = "AmountTypingAnimation"
                ) { targetText -> Text(text = targetText, fontSize = 28.sp, color = if (isIncome) DesignTokens.EmeraldGlow else Color.Red, fontWeight = FontWeight.ExtraBold) }
                OutlinedTextField(
                    value = note, onValueChange = { note = it },
                    placeholder = { Text("Catatan / Untuk kebutuhan apa? (Opsional)", color = DesignTokens.TextSecondary, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = DesignTokens.TextPrimary, unfocusedTextColor = DesignTokens.TextPrimary, focusedBorderColor = DesignTokens.CobaltAccent, unfocusedBorderColor = DesignTokens.BorderLight),
                    singleLine = true, modifier = Modifier.fillMaxWidth().height(48.dp)
                )
                val suggestedCategories = remember(note, filteredCategories) { com.example.modules.dashboard.ai.CategorySuggestionEngine.suggestCategory(note, filteredCategories) }
                if (suggestedCategories.isNotEmpty()) CategorySuggestionChipRow(suggestions = suggestedCategories, selectedCategoryId = selectedCategoryId, onSelectCategory = { selectedCategoryId = it })
                Spacer(modifier = Modifier.height(6.dp))
                CustomKeypad(onKeyPress = onKeyPressHandler)
                Spacer(modifier = Modifier.weight(1f))
                TransactionSubmitButton(
                    amount = amount, note = note, isIncome = isIncome, isLoading = transactionState is TransactionState.Loading,
                    onValidatedSubmit = { validAmt ->
                        val catObj = filteredCategories.find { it.id == selectedCategoryId } ?: filteredCategories.firstOrNull()
                        val catId = catObj?.id ?: selectedCategoryId
                        val catName = catObj?.name ?: if (isIncome) "Pemasukan" else "Pengeluaran"
                        val walId = if (selectedWalletId.isNotBlank()) selectedWalletId else (wallets.firstOrNull()?.id ?: "")
                        onSubmit(validAmt, note.ifBlank { catName }, walId, catId, isIncome, selectedTimestamp, selectedGoalId)
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
    if (showWalletDrawer) WindowsTileDrawer(title = "Pilih Akun / Sumber Dana", subtitle = "Sistem Icon Grid ala Windows OS", wallets = wallets, selectedWalletId = selectedWalletId, onSelectWallet = { selectedWalletId = it }, onDismiss = { showWalletDrawer = false })
    if (showCategoryDrawer) WindowsTileDrawer(title = "Pilih Kategori Transaksi", subtitle = "Sistem Icon Grid ala Windows OS", categories = filteredCategories, selectedCategoryId = selectedCategoryId, isIncome = isIncome, onSelectCategory = { selectedCategoryId = it }, onDismiss = { showCategoryDrawer = false })
}
