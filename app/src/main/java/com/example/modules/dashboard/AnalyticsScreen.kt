package com.example.modules.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.dialogs.CategoryTransactionsDialog
import com.example.modules.dashboard.dialogs.MemberTransactionsDialog
import com.example.modules.dashboard.primitives.CategoryBreakdownReportCard
import com.example.modules.dashboard.primitives.CategorySpendingTrendChart
import com.example.modules.dashboard.primitives.MemberExpenseCard
import com.example.shared.models.*
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    transactions: List<Transaction>,
    categories: List<Category>,
    members: List<Member>,
    onTransactionClick: (Transaction) -> Unit = {},
    onBack: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    var selectedCategoryForDetail by remember { mutableStateOf<Category?>(null) }
    var selectedMemberForDetail by remember { mutableStateOf<Member?>(null) }
    val memberExpenses = remember(transactions, members, categories) {
        val expenses = transactions.filter { t -> t.amount < 0 && categories.find { it.id == t.categoryId }?.type == "Expense" }
        members.associateWith { m -> expenses.filter { it.memberId == m.id }.sumOf { -it.amount } }
    }
    val totalHouseholdExpense = memberExpenses.values.sum()
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) { animProgress.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Transparansi", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DesignTokens.TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceGlass), border = BorderStroke(1.dp, DesignTokens.BorderGlass), modifier = Modifier.fillMaxWidth()) {
                Text(
                    "💡 Membangun keharmonisan melalui keterbukaan finansial. Halaman ini meringkas proporsi pengeluaran rumah tangga agar suami & istri selaras.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DesignTokens.TextSecondary,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Card(colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface), border = BorderStroke(1.dp, DesignTokens.BorderGlass), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Pengeluaran Bulan Ini", color = DesignTokens.TextSecondary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(formatter.format(totalHouseholdExpense), color = Color.Red, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Text("Proporsi Pengeluaran Pasangan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DesignTokens.TextPrimary)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                members.forEachIndexed { index, member ->
                    MemberExpenseCard(
                        member = member,
                        expense = memberExpenses[member] ?: 0L,
                        totalExpense = totalHouseholdExpense,
                        accentColor = if (index == 0) DesignTokens.CobaltAccent else DesignTokens.AmberAccent,
                        animProgress = animProgress.value,
                        onClick = { selectedMemberForDetail = member },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            CategorySpendingTrendChart(transactions = transactions, categories = categories)

            CategoryBreakdownReportCard(
                transactions = transactions,
                categories = categories,
                totalExpenses = totalHouseholdExpense,
                onCategoryClick = { selectedCategoryForDetail = it }
            )
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    selectedCategoryForDetail?.let { cat ->
        CategoryTransactionsDialog(category = cat, transactions = transactions, members = members, onTransactionClick = onTransactionClick, onDismiss = { selectedCategoryForDetail = null })
    }

    selectedMemberForDetail?.let { mem ->
        MemberTransactionsDialog(member = mem, transactions = transactions, categories = categories, onTransactionClick = onTransactionClick, onDismiss = { selectedMemberForDetail = null })
    }
}
