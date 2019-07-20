package com.github.bobekos.simplebarcodescanner2.scanner

import android.graphics.RectF
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraImageConverter
import com.github.bobekos.simplebarcodescanner2.utils.getBoundingBoxF
import com.github.bobekos.simplebarcodescanner2.utils.getRawValueOrEmpty
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import java.util.concurrent.atomic.AtomicBoolean

class BarcodeScanner(config: ScannerConfig) {

    private val detectorOptions = FirebaseVisionBarcodeDetectorOptions.Builder()
        .setBarcodeFormats(config.barcodeFormat.first(), *config.barcodeFormat.drop(1).toIntArray())
        .build()

    private val detector = FirebaseVision.getInstance()
        .getVisionBarcodeDetector(detectorOptions)

    private val isProcessing = AtomicBoolean(false)

    fun processImage(
        cameraImage: CameraImageConverter,
        barcodeResultListener: (barcodeResult: BarcodeResult) -> Unit,
        overlayListener: (rectF: RectF, rawValue: String) -> Unit
    ) {

        if (isProcessing.compareAndSet(false, true)) {
            return
        }

        val visionImage = cameraImage.build()

        detector.detectInImage(visionImage)
            .addOnSuccessListener { result ->
                if (result == null || result.isEmpty()) {
                    barcodeResultListener(BarcodeResult.Empty)
                    overlayListener(RectF(), "")
                } else {
                    result.forEach {
                        barcodeResultListener(BarcodeResult.Data(it))
                        overlayListener(it.getBoundingBoxF(), it.getRawValueOrEmpty())
                    }
                }

                isProcessing.set(false)
            }
    }
}