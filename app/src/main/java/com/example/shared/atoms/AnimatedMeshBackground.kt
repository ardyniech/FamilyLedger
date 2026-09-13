package com.example.shared.atoms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.shared.theme.DesignTokens

@Composable
fun AnimatedMeshBackground(modifier: Modifier = Modifier) {
    val bgTop = DesignTokens.BackgroundTop
    val bgBottom = DesignTokens.BackgroundBottom
    
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_cheerful_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.85f,
            modifier = Modifier.fillMaxSize()
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(bgTop.copy(alpha = 0.05f), bgBottom.copy(alpha = 0.2f)),
                    center = Offset(size.width * 0.5f, size.height * 0.2f),
                    radius = size.width * 1.3f
                ),
                size = size
            )
        }
    }
}



