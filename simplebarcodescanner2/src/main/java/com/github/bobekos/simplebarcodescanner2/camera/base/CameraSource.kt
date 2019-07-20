package com.github.bobekos.simplebarcodescanner2.camera.base

import android.view.TextureView
import androidx.lifecycle.LifecycleOwner
import com.github.bobekos.simplebarcodescanner2.ScannerConfig
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

abstract class CameraSource(private val config: ScannerConfig) {

    companion object {
        private val updateSubject = PublishSubject.create<ScannerConfig>()

        fun updateByConfig(config: ScannerConfig) {
            updateSubject.onNext(config)
        }
    }

    private var updateDisposable: Disposable? = null

    abstract fun build(
        lifecycleOwner: LifecycleOwner,
        textureView: TextureView,
        width: Int,
        height: Int
    ): CameraSource

    abstract fun onImageProcessing(block: (imageConverter: CameraImageConverter) -> Unit)

    open fun clear() {
        updateDisposable?.dispose()
    }

    open fun onConfigChange(config: ScannerConfig) {
    }

    fun setConfigListener(): CameraSource {
        onConfigChange(config)

        updateDisposable = updateSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    onConfigChange(config)
                },
                {
                    //TODO
                })

        return this
    }
}