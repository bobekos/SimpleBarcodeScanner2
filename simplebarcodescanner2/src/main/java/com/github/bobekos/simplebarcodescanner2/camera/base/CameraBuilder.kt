package com.github.bobekos.simplebarcodescanner2.camera.base

import android.os.Handler
import android.os.HandlerThread
import android.view.TextureView
import com.github.bobekos.simplebarcodescanner2.utils.DisplayRotation

abstract class CameraBuilder<T, X>() {

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
}