package com.github.bobekos.simplebarcodescanner2

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Size
import android.view.TextureView
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.github.bobekos.simplebarcodescanner2.camera.v2.Camera2Source
import com.github.bobekos.simplebarcodescanner2.overlay.BarcodeOverlay
import com.github.bobekos.simplebarcodescanner2.overlay.BarcodeRectOverlay
import com.github.bobekos.simplebarcodescanner2.overlay.OverlayBuilder
import com.github.bobekos.simplebarcodescanner2.scanner.BarcodeScanner
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing
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

    private lateinit var textureView: TextureView
    private lateinit var lifecycleRegistry: LifecycleRegistry

    private val defaultConfig = ScannerConfig()
    private val overlayBuilder = OverlayBuilder()
    private val barcodeScanner = BarcodeScanner(defaultConfig)
    //TODO
    private val cameraSource by lazy {
        Camera2Source(defaultConfig, getDisplaySize())
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    private fun init() {
        setBackgroundColor(Color.RED)

        lifecycleRegistry = LifecycleRegistry(this)

        textureView = TextureView(context)

        addView(textureView)
    }

    fun setFacing(facing: CameraFacing) = apply {
        defaultConfig.lensFacing = facing
    }

    fun drawOverlay(barcodeOverlay: BarcodeOverlay = BarcodeRectOverlay(context)) = apply {
        defaultConfig.barcodeOverlay = barcodeOverlay
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
                        cameraSource.clear()
                    }

                    return true
                }

                override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                    lifecycleRegistry.markState(Lifecycle.State.CREATED)
                    lifecycleRegistry.markState(Lifecycle.State.STARTED)

                    overlayBuilder
                        .createOverlayView(this@BarcodeView, width, height, defaultConfig.barcodeOverlay)
                        .calculateOverlayScale(width, height, getDisplaySize())

                    cameraSource
                        .build(this@BarcodeView, textureView, width, height)
                        .onImageProcessing { image, rotation ->
                            barcodeScanner.processImage(image, rotation) { barcode ->
                                emitter.isNotDisposed {
                                    overlayBuilder.onBarcodeDetected(barcode, defaultConfig.barcodeOverlay)

                                    onNext(barcode)
                                }
                            }
                        }
                }
            }

            emitter.setCancellable {
                //textureView.surfaceTextureListener = null
            }
        }.distinctUntilChanged { barcode1, barcode2 ->
            barcode1.rawValue == barcode2.rawValue
        }
    }

    private fun getDisplaySize(): Size {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = Point()
        wm.defaultDisplay.getRealSize(p)

        return Size(p.x, p.y)
    }
}