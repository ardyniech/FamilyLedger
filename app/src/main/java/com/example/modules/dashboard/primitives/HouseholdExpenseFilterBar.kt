package com.example.modules.dashboard.primitives

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdExpenseFilterBar(
    selectedNeedFilter: Boolean?, // null = all, true = need only, false = want only
    onSelectNeedFilter: (Boolean?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedNeedFilter == null,
            onClick = { onSelectNeedFilter(null) },
            label = { Text("Semua Pengeluaran") }
        )
        FilterChip(
            selected = selectedNeedFilter == true,
            onClick = { onSelectNeedFilter(true) },
            label = { Text("Kebutuhan (Need)") }
        )
        FilterChip(
            selected = selectedNeedFilter == false,
            onClick = { onSelectNeedFilter(false) },
            label = { Text("Keinginan (Want)") }
        )
    }
}
