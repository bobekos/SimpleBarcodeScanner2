package com.github.bobekos.simplebarcodescanner2.overlay

import android.graphics.Rect

interface BarcodeOverlay {
    fun onUpdate(posRect: Rect = Rect(), barcodeValue: String = "")
}