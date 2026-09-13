package com.example.shared.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object DesignTokens {
    val CobaltAccent = Color(0xFF2563EB)
    val CobaltMedium = Color(0xFF3B82F6)
    val AmberAccent = Color(0xFFD97706)
    val EmeraldGlow = Color(0xFF059669)
    val EmeraldAccent = Color(0xFF059669)
    val RoseAccent = Color(0xFFE11D48)
    val CrimsonAccent = Color(0xFFE11D48)
    val PurpleAccent = Color(0xFF8B5CF6)
    val PinkBlossom = Color(0xFFEC4899)
    val TextOnGradient = Color(0xFFFFFFFF)

    val RoleAccent: Color
        @Composable @ReadOnlyComposable
        get() = RoleTheme.current.primaryAccent

    val RoleSecondary: Color
        @Composable @ReadOnlyComposable
        get() = RoleTheme.current.secondaryAccent

    val RoleSoft: Color
        @Composable @ReadOnlyComposable
        get() = RoleTheme.current.primarySoft

    val RoleContainer: Color
        @Composable @ReadOnlyComposable
        get() = RoleTheme.current.primaryContainer

    val Background: Color
        @Composable @ReadOnlyComposable
        get() = RoleTheme.current.backgroundTint

    val BackgroundTop: Color
        @Composable @ReadOnlyComposable
        get() = RoleTheme.current.backgroundTint

    val BackgroundBottom: Color
        @Composable @ReadOnlyComposable
        get() = RoleTheme.current.backgroundTint

    val Surface: Color
        @Composable @ReadOnlyComposable
        get() = Color(0xFFFFFFFF)

    val SurfaceCard: Color
        @Composable @ReadOnlyComposable
        get() = Color(0xFFFFFFFF)

    val SurfaceGlass: Color
        @Composable @ReadOnlyComposable
        get() = Color(0xFFF8FAFC)

    val SurfaceElevated: Color
        @Composable @ReadOnlyComposable
        get() = RoleTheme.current.surfaceElevated

    val TextPrimary: Color
        @Composable @ReadOnlyComposable
        get() = Color(0xFF0F172A)

    val TextSecondary: Color
        @Composable @ReadOnlyComposable
        get() = Color(0xFF475569)

    val TextMuted: Color
        @Composable @ReadOnlyComposable
        get() = Color(0xFF64748B)

    val BorderLight: Color
        @Composable @ReadOnlyComposable
        get() = RoleTheme.current.cardBorder

    val BorderGlass: Color
        @Composable @ReadOnlyComposable
        get() = RoleTheme.current.cardBorder.copy(alpha = 0.5f)

    val PaddingSmall = 8.dp
    val PaddingMedium = 16.dp
    val PaddingLarge = 24.dp
    val CornerRadius = 24.dp
    val ElevationSoft = 8.dp
}

