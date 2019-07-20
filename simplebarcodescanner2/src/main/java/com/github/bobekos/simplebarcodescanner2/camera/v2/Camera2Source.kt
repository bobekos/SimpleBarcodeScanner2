package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.util.Size
import android.view.TextureView
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraImageConverter
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraSource

class Camera2Source(config: ScannerConfig, displaySize: Size) : CameraSource(config) {

    private val cameraBuilder = Camera2SourceBuilder(config, displaySize)

    private var preview: Preview? = null
    private var processor: Camera2ImageProcessor? = null

    override fun build(
        lifecycleOwner: LifecycleOwner,
        textureView: TextureView,
        width: Int,
        height: Int
    ) =
        apply {
            preview = cameraBuilder.getPreview(textureView, width, height)
            processor = cameraBuilder.getImageProcessor()

            CameraX.bindToLifecycle(lifecycleOwner, preview, processor?.imageAnalysis)
        }

    override fun onImageProcessing(block: (imageConverter: CameraImageConverter) -> Unit) {
        processor?.setImageProcessListener(block)
    }

    override fun onConfigChange(config: ScannerConfig) {
        super.onConfigChange(config)

        preview?.enableTorch(config.isFlashOn)
    }

    override fun clear() {
        CameraX.unbindAll()
    }
}