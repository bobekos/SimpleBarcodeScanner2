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
    //private val targetSize: Size = Size(1080, 2065)

    private val previewConfig = PreviewConfig.Builder()
            .setLensFacing(getFacing(config.lensFacing))
            .setTargetResolution(targetSize)
            .setTargetAspectRatio(Rational(targetSize.width, targetSize.height))
            .build()

    override fun createPreview(textureView: TextureView, width: Int, height: Int): Preview {
        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            textureView.surfaceTexture = it.surfaceTexture

            updateTextureViewNew(textureView, it.textureSize, width, height)
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

    private fun updateTextureViewNew(
            textureView: TextureView,
            textureSize: Size,
            parentViewWidth: Int,
            parentViewHeight: Int
    ) {
        var pWidth = textureSize.width
        var pHeight = textureSize.height

        val ratioSurface = parentViewWidth.fdiv(parentViewHeight)
        val ratioPreview = pWidth.fdiv(pHeight)

        val scaleX: Float
        val scaleY: Float

        if (ratioSurface > ratioPreview) {
            scaleX = parentViewHeight.fdiv(pHeight)
            scaleY = 1f
        } else {
            scaleX = 1f
            scaleY = parentViewHeight.fdiv(pWidth)
        }

        val matrix = Matrix()

        if (displayRotation.isLandscape()) {
            matrix.postRotate(90f * (displayRotation.getDisplayRotation() - 2), parentViewWidth.toFloat(), parentViewHeight.toFloat())
        }

        matrix.setScale(scaleX, scaleY)
        textureView.setTransform(matrix)

//        val scaledWidth = parentViewWidth * scaleX
//        val scaledHeight = parentViewHeight * scaleY
//
//        val dx = (parentViewWidth - scaledWidth).div(2)
//        val dy = (parentViewHeight - scaledHeight).div(2)
//
//        textureView.translationX = dx
//        textureView.translationZ = dy
    }

    private fun updateTextureView(
            textureView: TextureView,
            textureSize: Size,
            parentViewWidth: Int,
            parentViewHeight: Int
    ) {
        val matrix = Matrix()

        val rectView = RectF(0f, 0f, parentViewWidth.toFloat(), parentViewHeight.toFloat())
        val viewCenterX = rectView.centerX()
        val viewCenterY = rectView.centerY()

        val rectPreview = RectF(0f, 0f, textureSize.height.toFloat(), textureSize.width.toFloat())
        val previewCenterX = rectPreview.centerX()
        val previewCenterY = rectPreview.centerY()

        rectPreview.offset(viewCenterX - previewCenterX, viewCenterY - previewCenterY)
        matrix.setRectToRect(rectView, rectPreview, Matrix.ScaleToFit.FILL)

        val scaleX = parentViewHeight.fdiv(textureSize.height)
        val scaleY = parentViewHeight.fdiv(textureSize.width)

        matrix.postScale(1f, scaleY, viewCenterX, viewCenterY)

        if (displayRotation.isLandscape()) {
            matrix.postRotate(90f * (displayRotation.getDisplayRotation() - 2), viewCenterX, viewCenterY)
        }

        textureView.setTransform(matrix)
    }
}