package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.annotation.TargetApi
import android.os.Build
import android.os.Handler
import android.util.Rational
import androidx.camera.core.CameraX
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysisConfig
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraImageProcessBuilder
import com.github.bobekos.simplebarcodescanner2.utils.Size

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class Camera2ImageProcessor(handler: Handler, facing: CameraX.LensFacing, scannerResolution: Size) :
    CameraImageProcessBuilder() {

    private val config = ImageAnalysisConfig.Builder()
        .setTargetResolution(scannerResolution.getCamera2Size())
        .setTargetAspectRatio(Rational(scannerResolution.width, scannerResolution.height))
        .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        .setLensFacing(facing)
        .setCallbackHandler(handler)
        .build()

    val imageAnalysis = ImageAnalysis(config)

    init {
        imageAnalysis.setAnalyzer { frame, rotationDegrees ->
            frame?.image?.let {
                imageProcessCallback?.invoke(
                    Camera2Image(it, getVisionRotation(rotationDegrees)),
                    Size(it.width, it.height)
                )
            }
        }
    }

}