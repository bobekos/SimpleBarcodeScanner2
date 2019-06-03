package com.github.bobekos.simplebarcodescanner2.camera.base

import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.TextureView
import android.view.WindowManager
import com.github.bobekos.simplebarcodescanner2.utils.DisplayRotation

abstract class CameraBuilder<T, X>(val ctx: Context) {

    private val imageProcessingThread = HandlerThread("CameraImageProcessingThread")

    lateinit var displayRotation: DisplayRotation
        private set

    abstract fun createPreview(textureView: TextureView, width: Int, height: Int): T

    abstract fun createImageAnalyzer(handler: Handler): X

    fun getPreview(textureView: TextureView, width: Int, height: Int): T {
        displayRotation = DisplayRotation(textureView.display)

        return createPreview(textureView, width, height)
    }

    fun getImageProcessor(): X {
        imageProcessingThread.start()

        return createImageAnalyzer(Handler(imageProcessingThread.looper))
    }

    fun getDisplaySize() : Size {
        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = Point()
        wm.defaultDisplay.getRealSize(p)

        return Size(p.x, p.y)
    }
}