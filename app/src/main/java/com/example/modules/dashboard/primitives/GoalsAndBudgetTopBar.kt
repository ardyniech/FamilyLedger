package com.example.modules.dashboard.primitives

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.shared.models.Member
import com.example.shared.theme.DesignTokens
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsAndBudgetTopBar(
    members: List<Member>,
    monthlyBudget: Long,
    totalExpenses: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    TopAppBar(
        title = { Text("Rencana & Anggaran", fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = DesignTokens.TextPrimary)
            }
        },
        actions = {
            IconButton(onClick = {
                val husband = members.find { it.role == "Husband" }?.name ?: "Suami"
                val wife = members.find { it.role == "Wife" }?.name ?: "Istri"
                val sisa = (monthlyBudget - totalExpenses).coerceAtLeast(0L)
                val report = "🕊️ *EVALUASI ANGGARAN & IMPIAN KELUARGA*\nPasangan: $husband & $wife\nBatas Anggaran: ${formatter.format(monthlyBudget)}\nPengeluaran: ${formatter.format(totalExpenses)}\nSisa Anggaran: ${formatter.format(sisa)}\n\n_Dibuat dengan Family Ledgers_"
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Rangkuman", report))
                Toast.makeText(context, "Rangkuman anggaran disalin untuk pasangan! 🕊️", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Default.Share, contentDescription = "Bagikan", tint = DesignTokens.CobaltAccent)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}
