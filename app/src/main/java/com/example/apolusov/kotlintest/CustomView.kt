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

    companion object {
        const val INITIAL_SCROLL = 0f
    }

    var currentScroll = INITIAL_SCROLL

    val series = listOf<PointD>()
    var currentSeries = listOf<PointD>()
    var pixelSeries = listOf<PointD>()
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
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidth = width.toFloat()
        viewHeight = height.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pixelSeries.forEach {
            canvas.drawCircle(it.x, it.y, 5f, paint)
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
        }
        invalidate()
    }

    fun scaleView(factor: Float) {
        pixelSeries = pixelSeries.map {
            it.scale(factor, viewWidth / 2, viewHeight / 2)
        }
        invalidate()
    }

    fun getData(distanceX: Float) {
        currentScroll = currentScroll + distanceX
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
                        getCalculatedX(it.x, maxX, viewWidth).translate(-1 * currentScroll),
                        getCalculatedY(it.y, maxY, viewHeight)
                    )
                }
                Timber.d("$viewWidth, $viewHeight")
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

    fun setData(data: List<PointD>) {
        currentSeries = data.slice(startPageLeft..startPageRight)
        currentScroll = getCalculatedX(startPageLeft.toFloat() + offsetLeft,  maxX, viewWidth)
        pixelSeries = currentSeries.map {
            Point(it.x, it.y, getCalculatedX(it.x, maxX, viewWidth) - currentScroll, getCalculatedY(it.y, maxY, viewHeight))
        }
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