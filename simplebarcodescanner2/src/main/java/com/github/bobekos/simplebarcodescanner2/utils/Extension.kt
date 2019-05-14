package com.github.bobekos.simplebarcodescanner2.utils

import io.reactivex.ObservableEmitter

fun Int.fdiv(i: Int): Float = this / i.toFloat()

inline fun <T> ObservableEmitter<T>.isNotDisposed(block: ObservableEmitter<T>.() -> Unit) {
    if (!isDisposed) {
        block()
    }
}