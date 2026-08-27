package com.example.modules.dashboard.management

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.models.Category
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    categories: List<Category>,
    onSaveCategory: (id: String?, name: String, type: String, budgetLimit: Double) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editCategory by remember { mutableStateOf<Category?>(null) }
    var selectedType by remember { mutableStateOf("Expense") }
    var searchQuery by remember { mutableStateOf("") }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    // Categories filtered by Type and Search Query
    val filteredCategories = remember(categories, selectedType, searchQuery) {
        categories.filter { cat ->
            cat.type == selectedType &&
            !cat.isDeleted &&
            cat.name.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Kelola Kategori Ledger", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp,
                        color = DesignTokens.TextPrimary 
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Kembali ke Dashboard",
                            tint = DesignTokens.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = DesignTokens.CobaltAccent,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_category_fab")
            ) {
                Icon(Icons.Default.Add, "Tambah Kategori")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = DesignTokens.PaddingMedium)
        ) {
            
            // 1. Premium Expense / Income Type Segmented Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(DesignTokens.CornerRadius))
                    .background(DesignTokens.SurfaceGlass)
                    .padding(4.dp)
            ) {
                listOf("Expense", "Income").forEach { type ->
                    val isSelected = selectedType == type
                    val label = if (type == "Expense") "Pengeluaran" else "Pemasukan"
                    val activeBgColor = if (type == "Expense") DesignTokens.RoseAccent.copy(alpha = 0.85f) else DesignTokens.EmeraldGlow.copy(alpha = 0.85f)
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(DesignTokens.CornerRadius - 4.dp))
                            .background(if (isSelected) activeBgColor else Color.Transparent)
                            .clickable { selectedType = type }
                            .padding(vertical = 10.dp)
                            .testTag("tab_toggle_$type"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label, 
                            color = if (isSelected) Color.White else DesignTokens.TextSecondary, 
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            // 2. Modern Interactive Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari kategori...", fontSize = 13.sp, color = DesignTokens.TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari", modifier = Modifier.size(20.dp), tint = DesignTokens.TextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DesignTokens.CobaltAccent,
                    unfocusedBorderColor = DesignTokens.BorderGlass,
                    focusedContainerColor = DesignTokens.SurfaceGlass,
                    unfocusedContainerColor = DesignTokens.SurfaceGlass
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("category_search_input")
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // 3. Category List
            if (filteredCategories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Tidak Ada Kategori",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DesignTokens.TextPrimary
                        )
                        Text(
                            text = if (searchQuery.isEmpty()) "Tambahkan kategori keluarga baru dengan menekan tombol + di kanan bawah." else "Kategori dengan kata kunci '$searchQuery' tidak ditemukan.",
                            fontSize = 12.sp,
                            color = DesignTokens.TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredCategories, key = { it.id }) { cat ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
                            border = BorderStroke(1.dp, DesignTokens.BorderGlass),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { editCategory = cat }
                                .testTag("category_card_${cat.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), 
                                horizontalArrangement = Arrangement.SpaceBetween, 
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Visual color prefix badge
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (cat.type == "Expense") DesignTokens.RoseAccent.copy(alpha = 0.15f)
                                                else DesignTokens.EmeraldGlow.copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val firstLetter = cat.name.take(1).uppercase()
                                        Text(
                                            text = firstLetter,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (cat.type == "Expense") DesignTokens.RoseAccent else DesignTokens.EmeraldGlow
                                        )
                                    }
                                    
                                    Column {
                                        Text(
                                            text = cat.name, 
                                            fontWeight = FontWeight.SemiBold, 
                                            fontSize = 14.sp,
                                            color = DesignTokens.TextPrimary
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = if (cat.type == "Expense") "Keluaran" else "Pemasukan",
                                                fontSize = 11.sp,
                                                color = DesignTokens.TextSecondary
                                            )
                                            if (cat.type == "Expense" && cat.budgetLimit > 0) {
                                                val fmt = remember { java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID")) }
                                                Text(
                                                    text = "• Batas: ${fmt.format(cat.budgetLimit)}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DesignTokens.AmberAccent
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { editCategory = cat },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit, 
                                            contentDescription = "Edit Kategori", 
                                            tint = DesignTokens.TextSecondary, 
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { categoryToDelete = cat },
                                        modifier = Modifier.size(32.dp).testTag("delete_category_icon_${cat.name}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline, 
                                            contentDescription = "Hapus Kategori", 
                                            tint = DesignTokens.RoseAccent, 
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // 4. Create or Edit Category Modal
    if (showAddDialog || editCategory != null) {
        val isEdit = editCategory != null
        var name by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("") }
        var budgetLimitStr by remember { mutableStateOf("") }

        LaunchedEffect(showAddDialog, editCategory) {
            if (editCategory != null) {
                name = editCategory!!.name
                type = editCategory!!.type
                budgetLimitStr = if (editCategory!!.budgetLimit > 0) editCategory!!.budgetLimit.toInt().toString() else ""
            } else {
                name = ""
                type = selectedType
                budgetLimitStr = ""
            }
        }
        
        AlertDialog(
            onDismissRequest = { showAddDialog = false; editCategory = null },
            shape = RoundedCornerShape(DesignTokens.CornerRadius),
            containerColor = DesignTokens.Surface,
            title = { 
                Text(
                    text = if (isEdit) "Ubah Kategori" else "Tambah Kategori Baru",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DesignTokens.TextPrimary
                ) 
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Kategori", fontSize = 12.sp) },
                        placeholder = { Text("misal: Transportasi, Belanja Bulanan") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DesignTokens.CobaltAccent,
                            unfocusedBorderColor = DesignTokens.BorderGlass
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("category_name_field")
                    )
                    
                    if (!isEdit) {
                        Text("Tipe Kategori:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextSecondary)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("Expense", "Income").forEach { t ->
                                val label = if (t == "Expense") "Pengeluaran" else "Pemasukan"
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { type = t }
                                ) {
                                    RadioButton(
                                        selected = type == t, 
                                        onClick = { type = t },
                                        colors = RadioButtonDefaults.colors(selectedColor = DesignTokens.CobaltAccent)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(label, fontSize = 13.sp, color = DesignTokens.TextPrimary)
                                }
                            }
                        }
                    }

                    if (type == "Expense") {
                        OutlinedTextField(
                            value = budgetLimitStr,
                            onValueChange = { budgetLimitStr = it.filter { char -> char.isDigit() } },
                            label = { Text("Batas Anggaran Bulanan (Opsional)", fontSize = 12.sp) },
                            placeholder = { Text("misal: 1500000") },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            prefix = { Text("Rp ", fontSize = 13.sp, color = DesignTokens.TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DesignTokens.CobaltAccent,
                                unfocusedBorderColor = DesignTokens.BorderGlass
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("category_budget_limit_field")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val limit = budgetLimitStr.toDoubleOrNull() ?: 0.0
                            onSaveCategory(editCategory?.id, name, type, limit)
                            showAddDialog = false
                            editCategory = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.CobaltAccent),
                    modifier = Modifier.testTag("save_category_button")
                ) { 
                    Text("Simpan") 
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddDialog = false; editCategory = null }
                ) { 
                    Text("Batal", color = DesignTokens.TextSecondary) 
                }
            }
        )
    }

    // 5. Delete Confirmation Modal
    categoryToDelete?.let { cat ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            shape = RoundedCornerShape(DesignTokens.CornerRadius),
            containerColor = DesignTokens.Surface,
            title = {
                Text(
                    text = "Hapus Kategori?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DesignTokens.RoseAccent
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus kategori '${cat.name}'? Langkah ini akan menyembunyikan kategori dari daftar input transaksi baru.",
                    fontSize = 13.sp,
                    color = DesignTokens.TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCategory(cat)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.RoseAccent),
                    modifier = Modifier.testTag("confirm_delete_category_btn")
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { categoryToDelete = null }
                ) {
                    Text("Batal", color = DesignTokens.TextSecondary)
                }
            }
        )
    }
}
