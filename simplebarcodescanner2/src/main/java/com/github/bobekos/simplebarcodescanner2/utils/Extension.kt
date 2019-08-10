package com.github.bobekos.simplebarcodescanner2.utils

import android.content.Context
import android.graphics.Point
import android.graphics.RectF
import android.view.WindowManager
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

fun Context.getDisplaySize() : Size {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val p = Point()
    wm.defaultDisplay.getSize(p)

    return Size(p.x, p.y)
}