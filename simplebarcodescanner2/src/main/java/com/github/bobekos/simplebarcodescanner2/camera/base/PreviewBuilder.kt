package com.github.bobekos.simplebarcodescanner2.camera.base

import android.view.TextureView
import com.github.bobekos.simplebarcodescanner2.utils.DisplayRotation

open class PreviewBuilder<T> {

    lateinit var displayRotation: DisplayRotation
        private set

    open fun createPreview(textureView: TextureView, width: Int, height: Int, block: (result: T) -> Unit) {
        displayRotation = DisplayRotation(textureView.display)
    }
}