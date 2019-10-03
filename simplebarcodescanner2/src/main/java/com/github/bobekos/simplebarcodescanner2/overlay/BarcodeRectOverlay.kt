package com.github.bobekos.simplebarcodescanner2.overlay

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class BarcodeRectOverlay : View, BarcodeOverlay {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var rect: RectF? = null

    private val paint by lazy {
        Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, context?.resources?.displayMetrics)
        }
    }

    init {
        setWillNotDraw(false)
    }

    override fun onUpdate(posRect: RectF, barcodeValue: String) {
        rect = posRect
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        rect?.let {
            canvas?.drawRect(it, paint)
        }
    }
}