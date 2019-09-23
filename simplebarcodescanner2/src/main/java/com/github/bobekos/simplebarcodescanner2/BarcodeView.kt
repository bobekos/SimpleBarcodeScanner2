package com.github.bobekos.simplebarcodescanner2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.AttributeSet
import android.view.TextureView
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
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
import com.github.bobekos.simplebarcodescanner2.utils.Size
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

    private val barcodeScanner: BarcodeScanner by lazy {
        BarcodeScanner(context, config)
    }

    private val cameraSource: CameraSource by lazy {
        if (config.isDefaultScannerResolution) {
            config.scannerResolution = getDisplaySize()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Camera2Source(config, getDisplaySize())
        } else {
            Camera1Source(config, getDisplaySize())
        }
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    private fun init() {
        setBackgroundColor(Color.BLACK)

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

    fun setBarcodeFormats(vararg format: Int) = apply {
        config.barcodeFormat = format.toList()
    }

    fun setScannerResolution(width: Int, height: Int) = apply {
        config.isDefaultScannerResolution = false
        config.scannerResolution = Size(width, height)
    }

    fun playBeepSound(isEnabled: Boolean = true) = apply {
        config.playBeepSound = isEnabled
    }

    fun setVibration(duration: Long = 500L) = apply {
        config.vibrate = duration
    }

    fun getObservable(): Observable<FirebaseVisionBarcode> {
        return Observable.create<BarcodeResult> { emitter ->
            if (!hasPermission()) {
                emitter.isNotDisposed {
                    onError(SecurityException("Permission denied (missing Camera permission)"))
                }

                return@create
            }

            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(
                        surface: SurfaceTexture?,
                        width: Int,
                        height: Int
                ) {
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                    lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
                    barcodeScanner.close()
                    cameraSource.clear()

                    return true
                }

                override fun onSurfaceTextureAvailable(
                        surface: SurfaceTexture?,
                        width: Int,
                        height: Int
                ) {
                    lifecycleRegistry.currentState = Lifecycle.State.CREATED
                    lifecycleRegistry.currentState = Lifecycle.State.STARTED

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
        }.doOnNext {
            barcodeScanner.onBarcodeDetection()
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

    private fun getDisplaySize(): Size {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = Point()
        wm.defaultDisplay.getSize(p)

        return Size(p.x, p.y)
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }
}