package com.github.bobekos.simplebarcodescanner2.camera.base

import android.graphics.Matrix
import android.graphics.RectF
import android.os.Handler
import android.os.HandlerThread
import android.view.TextureView
import com.github.bobekos.simplebarcodescanner2.utils.DisplayRotation
import com.github.bobekos.simplebarcodescanner2.utils.Size
import com.github.bobekos.simplebarcodescanner2.utils.fdiv
import kotlin.math.max

abstract class CameraBuilder<T, X> {

    private val imageProcessingThread = HandlerThread("CameraImageProcessingThread")

    lateinit var displayRotation: DisplayRotation
        private set

    abstract fun createPreview(textureView: TextureView, width: Int, height: Int): T

    abstract fun createImageAnalyzer(handler: Handler): X

    fun getPreview(textureView: TextureView, width: Int, height: Int): T {
        displayRotation = DisplayRotation(textureView.display)

        return createPreview(textureView, width, height)
    }

    fun getImageProcessor(): X {
        imageProcessingThread.start()

        return createImageAnalyzer(Handler(imageProcessingThread.looper))
    }

    fun updateTextureView(
        textureView: TextureView,
        cameraPreviewSize: Size,
        parentViewWidth: Int,
        parentViewHeight: Int
    ) {
        val matrix = Matrix()

        val textureRectF = RectF(0f, 0f, parentViewWidth.toFloat(), parentViewHeight.toFloat())
        val previewRectF =
            RectF(0f, 0f, cameraPreviewSize.height.toFloat(), cameraPreviewSize.width.toFloat())

        val centerX = textureRectF.centerX()
        val centerY = textureRectF.centerY()

        if (displayRotation.isLandscape()) {
            previewRectF.offset(centerX - previewRectF.centerX(), centerY - previewRectF.centerY())
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL)

            val scale = max(
                parentViewWidth.fdiv(cameraPreviewSize.width),
                parentViewHeight.fdiv(cameraPreviewSize.height)
            )
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate(90f * (displayRotation.getDisplayRotation() - 2), centerX, centerY)
        }

        textureView.setTransform(matrix)
    }
}