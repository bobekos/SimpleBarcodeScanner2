package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.view.TextureView
import com.github.bobekos.simplebarcodescanner2.ScannerConfig

class Camera2Source(private val config: ScannerConfig) {

    private val previewBuilder = Camera2SourceBuilder(config)

    fun onSurfaceReady(textureView: TextureView, width: Int, height: Int) {
        val preview = previewBuilder.getPreview(textureView, width, height)


    }

}