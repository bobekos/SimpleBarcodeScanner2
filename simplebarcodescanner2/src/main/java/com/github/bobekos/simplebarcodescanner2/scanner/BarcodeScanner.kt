package com.github.bobekos.simplebarcodescanner2.scanner

import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.utils.isNotDisposed
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class BarcodeScanner(private val config: ScannerConfig) {

    //TODO CONFIG

    private val detector = FirebaseVision.getInstance().visionBarcodeDetector

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