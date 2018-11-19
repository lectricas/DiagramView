package com.example.apolusov.kotlintest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.widget.TextView
import timber.log.Timber
import kotlin.math.roundToInt


class CustomView : TextView {

    val path = Path()
    val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 3f
    }

    var scaleFactor = 1f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId)

    fun scaleView(scaleFactor: Float) {
        this.scaleFactor = scaleFactor
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val currentWidth = (MeasureSpec.getSize(widthMeasureSpec))
        val newWidth  = (currentWidth * scaleFactor).roundToInt()
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(newWidth, height)
    }

//    override fun onDraw(canvas: Canvas) {
//        Timber.d("onDraw $width, $height")
//        canvas.drawCircle(width / 2f, height / 2f, 100f, paint)
//    }
}