package com.example.shared.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticEngine {
    fun triggerLightImpact(context: Context) {
        triggerHaptic(context, VibrationEffect.EFFECT_TICK)
    }

    fun triggerMediumImpact(context: Context) {
        triggerHaptic(context, VibrationEffect.EFFECT_CLICK)
    }

    fun triggerHeavyImpact(context: Context) {
        triggerHaptic(context, VibrationEffect.EFFECT_HEAVY_CLICK)
    }

    fun triggerSuccess(context: Context) {
        triggerHaptic(context, VibrationEffect.EFFECT_DOUBLE_CLICK)
    }

    private fun triggerHaptic(context: Context, effectId: Int) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(effectId))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(50) // Fallback
                }
            }
        } catch (e: Exception) {
            // Ignore haptic failures
        }
    }
}
