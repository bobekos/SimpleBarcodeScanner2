package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.media.Image
import android.os.Handler
import android.util.Rational
import android.util.Size
import androidx.camera.core.CameraX
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysisConfig
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraImageProcessBuilder

class Camera2ImageProcessor(handler: Handler, facing: CameraX.LensFacing, scannerResolution: Size) :
    CameraImageProcessBuilder<Image>(scannerResolution) {

    private val config = ImageAnalysisConfig.Builder()
        .setTargetResolution(resolution)
        .setTargetAspectRatio(Rational(resolution.width, resolution.height))
        .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        .setLensFacing(facing)
        .setCallbackHandler(handler)
        .build()

    val imageAnalysis = ImageAnalysis(config)

    init {
        imageAnalysis.setAnalyzer { image, rotationDegrees ->
            image?.image?.let {
                imageProcessCallback?.invoke(it, getVisionRotation(rotationDegrees))
            }
        }
    }

}