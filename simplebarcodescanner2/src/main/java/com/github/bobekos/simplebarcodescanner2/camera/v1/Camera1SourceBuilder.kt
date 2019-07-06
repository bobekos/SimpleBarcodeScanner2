package com.github.bobekos.simplebarcodescanner2.camera.v1

import android.graphics.Matrix
import android.hardware.Camera
import android.os.Handler
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.SurfaceHolder
import android.view.TextureView
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraBuilder
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing
import com.github.bobekos.simplebarcodescanner2.utils.fdiv
import com.google.android.gms.vision.CameraSource

class Camera1SourceBuilder(private val config: ScannerConfig, private  val displaySize: Size) :
    CameraBuilder<Camera1Preview, Camera1ImageProcessor>() {

    private lateinit var camera: Camera

    fun build() = apply {
        val cameraInfo = Camera.CameraInfo()
        val cameraId = (0..Camera.getNumberOfCameras())
            .find {
                Camera.getCameraInfo(it, cameraInfo)

                cameraInfo.facing == getFacing(config.lensFacing)
            }

        if (cameraId == null) {
            //TODO throw no facing for config type found dont forget camera 2
        } else {
            camera = Camera.open(cameraId)
        }

        //TODO set camera parameters
    }

    override fun createPreview(textureView: TextureView, width: Int, height: Int): Camera1Preview {
        camera.setPreviewTexture(textureView.surfaceTexture)
        camera.setDisplayOrientation(displayRotation.getDisplayRotation())

        val preview = Camera1Preview(camera, displaySize)
        updateTextureView(textureView, preview.previewSize, width, height)

        return preview
    }

    override fun createImageAnalyzer(handler: Handler): Camera1ImageProcessor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getFacing(facing: CameraFacing): Int {
        return when (facing) {
            CameraFacing.BACK -> CameraSource.CAMERA_FACING_BACK
            CameraFacing.FRONT -> CameraSource.CAMERA_FACING_FRONT
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