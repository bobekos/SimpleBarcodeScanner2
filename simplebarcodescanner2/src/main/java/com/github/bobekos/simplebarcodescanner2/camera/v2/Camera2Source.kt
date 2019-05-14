package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.view.TextureView
import androidx.camera.core.CameraX
import androidx.lifecycle.LifecycleOwner
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.google.firebase.ml.vision.common.FirebaseVisionImage

class Camera2Source(config: ScannerConfig) {

    private val cameraBuilder = Camera2SourceBuilder(config)

    fun create(
        lifecycleOwner: LifecycleOwner,
        textureView: TextureView,
        width: Int,
        height: Int,
        block: (image: FirebaseVisionImage) -> Unit
    ) {
        val cameraPreview = cameraBuilder.getPreview(textureView, width, height)
        val imageProcessor = cameraBuilder.getImageProcessor { image ->
            block(image)
        }

        CameraX.bindToLifecycle(lifecycleOwner, cameraPreview, imageProcessor)
    }

}