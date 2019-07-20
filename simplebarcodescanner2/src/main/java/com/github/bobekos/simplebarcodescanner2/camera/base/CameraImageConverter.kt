package com.github.bobekos.simplebarcodescanner2.camera.base

import com.google.firebase.ml.vision.common.FirebaseVisionImage

interface CameraImageConverter {

    fun build(): FirebaseVisionImage

}