package com.example.modules.dashboard.subscreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.CategoryGroupCalculator
import com.example.modules.dashboard.logic.GroupSpendingSummary
import com.example.modules.dashboard.primitives.CategoryGroupPieChart
import com.example.shared.models.Category
import com.example.shared.models.CategoryGroup
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryGroupDashboardScreen(
    transactions: List<Transaction>,
    categories: List<Category>,
    groups: List<CategoryGroup>,
    onBackClick: () -> Unit,
    onManageGroupsClick: () -> Unit
) {
    var isExpenseMode by remember { mutableStateOf(true) }
    val summaries = remember(transactions, categories, groups) {
        CategoryGroupCalculator.calculate(transactions, categories, groups)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grup Kategori & Tag", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onManageGroupsClick) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah / Kelola Grup")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DesignTokens.Background)
            )
        },
        containerColor = DesignTokens.Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DesignTokens.SurfaceGlass).padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { isExpenseMode = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isExpenseMode) DesignTokens.CobaltAccent else Color.Transparent),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("% dari Expense", fontSize = 12.sp, color = if (isExpenseMode) Color.White else DesignTokens.TextSecondary) }
                    Button(
                        onClick = { isExpenseMode = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (!isExpenseMode) DesignTokens.EmeraldAccent else Color.Transparent),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("% dari Income", fontSize = 12.sp, color = if (!isExpenseMode) Color.White else DesignTokens.TextSecondary) }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DesignTokens.SurfaceCard)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CategoryGroupPieChart(summaries = summaries, isExpenseMode = isExpenseMode)
                        Text(
                            text = if (isExpenseMode) "Distribusi Pengeluaran per Grup" else "Distribusi Pemasukan per Grup",
                            fontSize = 12.sp,
                            color = DesignTokens.TextSecondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            items(summaries) { summary ->
                CategoryGroupRowItem(summary = summary, isExpenseMode = isExpenseMode)
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
