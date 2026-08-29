package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.theme.DesignTokens
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerRow(
    selectedTimestamp: Long,
    onTimestampChanged: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val calendar = remember(selectedTimestamp) { Calendar.getInstance().apply { timeInMillis = selectedTimestamp } }
    val dateFmt = remember { SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID")) }
    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale("id", "ID")) }

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedCard(
            onClick = { showDatePicker = true }, modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(DesignTokens.CornerRadius), colors = CardDefaults.outlinedCardColors(containerColor = DesignTokens.SurfaceGlass),
            border = BorderStroke(1.dp, DesignTokens.BorderGlass)
        ) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Pilih Tanggal", tint = DesignTokens.CobaltAccent, modifier = Modifier.size(18.dp))
                Column {
                    Text("Tanggal Transaksi", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text(dateFmt.format(calendar.time), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                }
            }
        }

        OutlinedCard(
            onClick = { showTimePicker = true }, modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(DesignTokens.CornerRadius), colors = CardDefaults.outlinedCardColors(containerColor = DesignTokens.SurfaceGlass),
            border = BorderStroke(1.dp, DesignTokens.BorderGlass)
        ) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Schedule, contentDescription = "Pilih Jam", tint = DesignTokens.AmberAccent, modifier = Modifier.size(18.dp))
                Column {
                    Text("Jam (Analog)", fontSize = 10.sp, color = DesignTokens.TextSecondary)
                    Text(timeFmt.format(calendar.time), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sel = Calendar.getInstance().apply { timeInMillis = millis }
                        val upd = Calendar.getInstance().apply {
                            timeInMillis = selectedTimestamp
                            set(Calendar.YEAR, sel.get(Calendar.YEAR))
                            set(Calendar.MONTH, sel.get(Calendar.MONTH))
                            set(Calendar.DAY_OF_MONTH, sel.get(Calendar.DAY_OF_MONTH))
                        }
                        onTimestampChanged(upd.timeInMillis)
                    }
                    showDatePicker = false
                }) { Text("OK", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent) }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Batal", color = DesignTokens.TextSecondary) } }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(initialHour = calendar.get(Calendar.HOUR_OF_DAY), initialMinute = calendar.get(Calendar.MINUTE), is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val upd = Calendar.getInstance().apply {
                        timeInMillis = selectedTimestamp
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                        set(Calendar.SECOND, 0)
                    }
                    onTimestampChanged(upd.timeInMillis)
                    showTimePicker = false
                }) { Text("OK", fontWeight = FontWeight.Bold, color = DesignTokens.CobaltAccent) }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Batal", color = DesignTokens.TextSecondary) } },
            title = { Text("Atur Waktu Transaksi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DesignTokens.TextPrimary) },
            text = { Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { TimePicker(state = timePickerState) } }
        )
    }
}
