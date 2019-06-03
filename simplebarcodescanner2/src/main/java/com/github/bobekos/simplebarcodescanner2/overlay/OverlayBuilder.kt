package com.github.bobekos.simplebarcodescanner2.overlay

import android.graphics.Rect
import android.util.Size
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.scaleMatrix
import com.github.bobekos.simplebarcodescanner2.utils.fdiv
import com.github.bobekos.simplebarcodescanner2.utils.getNullSafeBoundingBox
import com.github.bobekos.simplebarcodescanner2.utils.getNullSafeRawValue
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode

class OverlayBuilder {

    private var xScaleFactorP: Float = 0f
    private var xScaleFactorL: Float = 0f
    private var yScaleFactorP: Float = 0f
    private var yScaleFactorL: Float = 0f

    fun createOverlayView(root: FrameLayout, width: Int, height: Int, overlay: BarcodeOverlay?) = apply {
        if (overlay == null) {
            return this
        }

        root.removeView(overlay as View)
        root.addView(overlay, width, height)
    }

    fun calculateOverlayScale(width: Int, height: Int, displaySize: Size) {
        xScaleFactorP = width.fdiv(Math.min(displaySize.width, displaySize.height))
        xScaleFactorL = width.fdiv(Math.max(displaySize.width, displaySize.height))
        yScaleFactorP = height.fdiv(Math.max(displaySize.width, displaySize.height))
        yScaleFactorL = height.fdiv(Math.min(displaySize.width, displaySize.height))
    }

    fun onBarcodeDetected(barcode: FirebaseVisionBarcode, overlay: BarcodeOverlay?) {
        if (overlay == null) {
            return
        }

        val rect = Rect(barcode.getNullSafeBoundingBox()).apply {
            left = translateX(left)
            top = translateY(top)
            right = translateX(right)
            bottom = translateY(bottom)
        }

        overlay.onUpdate(rect, barcode.getNullSafeRawValue())
    }

    private fun translateX(x: Int): Int {
        var result = (x * if (isPortraitMode()) xScaleFactorP else xScaleFactorL).toInt()

        /*
        if (Camera.isFacingFront(config.facing)) {
            result = cameraView.width - result
        }
         */

        return result
    }

    private fun translateY(y: Int): Int {
        return (y * if (isPortraitMode()) yScaleFactorP else yScaleFactorL).toInt()
    }

    private fun isPortraitMode(): Boolean {
        return true
    }
}