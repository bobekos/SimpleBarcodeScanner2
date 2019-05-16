package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.content.Context
import android.graphics.Point
import android.media.Image
import android.util.Size
import android.view.TextureView
import android.view.WindowManager
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import com.github.bobekos.simplebarcodescanner2.ScannerConfig

class Camera2Source(private val ctx: Context, config: ScannerConfig) {

    private val cameraBuilder = Camera2SourceBuilder(getDisplaySize(), config)

    private lateinit var preview: Preview
    private lateinit var processor: Camera2ImageProcessor

    fun build(lifecycleOwner: LifecycleOwner, textureView: TextureView, width: Int, height: Int) = apply {
        preview = cameraBuilder.getPreview(textureView, width, height)
        processor = cameraBuilder.getImageProcessor()

        CameraX.bindToLifecycle(lifecycleOwner, preview, processor.imageAnalysis)
    }

    fun onImageProcessing(block: (image: Image, imageRotation: Int) -> Unit) {
        processor.setImageProcessListener(block)
    }

    fun clear() {
        CameraX.unbindAll()
    }

    //TODO move
    private fun getDisplaySize() : Size {
        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = Point()
        wm.defaultDisplay.getRealSize(p)

        return Size(p.x, p.y)
    }

}