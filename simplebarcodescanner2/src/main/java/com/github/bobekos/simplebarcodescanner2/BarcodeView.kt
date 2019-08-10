package com.github.bobekos.simplebarcodescanner2

import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.AttributeSet
import android.view.TextureView
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.github.bobekos.simplebarcodescanner2.camera.base.CameraSource
import com.github.bobekos.simplebarcodescanner2.camera.v1.Camera1Source
import com.github.bobekos.simplebarcodescanner2.camera.v2.Camera2Source
import com.github.bobekos.simplebarcodescanner2.overlay.BarcodeOverlay
import com.github.bobekos.simplebarcodescanner2.overlay.BarcodeRectOverlay
import com.github.bobekos.simplebarcodescanner2.overlay.OverlayBuilder
import com.github.bobekos.simplebarcodescanner2.scanner.BarcodeResult
import com.github.bobekos.simplebarcodescanner2.scanner.BarcodeScanner
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing
import com.github.bobekos.simplebarcodescanner2.utils.getDisplaySize
import com.github.bobekos.simplebarcodescanner2.utils.isNotDisposed
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

class BarcodeView : FrameLayout, LifecycleOwner {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private lateinit var textureView: TextureView
    private lateinit var lifecycleRegistry: LifecycleRegistry

    private val config = ScannerConfig()
    private val overlayBuilder = OverlayBuilder()
    private val barcodeScanner = BarcodeScanner(config)

    private val cameraSource: CameraSource by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Camera2Source(config, context.getDisplaySize())
        } else {
            Camera1Source(config, context.getDisplaySize())
        }
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    private fun init() {
        setBackgroundColor(Color.RED)

        lifecycleRegistry = LifecycleRegistry(this)

        textureView = TextureView(context)

        addView(textureView)
    }

    fun setFacing(facing: CameraFacing) = apply {
        config.lensFacing = facing
    }

    fun drawOverlay(barcodeOverlay: BarcodeOverlay = BarcodeRectOverlay(context)) = apply {
        config.barcodeOverlay = barcodeOverlay
    }

    fun enableFlash(isOn: Boolean) = apply {
        config.isFlashOn = isOn

        CameraSource.updateByConfig(config)
    }

    fun getObservable(): Observable<FirebaseVisionBarcode> {
        return Observable.create<BarcodeResult> { emitter ->
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(
                    surface: SurfaceTexture?,
                    width: Int,
                    height: Int
                ) {
                    //TODO
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                    lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
                    cameraSource.clear()

                    return true
                }

                override fun onSurfaceTextureAvailable(
                    surface: SurfaceTexture?,
                    width: Int,
                    height: Int
                ) {
                    lifecycleRegistry.markState(Lifecycle.State.CREATED)
                    lifecycleRegistry.markState(Lifecycle.State.STARTED)

                    overlayBuilder
                        .createOverlayView(this@BarcodeView, config.barcodeOverlay)
                        .checkOrientationAndFacing(context, config.lensFacing)

                    cameraSource
                        .build(this@BarcodeView, textureView, width, height)
                        .setConfigListener()

                    processFrame(width, height, emitter)
                }
            }

            if (textureView.isAvailable) {
                processFrame(textureView.width, textureView.height, emitter)
            }
        }.distinctUntilChanged { result1, result2 ->
            result1.rawValue == result2.rawValue
        }.filter {
            it is BarcodeResult.Data
        }.map {
            (it as BarcodeResult.Data).barcode
        }
    }

    private fun processFrame(width: Int, height: Int, emitter: ObservableEmitter<BarcodeResult>) {
        cameraSource.onImageProcessing { imageConverter, imageSize ->
            overlayBuilder.calculateOverlayScale(width, height, imageSize)

            barcodeScanner.processImage(imageConverter,
                barcodeResultListener = { barcodeResult ->
                    emitter.isNotDisposed { onNext(barcodeResult) }
                },
                overlayListener = { rectF, rawValue ->
                    emitter.isNotDisposed {
                        overlayBuilder.onBarcodeDetected(
                            rectF,
                            rawValue,
                            config.barcodeOverlay
                        )
                    }
                }
            )
        }
    }
}