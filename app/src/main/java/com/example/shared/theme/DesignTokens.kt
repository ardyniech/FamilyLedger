package com.example.shared.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object DesignTokens {
    val CobaltAccent = Color(0xFF2563EB)
    val CobaltDark = Color(0xFF3B82F6)
    val AmberAccent = Color(0xFFD97706)
    val EmeraldGlow = Color(0xFF059669)
    val EmeraldAccent = Color(0xFF059669)
    val RoseAccent = Color(0xFFE11D48)
    val CrimsonAccent = Color(0xFFE11D48)
    val PurpleAccent = Color(0xFF8B5CF6)
    val TextOnDark = Color(0xFFFFFFFF)

    val Background: Color
        @Composable @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFF0F172A) else Color(0xFFFFFBEB)

    val BackgroundTop: Color
        @Composable @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFF0B132B) else Color(0xFFF0F9FF)

    val BackgroundBottom: Color
        @Composable @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFF0F172A) else Color(0xFFFFFBEB)

    val Surface: Color
        @Composable @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFF1E293B) else Color(0xFFFFFFFF)

    val SurfaceCard: Color
        @Composable @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFF1E293B) else Color(0xFFFFFFFF)

    val SurfaceGlass: Color
        @Composable @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFF1E293B).copy(alpha = 0.85f) else Color(0xFFF8FAFC)

    val SurfaceElevated: Color
        @Composable @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFFFFFFF)

    val TextPrimary: Color
        @Composable @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFFF8FAFC) else Color(0xFF0F172A)

    val TextSecondary: Color
        @Composable @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFF94A3B8) else Color(0xFF475569)

    val TextMuted: Color
        @Composable @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFF64748B) else Color(0xFF64748B)

    val BorderLight: Color
        @Composable @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFE2E8F0)

    val BorderGlass: Color
        @Composable @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) Color(0xFF334155).copy(alpha = 0.6f) else Color(0xFFE2E8F0)

    val PaddingSmall = 8.dp
    val PaddingMedium = 16.dp
    val PaddingLarge = 24.dp
    val CornerRadius = 24.dp
    val ElevationSoft = 8.dp
}

