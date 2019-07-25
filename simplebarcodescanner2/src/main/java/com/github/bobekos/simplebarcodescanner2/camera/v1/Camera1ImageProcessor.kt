package com.github.bobekos.simplebarcodescanner2.camera.v1

import android.hardware.Camera
import android.os.Handler
import android.util.Size
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraImageProcessBuilder

class Camera1ImageProcessor(camera: Camera, handler: Handler, previewSize: Size, rotation: Int) :
    CameraImageProcessBuilder() {

    init {
        camera.setPreviewCallback { data, _ ->
            handler.post {
                if (data != null) {
                    imageProcessCallback?.invoke(
                        Camera1Image(
                            data,
                            previewSize,
                            getVisionRotation(rotation)
                        ),
                        previewSize
                    )
                }
            }
        }
    }

}