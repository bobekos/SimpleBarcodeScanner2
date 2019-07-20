package com.github.bobekos.simplebarcodescanner2.camera.v1

import android.hardware.Camera
import android.util.Size
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

class Camera1Preview(val camera: Camera) : LifecycleObserver {

    private val cameraParameters: Camera.Parameters = camera.parameters

    init {
        //TODO FOCUS MODE only by back
        cameraParameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
    }

    fun setPreviewSize(previewSize: Size) = apply {
        cameraParameters.setPreviewSize(previewSize.width, previewSize.height)
    }

    fun setRotation(rotation: Int) = apply {
        cameraParameters.setRotation(rotation)
    }

    fun build() = apply {
        camera.parameters = cameraParameters
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
}