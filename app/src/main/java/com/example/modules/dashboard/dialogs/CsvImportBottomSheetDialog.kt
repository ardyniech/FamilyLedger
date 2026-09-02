package com.example.modules.dashboard.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.csv.*
import com.example.modules.dashboard.csv.primitives.CsvFilePickerCard
import com.example.modules.dashboard.csv.primitives.CsvPreviewItemRow
import com.example.modules.dashboard.csv.primitives.CsvStructureValidationCard
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsvImportBottomSheetDialog(
    wallets: List<WalletAccount>,
    categories: List<Category>,
    transactions: List<Transaction>,
    onExecuteImport: (List<ParsedTransaction>, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var fileName by remember { mutableStateOf<String?>(null) }
    var fileSizeText by remember { mutableStateOf<String?>(null) }
    var rawText by remember { mutableStateOf("") }
    var skipDuplicates by remember { mutableStateOf(true) }

    val parseResult = remember(rawText, wallets, categories, transactions) {
        if (rawText.isNotBlank()) SmartCsvParser.parse(rawText, wallets, categories, transactions) else null
    }
    val lineCount = remember(rawText) { if (rawText.isBlank()) 0 else rawText.lines().filter { it.isNotBlank() }.size }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DesignTokens.BackgroundBottom,
        dragHandle = {
            Surface(modifier = Modifier.padding(top = 8.dp), color = DesignTokens.BorderLight, shape = RoundedCornerShape(4.dp)) {
                Box(modifier = Modifier.size(36.dp, 4.dp))
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Impor Data CSV 📄", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    Text("Pilih file lokal, validasi struktur & impor transaksi", fontSize = 12.sp, color = DesignTokens.TextSecondary)
                }
                IconButton(onClick = onDismiss) { Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = DesignTokens.TextSecondary) }
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().weight(1f)) {
                item {
                    CsvFilePickerCard(
                        fileName = fileName, fileSizeText = fileSizeText, lineCount = lineCount,
                        onFileLoaded = { n, s, t -> fileName = n; fileSizeText = s; rawText = t },
                        onClearFile = { fileName = null; fileSizeText = null; rawText = "" }
                    )
                }

                parseResult?.let { result ->
                    item { CsvStructureValidationCard(result = result, skipDuplicates = skipDuplicates, onToggleSkipDuplicates = { skipDuplicates = it }) }

                    if (result.records.isNotEmpty()) {
                        val countToImport = if (skipDuplicates) result.newCount else result.records.size

                        item {
                            Button(
                                onClick = { onExecuteImport(result.records, skipDuplicates); onDismiss() },
                                enabled = countToImport > 0,
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.EmeraldGlow)
                            ) {
                                Icon(Icons.Filled.CloudUpload, contentDescription = "Import", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("EKSEKUSI IMPOR $countToImport TRANSAKSI", color = DesignTokens.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        item { Text("Preview Data Transaksi (${result.records.size} Items)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) }

                        items(result.records) { record -> CsvPreviewItemRow(item = record) }
                    }
                }
            }
        }
    }
}
