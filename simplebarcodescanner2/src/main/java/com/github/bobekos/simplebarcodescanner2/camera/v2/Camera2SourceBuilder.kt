package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.graphics.Matrix
import android.os.Handler
import android.util.Log
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

    private fun updateTextureView(
        textureView: TextureView,
        textureSize: Size,
        parentViewWidth: Int,
        parentViewHeight: Int
    ) {
        val previewWidth = textureSize.height
        val previewHeight = textureSize.width

        val surfaceWidthRatio = parentViewWidth.fdiv(previewWidth)
        val surfaceHeightRatio = parentViewHeight.fdiv(previewHeight)

        Log.e("DebugCamera", "Perview x: $previewWidth y: $previewHeight")
        Log.e("DebugCamera", "ParentView x: $parentViewWidth y: $parentViewHeight")
        Log.e("DebugCamera", "x: $surfaceWidthRatio y: $surfaceHeightRatio")

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