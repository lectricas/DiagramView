package com.example.apolusov.kotlintest

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.view.MotionEventCompat
import android.view.*
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.OverScroller
import timber.log.Timber
import kotlin.math.roundToInt


class CustomView : View {

    private var newDataListener: NewDataListener
    private var scrollDetector: GestureDetector
    private var scaleDetector: ScaleGestureDetector

    companion object {
        const val INITIAL_SCROLL = 0
    }

    var currentScroll = INITIAL_SCROLL

    private var series = listOf<PointM>()
    private var currentSeries = listOf<PointM>()
    private var pixelSeries = listOf<PointD>()
    private var maxWidthInPoints = 0
    private var maxHeightInPoints = 0
    private var viewWidthInPixels = 0
    private var viewHeightInPixels = 0

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
    }

    //what this variable Do???
    var currentItemCount = 0

    var startPageLeft = 0
    var sliceLeft = 0
    var sliceRight = 0
    var page = 0

    var mLastTouchX = 0f
    var mLastTouchY = 0f
    var mActivePointerId = 0
    var mPosX = 0f
    var mPosY = 0f

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            moveView(distanceX.reverseSign().roundToInt())
            getData(distanceX.roundToInt())

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
        maxWidthInPoints = defaultWidth
        maxHeightInPoints = defaultHeight
        currentItemCount = maxWidthInPoints * 3
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidthInPixels = width
        viewHeightInPixels = height
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

    fun moveView(distanceX: Int) {
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

    fun getData(distanceX: Int) {
        currentScroll = currentScroll + distanceX
        if (currentScroll > viewWidthInPixels) {
            Timber.d("$currentScroll")
        }
        //visible10
        //all30
        val currentScrollInPoints = getCalculatedX(currentScroll, viewWidthInPixels, maxWidthInPoints)
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
    fun getCalculatedX(oldX: Int, oldWidth: Int, newWidth: Int): Int {
//        deltaX = deltaX + getCalculatedX(distanceX, viewWidthInPixels, maxWidthInPoints)
        return (oldX.toFloat() / oldWidth * newWidth).roundToInt()
    }

    //move from real world coordinates to the viewport and vice versa
    fun getCalculatedY(oldY: Int, oldHight: Int, newHight: Int): Int {
        return (newHight - oldY.toFloat() / oldHight * newHight).roundToInt()
    }

    interface NewDataListener {
        fun onNewData(point: PointM)
    }

    fun setData(data: List<PointM>) {
        if (series.isEmpty()) {
            series = data
            currentSeries = series.subList(30, 60)
            currentScroll = currentScroll + getCalculatedX(40,  maxWidthInPoints, viewWidthInPixels)
        } else {
            TODO("not implemented when there is some data")
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

private fun Int.translate(distance: Int) = this + distance

private fun PointD.scale(scaleFactor: Float, centerX: Int, centerY: Int): PointD {
    val tX = this.x - centerX
    val tY = this.y - centerY
    val sX = scaleFactor * tX
    val sY = scaleFactor * tY
    val nX = sX + centerX
    val nY = sY + centerY
    return PointD(nX.roundToInt(), nY.roundToInt(), this.text)
}