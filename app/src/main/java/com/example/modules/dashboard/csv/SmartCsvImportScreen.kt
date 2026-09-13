package com.example.modules.dashboard.csv

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.modules.dashboard.csv.primitives.*
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount

@Composable
fun SmartCsvImportScreen(
    wallets: List<WalletAccount>,
    categories: List<Category>,
    transactions: List<Transaction>,
    onExecuteImport: (List<ParsedTransaction>, Boolean, Boolean) -> Unit,
    onBack: () -> Unit
) {
    var rawText by remember { mutableStateOf("") }
    var skipDuplicates by remember { mutableStateOf(true) }
    var clearDatabaseFirst by remember { mutableStateOf(false) }

    val parseResult = remember(rawText, wallets, categories, transactions) {
        if (rawText.isNotBlank()) {
            SmartCsvParser.parse(rawText, wallets, categories, transactions)
        } else null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            CsvHeaderActionBar(
                onBack = onBack,
                onReset = { rawText = "" },
                canReset = rawText.isNotBlank()
            )
        }

        item {
            CsvInputSection(
                rawText = rawText,
                onTextChange = { rawText = it },
                onSelectTemplate = { tpl -> rawText = tpl }
            )
        }

        parseResult?.let { result ->
            item { CsvPreviewCard(result = result) }

            if (result.records.isNotEmpty()) {
                val importableCount = if (skipDuplicates && !clearDatabaseFirst) result.newCount else result.records.size

                item {
                    CsvImportControlBar(
                        importableCount = importableCount,
                        totalRecords = result.records.size,
                        clearDatabaseFirst = clearDatabaseFirst,
                        onExecuteImport = { onExecuteImport(result.records, skipDuplicates, clearDatabaseFirst) }
                    )
                }

                item {
                    CsvImportOptionsSection(
                        recordCount = result.records.size,
                        clearDatabaseFirst = clearDatabaseFirst,
                        onClearDatabaseFirstChange = { clearDatabaseFirst = it },
                        skipDuplicates = skipDuplicates,
                        onSkipDuplicatesChange = { skipDuplicates = it }
                    )
                }

                items(result.records) { record ->
                    CsvPreviewItemRow(item = record)
                }

                item {
                    CsvImportControlBar(
                        importableCount = importableCount,
                        totalRecords = result.records.size,
                        clearDatabaseFirst = clearDatabaseFirst,
                        onExecuteImport = { onExecuteImport(result.records, skipDuplicates, clearDatabaseFirst) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
