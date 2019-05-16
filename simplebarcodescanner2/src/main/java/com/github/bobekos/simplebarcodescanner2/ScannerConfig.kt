package com.github.bobekos.simplebarcodescanner2

import android.util.Size
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode

data class ScannerConfig(
    val previewSize: Size? = null,
    val barcodeFormat: List<Int> = arrayListOf(FirebaseVisionBarcode.FORMAT_ALL_FORMATS),
    val lensFacing: CameraFacing = CameraFacing.BACK
)