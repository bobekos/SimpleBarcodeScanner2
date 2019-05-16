package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.media.Image
import android.view.TextureView
import androidx.camera.core.CameraX
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import com.github.bobekos.simplebarcodescanner2.ScannerConfig

class Camera2Source(config: ScannerConfig) {

    private val cameraBuilder = Camera2SourceBuilder(config)

    private lateinit var preview: Preview
    private lateinit var processor: Camera2ImageProcessor

    fun build(lifecycleOwner: LifecycleOwner, textureView: TextureView, width: Int, height: Int) = apply {
        preview = cameraBuilder.getPreview(textureView, width, height)
        processor = cameraBuilder.getImageProcessor()

        CameraX.bindToLifecycle(lifecycleOwner, preview, processor.imageAnalysis)
    }

    fun onImageProcessing(block: (image: Image, firebaseRotation: Int) -> Unit) {
        processor.setImageProcessListener { image, firebaseRotation ->
            block(image, firebaseRotation)
        }
    }

}