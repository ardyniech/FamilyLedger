package com.example.modules.dashboard.management

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    categories: List<Category>,
    onSaveCategory: (id: String?, name: String, type: String, budgetLimit: Long) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editCategory by remember { mutableStateOf<Category?>(null) }
    var selectedType by remember { mutableStateOf("Expense") }
    var searchQuery by remember { mutableStateOf("") }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    val filteredCategories = remember(categories, selectedType, searchQuery) {
        categories.filter { it.type == selectedType && !it.isDeleted && it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Kategori Ledger", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DesignTokens.TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali ke Dashboard", tint = DesignTokens.TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = DesignTokens.CobaltAccent, contentColor = Color.White, modifier = Modifier.testTag("add_category_fab")) {
                Icon(Icons.Default.Add, "Tambah Kategori")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = DesignTokens.PaddingMedium)) {
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(DesignTokens.CornerRadius)).background(DesignTokens.SurfaceGlass).padding(4.dp)) {
                listOf("Expense", "Income").forEach { type ->
                    val isSelected = selectedType == type
                    val activeBg = if (type == "Expense") DesignTokens.RoseAccent.copy(alpha = 0.85f) else DesignTokens.EmeraldGlow.copy(alpha = 0.85f)
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(DesignTokens.CornerRadius - 4.dp)).background(if (isSelected) activeBg else Color.Transparent).clickable { selectedType = type }.padding(vertical = 10.dp).testTag("tab_toggle_$type"),
                        contentAlignment = Alignment.Center
                    ) { Text(if (type == "Expense") "Pengeluaran" else "Pemasukan", color = if (isSelected) Color.White else DesignTokens.TextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it }, placeholder = { Text("Cari kategori...", fontSize = 13.sp, color = DesignTokens.TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, "Cari", modifier = Modifier.size(20.dp), tint = DesignTokens.TextSecondary) }, singleLine = true, shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DesignTokens.CobaltAccent, unfocusedBorderColor = DesignTokens.BorderGlass, focusedContainerColor = DesignTokens.SurfaceGlass, unfocusedContainerColor = DesignTokens.SurfaceGlass),
                modifier = Modifier.fillMaxWidth().testTag("category_search_input")
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (filteredCategories.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Tidak Ada Kategori", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DesignTokens.TextPrimary)
                        Text(if (searchQuery.isEmpty()) "Tambahkan kategori keluarga baru dengan menekan tombol + di kanan bawah." else "Kategori dengan kata kunci '$searchQuery' tidak ditemukan.", fontSize = 12.sp, color = DesignTokens.TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                    items(filteredCategories, key = { it.id }) { cat ->
                        CategoryListItem(cat = cat, onEdit = { editCategory = cat }, onDelete = { categoryToDelete = cat })
                    }
                }
            }
        }
    }
    if (showAddDialog || editCategory != null) {
        CategoryFormDialog(editCategory = editCategory, defaultType = selectedType, onDismiss = { showAddDialog = false; editCategory = null }, onSave = { id, n, t, b -> onSaveCategory(id, n, t, b); showAddDialog = false; editCategory = null })
    }
    categoryToDelete?.let { cat ->
        CategoryDeleteDialog(category = cat, onDismiss = { categoryToDelete = null }, onConfirm = { onDeleteCategory(cat); categoryToDelete = null })
    }
}
