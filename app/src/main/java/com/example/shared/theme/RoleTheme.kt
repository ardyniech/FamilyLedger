package com.example.shared.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.shared.models.HouseholdRole

data class RoleThemePalette(
    val role: HouseholdRole,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val tertiaryAccent: Color,
    val primarySoft: Color,
    val primaryContainer: Color,
    val backgroundTint: Color,
    val surfaceElevated: Color,
    val bannerGradientStart: Color,
    val bannerGradientEnd: Color,
    val cardBorder: Color,
    val isWifePink: Boolean
) {
    companion object {
        fun suami(): RoleThemePalette {
            return RoleThemePalette(
                role = HouseholdRole.SUAMI,
                primaryAccent = Color(0xFF2563EB), // Cobalt Royal Blue
                secondaryAccent = Color(0xFF0284C7), // Sky Blue
                tertiaryAccent = Color(0xFFD97706), // Amber
                primarySoft = Color(0xFFDBEAFE),
                primaryContainer = Color(0xFFEFF6FF),
                backgroundTint = Color(0xFFF0F7FF),
                surfaceElevated = Color(0xFFFFFFFF),
                bannerGradientStart = Color(0xFF1D4ED8),
                bannerGradientEnd = Color(0xFF0F172A),
                cardBorder = Color(0xFFBFDBFE),
                isWifePink = false
            )
        }

        fun istri(): RoleThemePalette {
            return RoleThemePalette(
                role = HouseholdRole.ISTRI,
                primaryAccent = Color(0xFFEC4899), // Blossom Vibrant Rose Pink
                secondaryAccent = Color(0xFFF43F5E), // Coral Rose
                tertiaryAccent = Color(0xFFF59E0B), // Warm Golden Peach
                primarySoft = Color(0xFFFCE7F3),
                primaryContainer = Color(0xFFFFF1F2),
                backgroundTint = Color(0xFFFFF5F7),
                surfaceElevated = Color(0xFFFFFFFF),
                bannerGradientStart = Color(0xFFDB2777),
                bannerGradientEnd = Color(0xFF4C0519),
                cardBorder = Color(0xFFFBCFE8),
                isWifePink = true
            )
        }

        fun forRole(role: HouseholdRole): RoleThemePalette {
            return if (role == HouseholdRole.ISTRI) istri() else suami()
        }
    }
}

val LocalRoleTheme = staticCompositionLocalOf { RoleThemePalette.suami() }

object RoleTheme {
    val current: RoleThemePalette
        @Composable @ReadOnlyComposable
        get() = LocalRoleTheme.current
}

