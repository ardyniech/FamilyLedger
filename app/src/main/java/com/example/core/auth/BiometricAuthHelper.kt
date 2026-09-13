package com.example.core.auth

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object BiometricAuthHelper {
    fun isBiometricHardwareAvailable(context: Context): Boolean {
        return try {
            val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? android.app.KeyguardManager
            keyguardManager?.isDeviceSecure == true
        } catch (_: Exception) {
            false
        }
    }

    fun triggerHapticFeedback(context: Context, isSuccess: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                val effect = if (isSuccess) {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                } else {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                }
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                val millis = if (isSuccess) 40L else 120L
                vibrator?.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } catch (_: Exception) {
        }
    }
}
