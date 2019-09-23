package com.github.bobekos.simplebarcodescanner2.utils

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.*


object DetectionUtils {

    fun playBeepSound() {
        val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
        Handler(Looper.getMainLooper()).postDelayed({ toneGenerator.release() }, 150)
    }

    fun vibrate(ctx: Context, duration: Long) {
        val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(duration)
        }
    }

}