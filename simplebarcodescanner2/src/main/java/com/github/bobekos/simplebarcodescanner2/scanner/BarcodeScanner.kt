package com.github.bobekos.simplebarcodescanner2.scanner

import android.graphics.RectF
import android.media.Image
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.utils.getBoundingBoxF
import com.github.bobekos.simplebarcodescanner2.utils.getRawValueOrEmpty
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

    fun processImage(image: Image, rotation: Int,
                     barcodeListener: (barcode: FirebaseVisionBarcode) -> Unit,
                     overlayListener: (rectF: RectF, rawValue: String) -> Unit) {
        if (isProcessing.compareAndSet(false, true)) {
            overlayListener(RectF(), "")

            return
        }

        val visionImage = FirebaseVisionImage.fromMediaImage(image, rotation)

        detector.detectInImage(visionImage)
            .addOnSuccessListener { result ->
                result?.forEach {
                    barcodeListener(it)
                    overlayListener(it.getBoundingBoxF(), it.getRawValueOrEmpty())
                }

                isProcessing.set(false)
            }
    }
}