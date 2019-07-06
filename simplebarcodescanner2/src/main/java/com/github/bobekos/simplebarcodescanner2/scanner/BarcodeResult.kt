package com.github.bobekos.simplebarcodescanner2.scanner

import com.github.bobekos.simplebarcodescanner2.utils.getRawValueOrEmpty
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode

sealed class BarcodeResult(val rawValue: String) {
    object Empty : BarcodeResult("")
    data class Data(val barcode: FirebaseVisionBarcode) : BarcodeResult(barcode.getRawValueOrEmpty())
}