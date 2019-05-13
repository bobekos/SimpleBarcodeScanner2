package com.github.bobekos.simplebarcodescanner2

import android.util.Size
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing

data class ScannerConfig(
    val previewSize: Size,
    val lensFacing: CameraFacing = CameraFacing.BACK
)