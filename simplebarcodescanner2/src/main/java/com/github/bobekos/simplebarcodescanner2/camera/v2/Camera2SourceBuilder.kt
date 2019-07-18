package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.os.Handler
import android.util.Rational
import android.util.Size
import android.view.TextureView
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraBuilder
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing

class Camera2SourceBuilder(private val config: ScannerConfig, displaySize: Size) :
    CameraBuilder<Preview, Camera2ImageProcessor>() {

    private val previewConfig = PreviewConfig.Builder()
        .setLensFacing(getFacing(config.lensFacing))
        .setTargetResolution(displaySize)
        .setTargetAspectRatio(Rational(displaySize.width, displaySize.height))
        .build()

    override fun createPreview(textureView: TextureView, width: Int, height: Int): Preview {
        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            textureView.surfaceTexture = it.surfaceTexture

            updateTextureView(textureView, it.textureSize, width, height)
        }

        return preview
    }

    override fun createImageAnalyzer(handler: Handler): Camera2ImageProcessor {
        return Camera2ImageProcessor(handler, getFacing(config.lensFacing), config.scannerResolution)
    }

    private fun getFacing(facing: CameraFacing): CameraX.LensFacing {
        return when (facing) {
            CameraFacing.BACK -> CameraX.LensFacing.BACK
            CameraFacing.FRONT -> CameraX.LensFacing.FRONT
        }
    }
}