package com.example.modules.dashboard.primitives

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.modules.dashboard.logic.FabPosition
import com.example.shared.theme.DesignTokens

@Composable
fun FloatingAddTransactionButton(
    fabPosition: FabPosition,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(stiffness = 400f, dampingRatio = 0.6f),
        label = "fab_press_scale"
    )

    val alignment = when (fabPosition) {
        FabPosition.RIGHT -> Alignment.BottomEnd
        FabPosition.LEFT -> Alignment.BottomStart
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = alignment
    ) {
        FloatingActionButton(
            onClick = onClick,
            interactionSource = interactionSource,
            shape = CircleShape,
            containerColor = DesignTokens.CobaltAccent,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 14.dp),
            modifier = Modifier
                .size(60.dp)
                .scale(scale)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah Transaksi Baru (One-Hand)",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
