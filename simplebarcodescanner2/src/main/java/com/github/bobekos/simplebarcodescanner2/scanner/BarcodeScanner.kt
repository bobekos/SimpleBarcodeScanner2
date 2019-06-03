package com.github.bobekos.simplebarcodescanner2.scanner

import android.media.Image
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import java.util.concurrent.atomic.AtomicBoolean

class BarcodeScanner(config: ScannerConfig) {

    private val detectorOptions = FirebaseVisionBarcodeDetectorOptions.Builder()
        .setBarcodeFormats(config.barcodeFormat.first(), *config.barcodeFormat.drop(1).toIntArray())
        .build()

    private val detector = FirebaseVision.getInstance()
        .getVisionBarcodeDetector(detectorOptions)

    private val isProcessing = AtomicBoolean(false)

    fun processImage(image: Image, rotation: Int, block: (barcode: FirebaseVisionBarcode) -> Unit) {
        if (isProcessing.compareAndSet(false, true)) {
            return
        }

        val visionImage = FirebaseVisionImage.fromMediaImage(image, rotation)

        detector.detectInImage(visionImage)
            .addOnSuccessListener { result ->
                result?.forEach(block)

                isProcessing.set(false)
            }
            .addOnFailureListener {
                //TODO
            }
    }
}