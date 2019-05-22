package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.graphics.Matrix
import android.graphics.RectF
import android.os.Handler
import android.util.Rational
import android.util.Size
import android.view.Surface
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
        var previewWidth = textureSize.width
        var previewHeight = textureSize.height

        if (displayRotation.isPortrait()) {
            previewWidth = textureSize.height
            previewHeight = textureSize.width
        }

        val surfaceWidthRatio = parentViewWidth.fdiv(previewWidth)
        val surfaceHeightRatio = parentViewHeight.fdiv(previewHeight)

        val surfaceScaleX: Float
        val surfaceScaleY: Float

        if (surfaceWidthRatio > surfaceHeightRatio) {
            surfaceScaleX = 1f
            surfaceScaleY = (previewHeight * surfaceWidthRatio).div(parentViewHeight)
        } else {
            surfaceScaleX = (previewWidth * surfaceHeightRatio).div(previewWidth)
            surfaceScaleY = 1f
        }


        val centerX = parentViewWidth.fdiv(2)
        val centerY = parentViewHeight.fdiv(2)

        val matrix = Matrix()
        matrix.postRotate(-displayRotation.getSurfaceRotation(), centerX, centerY)
        matrix.preScale(surfaceScaleX, surfaceScaleY, centerX, centerY)

        textureView.setTransform(matrix)
    }
}