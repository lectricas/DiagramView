package com.example.apolusov.kotlintest.withadapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import timber.log.Timber
import java.util.*

class GraphicView : View {

    private var viewWidthInPixels = 0
    private var viewHeightInPixels = 0
//    private lateinit var scaleDetector: ScaleGestureDetector

    private var scaleFactor = 1f

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 10f
    }

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            increaseScale(detector.scaleFactor)
            return true
        }
    }

    constructor(context: Context) : this(context, null) {
//        scaleDetector = ScaleGestureDetector(context, scaleListener)
    }
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
//        scaleDetector = ScaleGestureDetector(context, scaleListener)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        scaleDetector = ScaleGestureDetector(context, scaleListener)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidthInPixels = width
        viewHeightInPixels = height
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        Timber.d(event.toString())
//        scaleDetector.onTouchEvent(event)
        return true
    }

    fun increaseScale(scaleFactor: Float) {
        this.scaleFactor = scaleFactor
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        (0..viewHeightInPixels).forEach { y ->
            (0..viewWidthInPixels).forEach { x ->
                if (x.rem(100) == 0 && y.rem(100) == 0) {
                    canvas.drawText("$x, $y", x.toFloat(), y.toFloat(), paint)
                }
            }
        }
    }
}