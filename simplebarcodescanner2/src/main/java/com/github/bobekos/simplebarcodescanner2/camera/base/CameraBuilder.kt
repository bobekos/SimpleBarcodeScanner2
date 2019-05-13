package com.github.bobekos.simplebarcodescanner2.camera.base

import android.view.TextureView
import com.github.bobekos.simplebarcodescanner2.utils.DisplayRotation

abstract class CameraBuilder<T> {

    lateinit var displayRotation: DisplayRotation
        private set

    abstract fun createPreview(textureView: TextureView, width: Int, height: Int): T

    abstract fun createImageAnalyzer()

    fun getPreview(textureView: TextureView, width: Int, height: Int): T {
        displayRotation = DisplayRotation(textureView.display)

        return createPreview(textureView, width, height)
    }
}