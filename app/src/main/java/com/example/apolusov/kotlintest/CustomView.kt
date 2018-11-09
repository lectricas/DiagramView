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
        const val INITIAL_SCROLL = 0
    }

    private var series = listOf<PointM>()
    private var currentSeries = listOf<PointM>()
    private var pixelSeries = listOf<PointD>()
    private var maxWidthInPoints = 0
    private var maxHeightInPoints = 0
    private var viewWidthInPixels = 0f
    private var viewHeightInPixels = 0f

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
    }

    private var currentScroll = 0f

    //what this variable Do???

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (distanceX > 0) {
                getRightContent(distanceX)
            } else {
                getLeftContent(distanceX)
            }
            return true
        }
    }

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleView(detector.scaleFactor)
            return true
        }
    }

    constructor(context: Context, newDataListener: NewDataListener, defaultWidth: Int, defaultHeight: Int) : super(
        context
    ) {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        scrollDetector = GestureDetector(context, scrollListener)
        scaleDetector = ScaleGestureDetector(context, scaleListener)
        this.newDataListener = newDataListener
        maxWidthInPoints = defaultWidth
        maxHeightInPoints = defaultHeight
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidthInPixels = width.toFloat()
        viewHeightInPixels = height.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pixelSeries.forEach {
            canvas.drawText(it.text.toString(), it.x.toFloat(), it.y.toFloat(), paint)
            canvas.drawCircle(it.x.toFloat(), it.y.toFloat(), 5f, paint)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        scrollDetector.onTouchEvent(event)
        scaleDetector.onTouchEvent(event)
        return true
    }

    fun getLeftContent(distanceX: Float) {
        val leftPointX = pixelSeries[0].x
        val toScroll: Float
        if (leftPointX > distanceX) {
            toScroll = leftPointX
        } else {
            toScroll = distanceX
        }

        pixelSeries = pixelSeries.map {
            PointD(it.x.translate(toScroll), it.y, it.text)
        }
        currentScroll += toScroll
//        Timber.d("currentScroll = $currentScroll first $leftPointX")
        invalidate()
    }

    fun getRightContent(distanceX: Float) {
        val pointToLoad = pixelSeries[pixelSeries.size - maxWidthInPoints].x
        val toScroll: Float
        if (distanceX > pointToLoad - viewWidthInPixels) {
            toScroll = pointToLoad - viewWidthInPixels
            val startItem = pixelSeries.last().text + 1
            val shift = startItem - 20
            pixelSeries = pixelSeries.takeLast(30).plus(
                series.subList(startItem, startItem + 30)
                    .map {
                        PointD(
                            getCalculatedX(it.x.toFloat() - shift, maxWidthInPoints.toFloat(), viewWidthInPixels) + toScroll,
                            getCalculatedY(it.y.toFloat(), maxHeightInPoints.toFloat(), viewHeightInPixels),
                            it.x
                        )
                    }
            )
        } else {
            toScroll = distanceX
        }
        pixelSeries = pixelSeries.map {
            PointD(it.x.translate(toScroll), it.y, it.text)
        }
        currentScroll += toScroll
//        Timber.d("$currentScroll, $rightPointX")
        invalidate()
    }


    fun scaleView(factor: Float) {
//        pixelSeries = pixelSeries.map {
//            it.scale(factor, viewWidthInPixels / 2, viewHeightInPixels / 2)
//        }
//        invalidate()
    }

    fun getData(distanceX: Int) {
//        currentScroll = currentScroll + distanceX
        //visible10
        //all30
//        val currentScrollInPoints = getCalculatedX(currentScroll, viewWidthInPixels, maxWidthInPoints)
//        Timber.d(currentScrollInPoints.toString())
//        if (page != deltaX.toInt()) {
//            page = deltaX.toInt()
//        } else {
//            return
//        }
//
//        if (page.rem(4) == 0) {
//            if (startPageLeft + page != sliceLeft && sliceRight != startPageLeft + currentItemCount + page) {
//                sliceLeft = startPageLeft + page
//                sliceRight = startPageLeft + currentItemCount + page
//                currentSeries = series.subList(sliceLeft, sliceRight).toMutableList()
//                pixelSeries = currentSeries.map {
//                    PointD(
//                        getCalculatedX(it.x, maxWidthInPoints, viewWidthInPixels).translate(-1 * currentScroll),
//                        getCalculatedY(it.y, maxHeightInPoints, viewHeightInPixels),
//                        it.x
//                    )
//                }
//                invalidate()
//            }
//        }
    }

    //move from real world coordinates to the viewport and vice versa
    fun getCalculatedX(oldX: Float, oldWidth: Float, newWidth: Float): Float {
//        deltaX = deltaX + getCalculatedX(distanceX, viewWidthInPixels, maxWidthInPoints)
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
            currentSeries = series.subList(0, 30)
        } else {
            TODO("not implemented when there is some data")
        }
        pixelSeries = currentSeries.map {
            PointD(
                getCalculatedX(it.x.toFloat(), maxWidthInPoints.toFloat(), viewWidthInPixels),
                getCalculatedY(it.y.toFloat(), maxHeightInPoints.toFloat(), viewHeightInPixels),
                it.x
            )
        }
        invalidate()
    }
}

private fun Float.translate(distance: Float) = this - distance

private fun PointD.scale(scaleFactor: Float, centerX: Int, centerY: Int): PointD {
    val tX = this.x - centerX
    val tY = this.y - centerY
    val sX = scaleFactor * tX
    val sY = scaleFactor * tY
    val nX = sX + centerX
    val nY = sY + centerY
    return PointD(nX, nY, this.text)
}