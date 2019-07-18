package com.github.bobekos.simplebarcodescanner2.camera.v1

import android.util.Size
import android.view.TextureView
import androidx.lifecycle.LifecycleOwner
import com.github.bobekos.simplebarcodescanner2.ScannerConfig

class Camera1Source(private val config: ScannerConfig, displaySize: Size) {

    private val cameraBuilder = Camera1SourceBuilder(config, displaySize).build()

    private var preview: Camera1Preview? = null

    fun build(lifecycleOwner: LifecycleOwner, textureView: TextureView, width: Int, height: Int) =
        apply {
            preview = cameraBuilder.getPreview(textureView, width, height)

            preview?.bindToLifecycle(lifecycleOwner)
        }

}