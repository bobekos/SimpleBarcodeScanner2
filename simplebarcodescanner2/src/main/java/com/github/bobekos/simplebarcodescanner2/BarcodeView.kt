package com.github.bobekos.simplebarcodescanner2

import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Size
import android.view.TextureView
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.github.bobekos.simplebarcodescanner2.camera.v2.Camera2Source
import com.github.bobekos.simplebarcodescanner2.scanner.BarcodeScanner
import com.github.bobekos.simplebarcodescanner2.utils.isNotDisposed
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import io.reactivex.Observable

class BarcodeView : FrameLayout, LifecycleOwner {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private lateinit var lifecycleRegistry: LifecycleRegistry
    private lateinit var textureView: TextureView

    private val defaultConfig by lazy {
        ScannerConfig(Size(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.heightPixels))
    }

    private val cameraSource by lazy {
        Camera2Source(defaultConfig)
    }

    private val barcodeScanner by lazy {
        BarcodeScanner(defaultConfig)
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    private fun init() {
        setBackgroundColor(Color.BLACK)

        lifecycleRegistry = LifecycleRegistry(this)
        textureView = TextureView(context)

        addView(textureView)
    }

    fun getObservable(): Observable<FirebaseVisionBarcode> {
        return Observable.create<FirebaseVisionBarcode> { emitter ->
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
                    //TODO
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                    lifecycleRegistry.markState(Lifecycle.State.DESTROYED)

                    emitter.isNotDisposed {
                        //onNext(BarcodeSurface.Disposed)
                    }

                    return true
                }

                override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                    lifecycleRegistry.markState(Lifecycle.State.CREATED)
                    lifecycleRegistry.markState(Lifecycle.State.STARTED)

                    cameraSource
                        .build(this@BarcodeView, textureView, width, height)
                        .onImageProcessing { image, firebaseRotation ->
                            barcodeScanner.processImage(image, firebaseRotation) { barcode ->
                                emitter.isNotDisposed {
                                    onNext(barcode)
                                }
                            }
                        }
                }
            }

            emitter.setCancellable {
                textureView.surfaceTextureListener = null
            }
        }
    }
}