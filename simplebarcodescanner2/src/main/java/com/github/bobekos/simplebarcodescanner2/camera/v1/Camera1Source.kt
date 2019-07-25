package com.github.bobekos.simplebarcodescanner2.camera.v1

import android.util.Size
import android.view.TextureView
import androidx.lifecycle.LifecycleOwner
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraImageConverter
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraSource

class Camera1Source(config: ScannerConfig, displaySize: Size) : CameraSource(config) {

    private val cameraBuilder = Camera1SourceBuilder(config, displaySize).build()

    private var preview: Camera1Preview? = null
    private var processor: Camera1ImageProcessor? = null

    override fun build(
        lifecycleOwner: LifecycleOwner,
        textureView: TextureView,
        width: Int,
        height: Int
    ) = apply {
        preview = cameraBuilder.getPreview(textureView, width, height)
        processor = cameraBuilder.getImageProcessor()

        preview?.bindToLifecycle(lifecycleOwner)
    }

    override fun onImageProcessing(block: (imageConverter: CameraImageConverter, imageSize: Size) -> Unit) {
        processor?.setImageProcessListener(block)
    }

    override fun onConfigChange(config: ScannerConfig) {
        super.onConfigChange(config)

        //TODO
        //preview?.enablteTorch(config.isFlashOn)
    }

    override fun clear() {
        super.clear()

        //TODO
    }
}