package com.github.bobekos.simplebarcodescanner2.camera.v1

import android.hardware.Camera
import android.util.Size
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraImageProcessBuilder

class Camera1ImageProcessor(camera: Camera, scannerResolution: Size) : CameraImageProcessBuilder() {

    init {
        camera.setPreviewCallback { data, _ ->
            if (data != null) {
                imageProcessCallback?.invoke(Camera1Image(data, scannerResolution, getVisionRotation(180)))
            }
        }
    }

}