package com.example.memorynote2025.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibrateUtil {
    fun vibrate(context: Context, duration: Long = 200) {
        // Vibrator 객체 가져오기
        // API 31 이상 (VibratorManager 사용)
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION") // 경고 메시지 무시
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        // 진동 실행
        // API 26 이상 (VibrationEffect 사용)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
}