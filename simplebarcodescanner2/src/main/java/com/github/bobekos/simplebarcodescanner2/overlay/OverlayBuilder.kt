package com.github.bobekos.simplebarcodescanner2.overlay

import android.content.Context
import android.content.res.Configuration
import android.graphics.RectF
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing
import com.github.bobekos.simplebarcodescanner2.utils.fdiv

class OverlayBuilder {

    private var textureWidth: Int = 0
    private var textureHeight: Int = 0

    private var xScaleFactorP: Float = 0f
    private var xScaleFactorL: Float = 0f
    private var yScaleFactorP: Float = 0f
    private var yScaleFactorL: Float = 0f

    private var isPortrait: Boolean = true
    private var cameraFacing: CameraFacing = CameraFacing.BACK

    fun createOverlayView(root: FrameLayout, overlay: BarcodeOverlay?) = apply {
        if (overlay == null) {
            return this
        }

        root.removeView(overlay as View)
        root.addView(overlay, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun calculateOverlayScale(width: Int, height: Int, scannerResolution: Size) = apply {
        textureWidth = width
        textureHeight = height

        val max = Math.max(scannerResolution.width, scannerResolution.height)
        val min = Math.min(scannerResolution.width, scannerResolution.height)

        xScaleFactorP = textureWidth.fdiv(min)
        xScaleFactorL = textureWidth.fdiv(max)
        yScaleFactorP = textureHeight.fdiv(max)
        yScaleFactorL = textureHeight.fdiv(min)
    }

    fun checkOrientationAndFacing(ctx: Context, facing: CameraFacing) {
        isPortrait = ctx.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        cameraFacing = facing
    }

    fun onBarcodeDetected(barcodeRect: RectF, rawValue: String, overlay: BarcodeOverlay?) {
        if (overlay == null) {
            return
        }

        barcodeRect.left = translateX(barcodeRect.left)
        barcodeRect.top = translateY(barcodeRect.top)
        barcodeRect.right = translateX(barcodeRect.right)
        barcodeRect.bottom = translateY(barcodeRect.bottom)

        overlay.onUpdate(barcodeRect, rawValue)
    }

    private fun translateX(x: Float): Float {
        var result = (x * if (isPortrait) xScaleFactorP else xScaleFactorL)

        if (cameraFacing == CameraFacing.FRONT) {
            result = textureWidth - result
        }

        return result
    }

    private fun translateY(y: Float): Float {
        return (y * if (isPortrait) yScaleFactorP else yScaleFactorL)
    }
}