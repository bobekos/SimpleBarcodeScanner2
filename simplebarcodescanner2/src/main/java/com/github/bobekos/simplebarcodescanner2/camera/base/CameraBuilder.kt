package com.github.bobekos.simplebarcodescanner2.camera.base

import android.os.Handler
import android.os.HandlerThread
import android.view.TextureView
import com.github.bobekos.simplebarcodescanner2.model.BarcodeSurface
import com.github.bobekos.simplebarcodescanner2.utils.DisplayRotation
import com.google.firebase.ml.vision.common.FirebaseVisionImage

abstract class CameraBuilder<T, X> {

    private val imageProcessingThread = HandlerThread("CameraImageProcessingThread")

    lateinit var displayRotation: DisplayRotation
        private set

    abstract fun createPreview(textureView: TextureView, width: Int, height: Int): T

    abstract fun createImageAnalyzer(handler: Handler, block: (image: FirebaseVisionImage) -> Unit): X

    fun getPreview(surfaceMetaData: BarcodeSurface.MetaData): T {
        displayRotation = DisplayRotation(surfaceMetaData.textureView.display)

        return createPreview(surfaceMetaData.textureView, surfaceMetaData.width, surfaceMetaData.height)
    }

    fun getImageProcessor(block: (image: FirebaseVisionImage) -> Unit): X {
        imageProcessingThread.start()

        return createImageAnalyzer(Handler(imageProcessingThread.looper), block)
    }
}