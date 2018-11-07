package com.example.apolusov.kotlintest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.*
import timber.log.Timber


class CustomView : View {

    private var newDataListener: NewDataListener
    private var scrollDetector: GestureDetector
    private var scaleDetector: ScaleGestureDetector

    companion object {
        const val INITIAL_SCROLL = 0f
    }

    var currentScroll = INITIAL_SCROLL

    private var series = listOf<PointM>()
    private var currentSeries = listOf<PointM>()
    private var pixelSeries = listOf<PointD>()
    private var maxWidthInPoints = 0f
    private var maxHeightInPoints = 0f
    private var viewWidthInPixels = 0f
    private var viewHeightInPixels = 0f

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
    }

    //what this variable Do???
    var startPageLeft = 0
    var startPageRight = 0
    var sliceLeft = 0
    var sliceRight = 0
    var deltaX = 0f
    var page = 0

    var offsetLeft = (startPageRight - startPageLeft) / 2 - maxWidthInPoints / 2

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

    constructor(context: Context, newDataListener: NewDataListener, defaultWidth: Int, defaultHeight: Int) : super(context) {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        scrollDetector = GestureDetector(context, scrollListener)
        scaleDetector = ScaleGestureDetector(context, scaleListener)
        this.newDataListener = newDataListener
        maxWidthInPoints = defaultWidth.toFloat()
        maxHeightInPoints = defaultHeight.toFloat()
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidthInPixels = width.toFloat()
        viewHeightInPixels = height.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pixelSeries.forEach {
            canvas.drawText(it.text.toString(), it.x, it.y, paint)
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
            PointD(it.x.translate(distanceX), it.y, it.text)
        }
        invalidate()
    }

    fun scaleView(factor: Float) {
        pixelSeries = pixelSeries.map {
            it.scale(factor, viewWidthInPixels / 2, viewHeightInPixels / 2)
        }
        invalidate()
    }

    fun getData(distanceX: Float) {
        currentScroll = currentScroll + distanceX
        deltaX = deltaX + getCalculatedX(distanceX, viewWidthInPixels, maxWidthInPoints)
        if (page != deltaX.toInt()) {
            page = deltaX.toInt()
        } else {
            return
        }

        if (page.rem(4) == 0) {
            if (startPageLeft + page != sliceLeft && sliceRight != startPageRight + page) {
                sliceLeft = startPageLeft + page
                sliceRight = startPageRight + page
                currentSeries = series.subList(sliceLeft, sliceRight).toMutableList()
                pixelSeries = currentSeries.map {
                    PointD(
                        getCalculatedX(it.x, maxWidthInPoints, viewWidthInPixels).translate(-1 * currentScroll),
                        getCalculatedY(it.y, maxHeightInPoints, viewHeightInPixels),
                        it.x
                    )
                }
                Timber.d("$viewWidthInPixels, $viewHeightInPixels")
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
        fun onNewData(point: PointM)
    }

    fun setData(data: List<PointM>) {
        if (series.isEmpty()) {
            series = data
            //todo set data properly
//            startPageLeft =
            currentSeries = series.subList(startPageLeft, startPageRight)
            currentScroll = currentScroll + getCalculatedX(startPageLeft.toFloat() + offsetLeft,  maxWidthInPoints, viewWidthInPixels)
        } else {

        }
        pixelSeries = currentSeries.map {
            PointD(
                getCalculatedX(it.x, maxWidthInPoints, viewWidthInPixels) - currentScroll,
                getCalculatedY(it.y, maxHeightInPoints, viewHeightInPixels),
                it.x
            )
        }
        invalidate()
    }
}

private fun Float.reverseSign(): Float = -this

private fun Float.translate(distance: Float) = this + distance

private fun PointD.scale(scaleFactor: Float, centerX: Float, centerY: Float): PointD {
    val tX = this.x - centerX
    val tY = this.y - centerY
    val sX = scaleFactor * tX
    val sY = scaleFactor * tY
    val nX = sX + centerX
    val nY = sY + centerY
    return PointD(nX, nY, this.text)
}