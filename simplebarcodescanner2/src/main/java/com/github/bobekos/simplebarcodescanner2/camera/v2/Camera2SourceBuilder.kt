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
        val matrix = Matrix()

        val rectView = RectF(0f, 0f, parentViewWidth.toFloat(), parentViewHeight.toFloat())
        val viewCenterX = rectView.centerX()
        val viewCenterY = rectView.centerY()

        val rectPreview = RectF(0f, 0f, textureSize.height.toFloat(), textureSize.width.toFloat())
        val previewCenterX = rectPreview.centerX()
        val previewCenterY = rectPreview.centerY()

        rectPreview.offset(viewCenterX - previewCenterX, viewCenterY - previewCenterY)
        matrix.setRectToRect(rectView, rectPreview, Matrix.ScaleToFit.FILL)

        val scale = 1f//Math.max(parentViewHeight.fdiv(targetSize.height), parentViewWidth.fdiv(targetSize.width))
        matrix.postScale(scale, scale, viewCenterX, viewCenterY)

        if (displayRotation.isLandscape()) {
            matrix.postRotate(90f * (displayRotation.test() - 2), viewCenterX, viewCenterY)
        }

        textureView.setTransform(matrix)
    }

    private fun updatetest(
        textureView: TextureView, textureSize: Size, parentViewWidth: Int,
        parentViewHeight: Int
    ) {
        val matrix = Matrix()

        val rectF = RectF(0f, 0f, parentViewWidth.toFloat(), parentViewHeight.toFloat())
        val viewCenterX = rectF.centerX()
        val viewCenterY = rectF.centerY()

        val rectPreview = RectF(0f, 0f, textureSize.height.toFloat(), textureSize.width.toFloat())
        val previewCenterX = rectPreview.centerX()
        val previewCenterY = rectPreview.centerY()

        if (displayRotation.isPortrait()) {
            rectPreview.offset(viewCenterX - previewCenterX, viewCenterY - previewCenterY)
            matrix.setRectToRect(rectF, rectPreview, Matrix.ScaleToFit.FILL)

            val scale = Math.max(parentViewHeight.fdiv(targetSize.height), parentViewWidth.fdiv(targetSize.width))
            matrix.postScale(scale, scale, viewCenterX, viewCenterY)
            //matrix.postRotate(90f * (displayRotation.test() - 2), viewCenterX, viewCenterY)
        } else {
            if (displayRotation.test() == Surface.ROTATION_180) {
                matrix.postRotate(180f, viewCenterX, viewCenterY)
            }
        }

        //matrix.postScale(-1f, 1f, viewCenterX, viewCenterY)

        textureView.setTransform(matrix)
    }

    private fun updateTextureViewOld(
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