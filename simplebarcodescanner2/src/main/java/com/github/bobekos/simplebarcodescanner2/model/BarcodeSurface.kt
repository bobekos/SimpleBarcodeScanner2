package com.github.bobekos.simplebarcodescanner2.model

import android.view.TextureView

sealed class BarcodeSurface {
    object Disposed : BarcodeSurface()
    data class MetaData(val textureView: TextureView, val width: Int, val height: Int) : BarcodeSurface()
}