package com.example.apolusov.kotlintest.withadapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.example.apolusov.kotlintest.MainActivity.Companion.VIEW_WIDTH
import com.example.apolusov.kotlintest.MainActivity.Companion.ZOOM
import com.example.apolusov.kotlintest.PointD
import com.example.apolusov.kotlintest.PointM
import timber.log.Timber
import kotlin.math.roundToInt

class GraphicView : View {

    private var viewWidthInPixels = 0
    private var viewHeightInPixels = 0
//    private var scaleDetector: ScaleGestureDetector

//    private var scaleFactor = 1f

    val series = (0..100).map { PointM(it, 5) }

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 10f
    }

//    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//        override fun onScale(detector: ScaleGestureDetector): Boolean {
//            increaseScale(detector.scaleFactor)
//            return true
//        }
//    }

    constructor(context: Context) : this(context, null) {
//        scaleDetector = ScaleGestureDetector(context, scaleListener)
    }
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
//        scaleDetector = ScaleGestureDetector(context, scaleListener)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        scaleDetector = ScaleGestureDetector(context, scaleListener)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(VIEW_WIDTH.toInt(), 1000)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidthInPixels = width
        viewHeightInPixels = height
    }

    fun increaseScale(scaleFactor: Float) {
//        this.scaleFactor = this.scaleFactor * scaleFactor
        requestLayout()
    }


    fun getCalculatedX(oldX: Int, oldWidth: Int, newWidth: Int): Int {
//        deltaX = deltaX + getCalculatedX(distanceX, viewWidthInPixels, maxWidthInPoints)
        return (oldX.toFloat() / oldWidth * newWidth).roundToInt()
    }

    //move from real world coordinates to the viewport and vice versa
    fun getCalculatedY(oldY: Int, oldHight: Int, newHight: Int): Int {
        return (newHight - oldY.toFloat() / oldHight * newHight).roundToInt()
    }

    override fun onDraw(canvas: Canvas) {
        (0..1000).forEach { y ->
            (0..1000).forEach { x ->
                if (x.rem(100) == 0 && y.rem(100) == 0) {
                    canvas.drawText("$x, $y", x.toFloat(), y.toFloat(), paint)
                }
            }
        }
    }
}

private fun PointD.scale(scaleFactor: Float, centerX: Int, centerY: Int): PointD {
    val tX = this.x - centerX
    val tY = this.y - centerY
    val sX = scaleFactor * tX
    val sY = scaleFactor * tY
    val nX = sX + centerX
    val nY = sY + centerY
    return PointD(nX, nY, this.text)
}