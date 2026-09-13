package com.example.modules.dashboard.primitives

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import com.example.modules.dashboard.csv.HouseholdExpenseCsvExporter
import com.example.modules.dashboard.csv.HouseholdExpensePdfExporter
import com.example.shared.models.HouseholdExpense

@Composable
fun HouseholdExpenseExportActions(
    context: Context,
    expenses: List<HouseholdExpense>
) {
    IconButton(onClick = {
        val csv = HouseholdExpenseCsvExporter.toCsv(expenses)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, csv)
            type = "text/csv"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Ekspor CSV Pengeluaran"))
    }) {
        Icon(Icons.Default.Share, contentDescription = "Ekspor CSV")
    }

    IconButton(onClick = {
        try {
            val file = HouseholdExpensePdfExporter.generatePdf(context, expenses)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "application/pdf"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(sendIntent, "Ekspor PDF Pengeluaran"))
        } catch (_: Exception) {}
    }) {
        Icon(Icons.Default.PictureAsPdf, contentDescription = "Ekspor PDF")
    }
}
