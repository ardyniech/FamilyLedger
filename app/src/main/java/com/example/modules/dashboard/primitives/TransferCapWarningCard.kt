package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modules.dashboard.logic.TransferCapEvaluation
import com.example.modules.dashboard.logic.TransferCapStatus
import com.example.shared.theme.DesignTokens

@Composable
fun TransferCapWarningCard(evaluation: TransferCapEvaluation?) {
    if (evaluation == null || evaluation.warningMessage == null) return

    val isExceeded = evaluation.status == TransferCapStatus.EXCEEDED
    val bgColor = if (isExceeded) DesignTokens.CrimsonAccent.copy(alpha = 0.15f) else DesignTokens.AmberAccent.copy(alpha = 0.15f)
    val textColor = if (isExceeded) DesignTokens.CrimsonAccent else DesignTokens.AmberAccent

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = if (isExceeded) "⚠️ MELEBIHI PLAFON BULANAN" else "⚡ MENDEKATI PLAFON BULANAN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(evaluation.warningMessage, fontSize = 12.sp, color = DesignTokens.TextPrimary)
            LinearProgressIndicator(
                progress = { (evaluation.percentageUsed / 100.0).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = textColor,
                trackColor = DesignTokens.SurfaceGlass
            )
        }
    }
}
