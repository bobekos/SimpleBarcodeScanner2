package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.annotation.TargetApi
import android.os.Build
import android.os.Handler
import android.util.Rational
import android.view.TextureView
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraBuilder
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing
import com.github.bobekos.simplebarcodescanner2.utils.Size
import com.github.bobekos.simplebarcodescanner2.utils.toSimpleSize

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class Camera2SourceBuilder(private val config: ScannerConfig, displaySize: Size) :
    CameraBuilder<Preview, Camera2ImageProcessor>() {

    private val previewConfig = PreviewConfig.Builder()
        .setLensFacing(getFacing(config.lensFacing))
        .setTargetResolution(displaySize.getCamera2Size())
        .setTargetAspectRatio(Rational(displaySize.width, displaySize.height))
        .build()

    override fun createPreview(textureView: TextureView, width: Int, height: Int): Preview {
        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            textureView.surfaceTexture = it.surfaceTexture

            updateTextureView(textureView, it.textureSize.toSimpleSize(), width, height)
        }

        return preview
    }

    override fun createImageAnalyzer(handler: Handler): Camera2ImageProcessor {
        return Camera2ImageProcessor(
            handler,
            getFacing(config.lensFacing),
            config.scannerResolution
        )
    }

    private fun getFacing(facing: CameraFacing): CameraX.LensFacing {
        return when (facing) {
            CameraFacing.BACK -> CameraX.LensFacing.BACK
            CameraFacing.FRONT -> CameraX.LensFacing.FRONT
        }
    }
}