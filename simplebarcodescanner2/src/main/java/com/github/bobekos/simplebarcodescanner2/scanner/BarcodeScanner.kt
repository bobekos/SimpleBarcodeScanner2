package com.github.bobekos.simplebarcodescanner2.scanner

import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.utils.isNotDisposed
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicBoolean

class BarcodeScanner(private val config: ScannerConfig) {

    companion object {
        val isProcessing = AtomicBoolean(false)
    }
    //TODO CONFIG

    private val detector = FirebaseVision.getInstance().visionBarcodeDetector

    fun create(image: FirebaseVisionImage, block: (barcode: FirebaseVisionBarcode) -> Unit) {
        detector.detectInImage(image)
            .addOnSuccessListener { result ->
                result?.forEach(block)

                isProcessing.set(false)
            }
            .addOnFailureListener {
                //TODO
            }
    }

    fun getObservable(image: FirebaseVisionImage): Observable<FirebaseVisionBarcode> {
        return Observable.create<FirebaseVisionBarcode> { emitter ->
            detector.detectInImage(image)
                .addOnSuccessListener { result ->
                    emitter.isNotDisposed {
                        result?.forEach {
                            onNext(it)
                        }
                    }
                }
                .addOnFailureListener {
                    emitter.isNotDisposed {
                        onError(it)
                    }
                }

            emitter.setCancellable {
                //TODO
            }
        }.subscribeOn(Schedulers.io())
    }

}