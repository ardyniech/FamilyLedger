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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared.models.Category
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    categories: List<Category>,
    onSaveCategory: (id: String?, name: String, type: String) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editCategory by remember { mutableStateOf<Category?>(null) }
    var selectedType by remember { mutableStateOf("Expense") }
    
    val filteredCategories = remember(categories, selectedType) {
        categories.filter { it.type == selectedType }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Add Category")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = DesignTokens.PaddingMedium)) {
            // Type Toggle
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(DesignTokens.SurfaceGlass).padding(4.dp)) {
                listOf("Expense", "Income").forEach { type ->
                    val isSelected = selectedType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) if (type == "Income") DesignTokens.EmeraldGlow else Color.Red.copy(alpha = 0.8f) else Color.Transparent)
                            .clickable { selectedType = type }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(type, color = if (isSelected) Color.White else DesignTokens.TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredCategories) { cat ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass),
                        modifier = Modifier.fillMaxWidth().clickable { editCategory = cat }
                    ) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(cat.name, fontWeight = FontWeight.SemiBold, color = DesignTokens.TextPrimary)
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DesignTokens.TextSecondary, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
    
    if (showAddDialog || editCategory != null) {
        val isEdit = editCategory != null
        var name by remember { mutableStateOf(editCategory?.name ?: "") }
        var type by remember { mutableStateOf(editCategory?.type ?: selectedType) }
        
        AlertDialog(
            onDismissRequest = { showAddDialog = false; editCategory = null },
            title = { Text(if (isEdit) "Edit Category" else "Add Category") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Category Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (!isEdit) {
                        Text("Type:")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Expense", "Income").forEach { t ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = type == t, onClick = { type = t })
                                    Text(t)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) {
                        onSaveCategory(editCategory?.id, name, type)
                        showAddDialog = false
                        editCategory = null
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false; editCategory = null }) { Text("Cancel") }
            }
        )
    }
}
