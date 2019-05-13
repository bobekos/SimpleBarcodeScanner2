package com.github.bobekos.simplebarcodescanner2.camera.base

import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata

open class CameraImageProcessBuilder {

    var imageProcessCallback: (image: FirebaseVisionImage) -> Unit = {}
        private set

    companion object {
        const val IMAGE_PROCESS_RESOLUTION_WIDTH = 480
        const val IMAGE_PROCESS_RESOLUTION_HEIGHT = 360
    }

    fun setImageProcessListener(block: (image: FirebaseVisionImage) -> Unit) {
        imageProcessCallback = block
    }

    fun getVisionRotation(rotationDegrees: Int): Int {
        return when (rotationDegrees) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> FirebaseVisionImageMetadata.ROTATION_0
        }
    }

}