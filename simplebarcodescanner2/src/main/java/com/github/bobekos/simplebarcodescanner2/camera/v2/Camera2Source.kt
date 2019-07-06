package com.github.bobekos.simplebarcodescanner2.camera.v2

import android.media.Image
import android.util.Size
import android.view.TextureView
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class Camera2Source(private val config: ScannerConfig, displaySize: Size) {

    companion object {
        private val updateSubject = PublishSubject.create<ScannerConfig>()

        fun updateByConfig(config: ScannerConfig) {
            updateSubject.onNext(config)
        }
    }

    private val cameraBuilder = Camera2SourceBuilder(config, displaySize)

    private var preview: Preview? = null
    private var processor: Camera2ImageProcessor? = null

    private var updateDisposable: Disposable? = null

    fun build(lifecycleOwner: LifecycleOwner, textureView: TextureView, width: Int, height: Int) =
        apply {
            preview = cameraBuilder.getPreview(textureView, width, height)
            processor = cameraBuilder.getImageProcessor()

            CameraX.bindToLifecycle(lifecycleOwner, preview, processor?.imageAnalysis)
        }

    fun setConfigListener() = apply {
        enableFlash(config.isFlashOn)

        updateDisposable = updateSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    enableFlash(it.isFlashOn)
                }, {

                })
    }

    fun onImageProcessing(block: (image: Image, imageRotation: Int) -> Unit) {
        processor?.setImageProcessListener(block)
    }

    //TODO move to base camerasource
    fun clear() {
        updateDisposable?.dispose()
        CameraX.unbindAll()
    }

    //TODO move to
    fun enableFlash(isOn: Boolean) {
        preview?.enableTorch(isOn)
    }
}