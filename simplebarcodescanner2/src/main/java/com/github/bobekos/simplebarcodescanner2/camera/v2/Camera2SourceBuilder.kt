package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.graphics.Matrix
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
import com.github.bobekos.simplebarcodescanner2.utils.fdiv

class Camera2SourceBuilder(displaySize: Size, private val config: ScannerConfig) :
    CameraBuilder<Preview, Camera2ImageProcessor>() {

    private val targetSize: Size = config.previewSize ?: displaySize

    private val previewConfig = PreviewConfig.Builder()
        .setLensFacing(getFacing(config.lensFacing))
        .setTargetResolution(targetSize)
        .setTargetAspectRatio(Rational(targetSize.width, targetSize.height))
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
        return Camera2ImageProcessor(handler, getFacing(config.lensFacing))
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
            matrix.preScale(xScale, yScale, centerX, centerY)
        } else {
            matrix.preScale(yScale, xScale, centerX, centerY)
        }

        textureView.setTransform(matrix)
    }

}