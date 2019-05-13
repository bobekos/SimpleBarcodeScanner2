package com.github.bobekos.simplebarcodescanner2.utils

import android.view.Display
import android.view.Surface

class DisplayRotation(private val display: Display?) {

    fun getSurfaceRotation(): Float {
        return when (display?.rotation) {
            Surface.ROTATION_0 -> 0f
            Surface.ROTATION_90 -> 90f
            Surface.ROTATION_180 -> 180f
            Surface.ROTATION_270 -> 270f
            else -> 0f
        }
    }

    fun isPortrait(): Boolean {
        return when (display?.rotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> true
            else -> false
        }
    }

    fun isLandscape(): Boolean {
        return when (display?.rotation) {
            Surface.ROTATION_90, Surface.ROTATION_270 -> true
            else -> false
        }
    }

}