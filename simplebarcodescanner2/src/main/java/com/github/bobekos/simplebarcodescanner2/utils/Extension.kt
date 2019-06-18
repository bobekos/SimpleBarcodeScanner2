package com.github.bobekos.simplebarcodescanner2.utils

import android.graphics.RectF
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import io.reactivex.ObservableEmitter

fun Int.fdiv(i: Int): Float = this / i.toFloat()

inline fun <T> ObservableEmitter<T>.isNotDisposed(block: ObservableEmitter<T>.() -> Unit) {
    if (!isDisposed) {
        block()
    }
}

fun FirebaseVisionBarcode.getBoundingBoxF(): RectF {
    return RectF(boundingBox)
}

fun FirebaseVisionBarcode.getRawValueOrEmpty(): String {
    return rawValue ?: ""
}