package com.github.bobekos.simplebarcodescanner2


import com.github.bobekos.simplebarcodescanner2.overlay.BarcodeOverlay
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing
import com.github.bobekos.simplebarcodescanner2.utils.Size
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode

data class ScannerConfig(
        var isDefaultScannerResolution: Boolean = true,
        var scannerResolution: Size = Size(480, 640),
        var barcodeFormat: List<Int> = arrayListOf(FirebaseVisionBarcode.FORMAT_ALL_FORMATS),
        var lensFacing: CameraFacing = CameraFacing.BACK,
        var barcodeOverlay: BarcodeOverlay? = null,
        var isFlashOn: Boolean = false,
        var playBeepSound: Boolean = false,
        var vibrate: Long = 0L
)