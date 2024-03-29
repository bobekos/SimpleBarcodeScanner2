package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.graphics.Matrix
import android.util.Rational
import android.util.Size
import android.view.TextureView
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.camera.base.PreviewBuilder
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing
import com.github.bobekos.simplebarcodescanner2.utils.fdiv

class Camera2PreviewBuilder(private val config: ScannerConfig) : PreviewBuilder<Preview>() {

    private val previewConfig = PreviewConfig.Builder()
        .setLensFacing(getFacing(config.lensFacing))
        .setTargetResolution(config.previewSize)
        .setTargetAspectRatio(Rational(config.previewSize.width, config.previewSize.height))
        .build()

    override fun createPreview(textureView: TextureView, width: Int, height: Int, block: (result: Preview) -> Unit) {
        super.createPreview(textureView, width, height, block)

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            textureView.surfaceTexture = it.surfaceTexture

            updateTextureView(textureView, it.textureSize, width, height)
        }

        block(preview)
    }

    private fun getFacing(facing: CameraFacing): CameraX.LensFacing {
        return when (facing) {
            CameraFacing.BACK -> CameraX.LensFacing.BACK
            CameraFacing.FRONT -> CameraX.LensFacing.FRONT
        }
    }

    private fun updateTextureView(
        textureView: TextureView,
        textureSize: Size,
        parentViewWidth: Int,
        parentViewHeight: Int
    ) {
        val matrix = Matrix()

        val rotation = displayRotation.getSurfaceRotation()

        val centerX = textureSize.width.div(2f)
        val centerY = textureSize.height.div(2f)

        matrix.postRotate(-rotation, centerX, centerY)

        val bufferRatio = textureSize.height.fdiv(textureSize.width)

        val scaledWidth: Int
        val scaledHeight: Int

        if (textureSize.width > textureSize.height) {
            scaledHeight = textureSize.width
            scaledWidth = (textureSize.width * bufferRatio).toInt()
        } else {
            scaledHeight = textureSize.height
            scaledWidth = (textureSize.height * bufferRatio).toInt()
        }

        val xScale = scaledWidth.fdiv(parentViewWidth)
        val yScale = scaledHeight.fdiv(parentViewHeight)

        if (displayRotation.isPortrait()) {
            matrix.postScale(xScale, yScale, centerX, centerY)
        } else {
            matrix.postScale(yScale, xScale, centerX, centerY)
        }

        textureView.setTransform(matrix)
    }

}