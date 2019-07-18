package com.github.bobekos.simplebarcodescanner2.scanner

import android.graphics.RectF
import android.media.Image
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.utils.getBoundingBoxF
import com.github.bobekos.simplebarcodescanner2.utils.getRawValueOrEmpty
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

class BarcodeScanner(config: ScannerConfig) {

    private val detectorOptions = FirebaseVisionBarcodeDetectorOptions.Builder()
        .setBarcodeFormats(config.barcodeFormat.first(), *config.barcodeFormat.drop(1).toIntArray())
        .build()

    private val detector = FirebaseVision.getInstance()
        .getVisionBarcodeDetector(detectorOptions)

    private val isProcessing = AtomicBoolean(false)

    fun processImage(
        frame: ByteArray, rotation: Int,
        barcodeResultListener: (barcodeResult: BarcodeResult) -> Unit,
        overlayListener: (rectF: RectF, rawValue: String) -> Unit
    ) {
        if (isProcessing.compareAndSet(false, true)) {
            return
        }

        val visionImage = FirebaseVisionImage.fromByteArray(
            frame, FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(rotation)
                .setHeight(720)
                .setWidth(720).build()
        )

        detectBarcode(visionImage, barcodeResultListener, overlayListener)
    }

    fun processImage(
        image: Image, rotation: Int,
        barcodeResultListener: (barcodeResult: BarcodeResult) -> Unit,
        overlayListener: (rectF: RectF, rawValue: String) -> Unit
    ) {
        if (isProcessing.compareAndSet(false, true)) {
            return
        }

        val visionImage = FirebaseVisionImage.fromMediaImage(image, rotation)

        detectBarcode(visionImage, barcodeResultListener, overlayListener)
    }

    private fun detectBarcode(
        visionImage: FirebaseVisionImage,
        barcodeResultListener: (barcodeResult: BarcodeResult) -> Unit,
        overlayListener: (rectF: RectF, rawValue: String) -> Unit
    ) {
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