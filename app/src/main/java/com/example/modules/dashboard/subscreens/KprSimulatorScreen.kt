package com.example.modules.dashboard.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.modules.dashboard.logic.KprFloatingRateCalculator
import com.example.modules.dashboard.primitives.KprAmortizationVisualizerCard
import com.example.modules.dashboard.primitives.KprSimulatorComparisonCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KprSimulatorScreen(
    initialPrincipal: Long = 500_000_000L,
    initialFixedRate: Float = 5.5f,
    initialFloatingRate: Float = 11.5f,
    initialTenorMonths: Int = 180,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var principal by remember { mutableStateOf(initialPrincipal) }
    var fixedRate by remember { mutableFloatStateOf(initialFixedRate) }
    var floatingRate by remember { mutableFloatStateOf(initialFloatingRate) }
    var tenorMonths by remember { mutableIntStateOf(initialTenorMonths) }

    val analysis = remember(principal, fixedRate, floatingRate, tenorMonths) {
        KprFloatingRateCalculator.analyze(
            loanName = "Simulasi KPR",
            remainingPrincipal = principal,
            fixedRatePct = fixedRate,
            floatingRatePct = floatingRate,
            remainingTenorMonths = tenorMonths,
            monthsUntilFloating = 3
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simulasi Suku Bunga KPR") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KprSimulatorComparisonCard(analysis = analysis)
            KprAmortizationVisualizerCard(
                fixedInstallment = analysis.fixedMonthlyInstallment,
                floatingInstallment = analysis.floatingMonthlyInstallment
            )
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Suku Bunga Floating: ${String.format("%.1f", floatingRate)}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(
                        value = floatingRate,
                        onValueChange = { floatingRate = it },
                        valueRange = 5.0f..18.0f,
                        steps = 25
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Suku Bunga Fixed Asal: ${String.format("%.1f", fixedRate)}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(
                        value = fixedRate,
                        onValueChange = { fixedRate = it },
                        valueRange = 3.0f..10.0f,
                        steps = 14
                    )
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Rekomendasi Cashflow & Mitigasi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(analysis.recommendationText, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
