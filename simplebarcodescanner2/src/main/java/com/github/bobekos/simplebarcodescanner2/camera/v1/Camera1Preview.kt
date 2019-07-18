package com.github.bobekos.simplebarcodescanner2.camera.v1

import android.hardware.Camera
import android.util.Size
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.math.abs

class Camera1Preview(val camera: Camera, displaySize: Size) : LifecycleObserver {

    private var previewSize: Size

    init {
        val parameters = camera.parameters

        previewSize = getValidPreviewSize(displaySize)

        parameters.setPreviewSize(previewSize.width, previewSize.height)
        parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE

        camera.parameters = parameters
    }

    fun setOnPreviewOutputListener(block: (camera: Camera, previewSize: Size) -> Unit) {
        block(camera, previewSize)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        camera.startPreview()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        camera.stopPreview()
    }

    fun bindToLifecycle(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private fun getValidPreviewSize(size: Size): Size {
        val supportedPreviewSize = camera.parameters.supportedPreviewSizes

        var result = size
        var minDiff = Int.MAX_VALUE

        supportedPreviewSize.forEach {
            val diff = abs(it.width - size.width) +
                    abs(it.height - size.height)
            if (diff < minDiff) {
                result = Size(it.width, it.height)
                minDiff = diff
            }
        }

        return result
    }
}