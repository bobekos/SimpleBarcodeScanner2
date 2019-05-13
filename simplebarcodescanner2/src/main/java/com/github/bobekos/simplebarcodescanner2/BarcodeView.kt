package com.github.bobekos.simplebarcodescanner2

import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Size
import android.view.TextureView
import android.widget.FrameLayout
import androidx.camera.core.CameraX
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.github.bobekos.simplebarcodescanner2.camera.v2.Camera2PreviewBuilder

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

    private val defaultConfig by lazy {
        ScannerConfig(Size(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.heightPixels))
    }

    private val previewBuilder by lazy {
        Camera2PreviewBuilder(defaultConfig)
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    private fun init() {
        setBackgroundColor(Color.BLACK)

        lifecycleRegistry = LifecycleRegistry(this)

        val textureView = TextureView(context).apply {
            surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
                    val test = ""
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                    val test = ""
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                    lifecycleRegistry.markState(Lifecycle.State.DESTROYED)

                    return true
                }

                override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                    lifecycleRegistry.markState(Lifecycle.State.CREATED)
                    lifecycleRegistry.markState(Lifecycle.State.STARTED)

                    if (this@apply.isAvailable) {
                        previewBuilder.createPreview(this@apply, right - left, bottom - top) {
                            CameraX.bindToLifecycle(this@BarcodeView, it)
                        }
                    }
                }
            }
        }

        addView(textureView)
    }
}