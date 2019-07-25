package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.media.Image
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraImageConverter
import com.google.firebase.ml.vision.common.FirebaseVisionImage

class Camera2Image(private val image: Image, private val rotation: Int) : CameraImageConverter {

    override fun build(): FirebaseVisionImage {
        return FirebaseVisionImage.fromMediaImage(image, rotation)
    }
}