package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.os.Handler
import android.util.Size
import androidx.camera.core.CameraX
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysisConfig
import androidx.camera.core.ImageProxy
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraImageProcessBuilder
import com.github.bobekos.simplebarcodescanner2.scanner.BarcodeScanner
import com.google.firebase.ml.vision.common.FirebaseVisionImage

//TODO refactoring
class Camera2ImageProcessor(handler: Handler, facing: CameraX.LensFacing) : CameraImageProcessBuilder(),
    ImageAnalysis.Analyzer {

    private val config = ImageAnalysisConfig.Builder()
        .setTargetResolution(Size(IMAGE_PROCESS_RESOLUTION_WIDTH, IMAGE_PROCESS_RESOLUTION_HEIGHT))
        .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        .setLensFacing(facing)
        .setCallbackHandler(handler)
        .build()

    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
        if (BarcodeScanner.isProcessing.compareAndSet(false, true)) {
            return
        }

        image?.image?.let {
            imageProcessCallback(FirebaseVisionImage.fromMediaImage(it, getVisionRotation(rotationDegrees)))
        }
    }


    val imageAnalysis = ImageAnalysis(config)

    init {
        imageAnalysis.analyzer = this
    }

}