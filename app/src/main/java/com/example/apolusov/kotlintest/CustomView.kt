package com.example.apolusov.kotlintest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.*
import timber.log.Timber
import java.util.*


class CustomView : View {

    private var newDataListener: NewDataListener
    private var scrollDetector: GestureDetector
    private var scaleDetector: ScaleGestureDetector

    val series = (0..100).map { Point(it.toFloat(), 5f, 0f, 0f) }.toMutableList()
    var currentSeries = mutableListOf<Point>()
    var pixelSeries = mutableListOf<Point>()
    val maxX = 10f
    val maxY = 10f
    var viewWidth = 0f
    var viewHeight = 0f
    var deltaX = 0f
    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
    }

    var startPageLeft = 40
    var startPageRight = 60
    var scrolledDelta = 0f
    var page = 0

    var sliceLeft = 0
    var sliceRight = 0

    var offsetLeft = (startPageRight - startPageLeft) / 2 - maxX / 2



    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            moveView(distanceX.reverseSign())
            getData(distanceX)
            return true
        }
    }

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleView(detector.scaleFactor)
            return true
        }
    }

    constructor(context: Context, newDataListener: NewDataListener) : super(context) {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        scrollDetector = GestureDetector(context, scrollListener)
        scaleDetector = ScaleGestureDetector(context, scaleListener)
        this.newDataListener = newDataListener
        currentSeries = series.slice(startPageLeft..startPageRight).toMutableList()

    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidth = width.toFloat()
        viewHeight = height.toFloat()
        scrolledDelta = getCalculatedX(startPageLeft.toFloat() + offsetLeft,  maxX, viewWidth)
        pixelSeries = currentSeries.map {
            Point(
                it.x,
                it.y,
                getCalculatedX(it.x, maxX, viewWidth) - scrolledDelta,
                getCalculatedY(it.y, maxY, viewHeight)
            )
        }
            .toMutableList()
        Timber.d("onLayout")
        Timber.d("$viewWidth, $viewHeight")
        Timber.d("$scrolledDelta")
        Timber.d("$pixelSeries")
        Timber.d("onLayoutEnd")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pixelSeries.forEach {
            canvas.drawText(" ${it.x}, ${it.y}", it.pixelX, it.pixelY, paint)
            canvas.drawCircle(it.pixelX, it.pixelY, 5f, paint)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        scrollDetector.onTouchEvent(event)
        scaleDetector.onTouchEvent(event)
        return true
    }

    fun moveView(distanceX: Float) {
        pixelSeries = pixelSeries.map {
            Point(it.x, it.y, it.pixelX.translate(distanceX), it.pixelY)
        }.toMutableList()
        invalidate()
    }

    fun scaleView(factor: Float) {
        pixelSeries = pixelSeries.map {
            it.scale(factor, viewWidth / 2, viewHeight / 2)
        }.toMutableList()
        invalidate()
    }

    fun getData(distanceX: Float) {
        scrolledDelta = scrolledDelta + distanceX
        deltaX = deltaX + getCalculatedX(distanceX, viewWidth, maxX)
        if (page != deltaX.toInt()) {
            page = deltaX.toInt()
        } else {
            return
        }

        if (page.rem(4) == 0) {

            if (startPageLeft + page != sliceLeft && sliceRight != startPageRight + page) {
                sliceLeft = startPageLeft + page
                sliceRight = startPageRight + page
                currentSeries = series.slice(sliceLeft..sliceRight).toMutableList()
                pixelSeries = currentSeries.map {
                    Point(
                        it.x,
                        it.y,
                        getCalculatedX(it.x, maxX, viewWidth).translate(-1 * scrolledDelta),
                        getCalculatedY(it.y, maxY, viewHeight)
                    )
                }
                    .toMutableList()
                Timber.d("$viewWidth, $viewHeight")
                Timber.d("$scrolledDelta")
                Timber.d("$pixelSeries")
                invalidate()
            }
        }
    }

    //move from real world coordinates to the viewport and vice versa
    fun getCalculatedX(oldX: Float, oldWidth: Float, newWidth: Float): Float {
        return oldX / oldWidth * newWidth
    }

    //move from real world coordinates to the viewport and vice versa
    fun getCalculatedY(oldY: Float, oldHight: Float, newHight: Float): Float {
        return newHight - oldY / oldHight * newHight
    }

    interface NewDataListener {
        fun onNewData(point: PointD)
    }
}

private fun Float.reverseSign(): Float = -this

private fun Float.translate(distance: Float) = this + distance

private fun Point.scale(scaleFactor: Float, centerX: Float, centerY: Float): Point {
    val tX = this.pixelX - centerX
    val tY = this.pixelY - centerY
    val sX = scaleFactor * tX
    val sY = scaleFactor * tY
    val nX = sX + centerX
    val nY = sY + centerY
    return Point(this.x, this.y, nX, nY)
}