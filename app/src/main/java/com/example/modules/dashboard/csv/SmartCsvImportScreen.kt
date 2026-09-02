package com.example.modules.dashboard.csv

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.csv.primitives.*
import com.example.shared.models.Category
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount
import com.example.shared.theme.DesignTokens

@Composable
fun SmartCsvImportScreen(
    wallets: List<WalletAccount>,
    categories: List<Category>,
    transactions: List<Transaction>,
    onExecuteImport: (List<ParsedTransaction>, Boolean) -> Unit,
    onBack: () -> Unit
) {
    var rawText by remember { mutableStateOf("") }
    var skipDuplicates by remember { mutableStateOf(true) }

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
                val importableCount = if (skipDuplicates) result.newCount else result.records.size

                item {
                    Button(
                        onClick = { onExecuteImport(result.records, skipDuplicates) },
                        enabled = importableCount > 0,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.EmeraldGlow)
                    ) {
                        Text(
                            "📥 IMPOR $importableCount TRANSAKSI SEKARANG",
                            color = DesignTokens.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daftar Transaksi (${result.records.size})", color = DesignTokens.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = skipDuplicates,
                                onCheckedChange = { skipDuplicates = it },
                                colors = CheckboxDefaults.colors(checkedColor = DesignTokens.EmeraldGlow)
                            )
                            Text("Skip Duplikat", color = DesignTokens.TextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                items(result.records) { record ->
                    CsvPreviewItemRow(item = record)
                }

                item {
                    Button(
                        onClick = { onExecuteImport(result.records, skipDuplicates) },
                        enabled = importableCount > 0,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DesignTokens.EmeraldGlow)
                    ) {
                        Text(
                            "📥 IMPOR $importableCount TRANSAKSI SEKARANG",
                            color = DesignTokens.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
