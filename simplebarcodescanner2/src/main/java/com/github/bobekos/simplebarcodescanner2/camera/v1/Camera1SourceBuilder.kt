package com.github.bobekos.simplebarcodescanner2.camera.v1

import android.hardware.Camera
import android.os.Handler
import android.util.Size
import android.view.TextureView
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraBuilder
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing
import com.google.android.gms.vision.CameraSource

class Camera1SourceBuilder(private val config: ScannerConfig, private val displaySize: Size) :
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
    }

    override fun createPreview(textureView: TextureView, width: Int, height: Int): Camera1Preview {
        val preview = Camera1Preview(camera, displaySize)

        preview.setOnPreviewOutputListener { camera, previewSize ->
            camera.setPreviewTexture(textureView.surfaceTexture)
            camera.setDisplayOrientation(90)

            updateTextureView(textureView, previewSize, width, height)
        }

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
}