package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.view.TextureView
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import com.github.bobekos.simplebarcodescanner2.ScannerConfig

class Camera2Source(private val config: ScannerConfig) {

    private val cameraBuilder = Camera2SourceBuilder(config)

    fun onSurfaceReady(
        textureView: TextureView,
        width: Int,
        height: Int,
        block: (preview: Preview, imageProcessor: ImageAnalysis) -> Unit
    ) {
        val preview = cameraBuilder.getPreview(textureView, width, height)
        val imageProcessor = cameraBuilder.getImageProcessor()

        block(preview, imageProcessor)
    }

}