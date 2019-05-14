package com.github.bobekos.simplebarcodescanner2.camera.v2

import androidx.camera.core.CameraX
import androidx.lifecycle.LifecycleOwner
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import com.github.bobekos.simplebarcodescanner2.model.BarcodeSurface
import com.github.bobekos.simplebarcodescanner2.utils.isNotDisposed
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class Camera2Source(config: ScannerConfig) {

    private val cameraBuilder = Camera2SourceBuilder(config)

    fun getObservable(lifecycleOwner: LifecycleOwner, barcodeSurface: BarcodeSurface): Observable<FirebaseVisionImage> {
        return Observable.create<FirebaseVisionImage> { emitter ->
            when (barcodeSurface) {
                is BarcodeSurface.Disposed -> {
                    //TODO
                    emitter.isNotDisposed {
                        onComplete()
                    }
                }
                is BarcodeSurface.MetaData -> {
                    val cameraPreview = cameraBuilder.getPreview(barcodeSurface)
                    val imageProcessor = cameraBuilder.getImageProcessor { image ->
                        emitter.isNotDisposed {
                            onNext(image)
                        }
                    }

                    CameraX.bindToLifecycle(lifecycleOwner, cameraPreview, imageProcessor)
                }
            }

            emitter.setCancellable {
                //TODO
                CameraX.unbindAll()
            }
        }.subscribeOn(Schedulers.io())
    }

}