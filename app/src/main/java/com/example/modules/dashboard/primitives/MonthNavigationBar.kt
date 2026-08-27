package com.example.modules.dashboard.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun MonthNavigationBar(
    currentMonthOffset: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetToCurrent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val monthCal = Calendar.getInstance().apply {
        add(Calendar.MONTH, currentMonthOffset)
    }
    val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
    val displayMonthName = monthFormatter.format(monthCal.time)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DesignTokens.BorderGlass)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DesignTokens.SurfaceGlass)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Bulan Sebelumnya",
                    tint = DesignTokens.TextPrimary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable { if (currentMonthOffset != 0) onResetToCurrent() }
            ) {
                Text(
                    text = displayMonthName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DesignTokens.TextPrimary
                )
                if (currentMonthOffset != 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DesignTokens.CobaltAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Bulan Ini", fontSize = 10.sp, color = DesignTokens.CobaltAccent, fontWeight = FontWeight.Bold)
                    }
                }
            }

            IconButton(
                onClick = onNextMonth,
                enabled = currentMonthOffset < 12,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DesignTokens.SurfaceGlass)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Bulan Berikutnya",
                    tint = if (currentMonthOffset < 12) DesignTokens.TextPrimary else DesignTokens.TextSecondary.copy(alpha = 0.3f)
                )
            }
        }
    }
}
