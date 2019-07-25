package com.github.bobekos.simplebarcodescanner2.camera.v1

import android.util.Size
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraImageConverter
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata

class Camera1Image(
    private val frame: ByteArray,
    private val size: Size,
    private val rotation: Int
) : CameraImageConverter {

    override fun build(): FirebaseVisionImage {
        return FirebaseVisionImage.fromByteArray(
            frame, FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(rotation)
                .setHeight(size.height)
                .setWidth(size.width)
                .build()
        )
    }
}