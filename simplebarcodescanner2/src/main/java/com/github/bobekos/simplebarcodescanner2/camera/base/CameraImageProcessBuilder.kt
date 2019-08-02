package com.github.bobekos.simplebarcodescanner2.camera.base

import com.github.bobekos.simplebarcodescanner2.utils.Size
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata

open class CameraImageProcessBuilder {

    var imageProcessCallback: ((CameraImageConverter, Size) -> Unit)? = null
        private set

    fun setImageProcessListener(block: (image: CameraImageConverter, imageSize: Size) -> Unit) {
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