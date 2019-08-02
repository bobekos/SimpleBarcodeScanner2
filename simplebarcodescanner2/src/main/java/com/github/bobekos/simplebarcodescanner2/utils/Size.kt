package com.github.bobekos.simplebarcodescanner2.utils

import android.annotation.TargetApi
import android.os.Build
import android.util.Size

data class Size(val width: Int, val height: Int) {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun getCamera2Size(): Size {
        return Size(width, height)
    }

}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Size.toSimpleSize() : com.github.bobekos.simplebarcodescanner2.utils.Size {
    return com.github.bobekos.simplebarcodescanner2.utils.Size(this.width, this.height)
}