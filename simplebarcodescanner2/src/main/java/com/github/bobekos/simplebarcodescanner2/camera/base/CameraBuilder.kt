package com.github.bobekos.simplebarcodescanner2.camera.base

import android.graphics.Matrix
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.TextureView
import com.github.bobekos.simplebarcodescanner2.utils.DisplayRotation
import com.github.bobekos.simplebarcodescanner2.utils.fdiv

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
        textureSize: Size,
        parentViewWidth: Int,
        parentViewHeight: Int
    ) {
        val previewWidth = textureSize.height
        val previewHeight = textureSize.width

        val surfaceWidthRatio = parentViewWidth.fdiv(previewWidth)
        val surfaceHeightRatio = parentViewHeight.fdiv(previewHeight)

        val surfaceScaleX: Float
        val surfaceScaleY: Float

        if (surfaceWidthRatio > surfaceHeightRatio) {
            surfaceScaleX = 1f
            surfaceScaleY = (previewHeight * surfaceWidthRatio).div(parentViewHeight)
        } else {
            surfaceScaleX = (previewWidth * surfaceHeightRatio).div(previewWidth)
            surfaceScaleY = 1f
        }

        val centerX = parentViewWidth.fdiv(2)
        val centerY = parentViewHeight.fdiv(2)

        val matrix = Matrix()
        matrix.postRotate(-displayRotation.getSurfaceRotation(), centerX, centerY)
        matrix.preScale(surfaceScaleX, surfaceScaleY, centerX, centerY)

        textureView.setTransform(matrix)
    }
}