package com.example.modules.dashboard.csv

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.shared.models.HouseholdExpense
import com.example.shared.utils.MathUtils
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object HouseholdExpensePdfExporter {

    fun generatePdf(context: Context, expenses: List<HouseholdExpense>): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paintTitle = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 18f
            isFakeBoldText = true
        }
        val paintSubtitle = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 10f
        }
        val paintText = Paint().apply {
            color = Color.parseColor("#334155")
            textSize = 9f
        }
        val paintBold = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 9f
            isFakeBoldText = true
        }

        var y = 40f
        canvas.drawText("Laporan Pengeluaran Rumah Tangga", 40f, y, paintTitle)
        y += 18f
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        canvas.drawText("Dicetak: ${sdf.format(Date())} | Total Item: ${expenses.size}", 40f, y, paintSubtitle)
        y += 24f

        val totalAmount = expenses.sumOf { it.amount }
        canvas.drawText("Total Pengeluaran: ${MathUtils.formatRupiah(totalAmount)}", 40f, y, paintBold)
        y += 20f

        val linePaint = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            strokeWidth = 1f
        }
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 16f

        // Table Header
        canvas.drawText("Item / Pos Belanja", 40f, y, paintBold)
        canvas.drawText("Kategori", 220f, y, paintBold)
        canvas.drawText("Tipe", 340f, y, paintBold)
        canvas.drawText("Nominal", 460f, y, paintBold)
        y += 8f
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 16f

        // Table Body
        for (item in expenses.take(30)) {
            if (y > 800f) break
            val truncatedTitle = if (item.title.length > 25) item.title.take(22) + "..." else item.title
            val truncatedCategory = if (item.categoryName.length > 18) item.categoryName.take(15) + "..." else item.categoryName
            val typeStr = if (item.isNeed) "Kebutuhan" else "Keinginan"

            canvas.drawText(truncatedTitle, 40f, y, paintText)
            canvas.drawText(truncatedCategory, 220f, y, paintText)
            canvas.drawText(typeStr, 340f, y, paintText)
            canvas.drawText(MathUtils.formatRupiah(item.amount), 460f, y, paintText)
            y += 16f
        }

        document.finishPage(page)

        val dir = File(context.cacheDir, "reports").apply { mkdirs() }
        val outputFile = File(dir, "laporan_pengeluaran_${System.currentTimeMillis()}.pdf")
        FileOutputStream(outputFile).use { out ->
            document.writeTo(out)
        }
        document.close()
        return outputFile
    }
}
