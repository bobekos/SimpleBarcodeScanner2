package com.github.bobekos.simplebarcodescanner2

import android.util.Size
import com.github.bobekos.simplebarcodescanner2.overlay.BarcodeOverlay
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode

data class ScannerConfig(
    var previewSize: Size? = null,
    var barcodeFormat: List<Int> = arrayListOf(FirebaseVisionBarcode.FORMAT_ALL_FORMATS),
    var lensFacing: CameraFacing = CameraFacing.BACK,
    var barcodeOverlay: BarcodeOverlay? = null
)