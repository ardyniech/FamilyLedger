package com.example.shared.atoms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.example.shared.theme.DesignTokens

@Composable
fun AnimatedMeshBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(DesignTokens.BackgroundTop, DesignTokens.BackgroundBottom),
                center = Offset(size.width * 0.5f, size.height * 0.25f),
                radius = size.width * 1.2f
            ),
            size = size
        )
    }
}


