package com.github.bobekos.simplebarcodescanner2.overlay

import android.graphics.RectF

interface BarcodeOverlay {
    fun onUpdate(posRect: RectF = RectF(), barcodeValue: String = "")
}