package com.example.apolusov.kotlintest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.*
import com.example.apolusov.kotlintest.daydata.DiabetPoint
import com.example.apolusov.kotlintest.daydata.GraphPixelPoint
import timber.log.Timber
import java.util.*


class CustomView : View {

    private var newDataListener: NewDataListener
    private var scrollDetector: GestureDetector
    private var scaleDetector: ScaleGestureDetector

    companion object {
        const val INITIAL_SCROLL = 0
    }

    private var series = listOf<DiabetPoint>()
    private var pixelSeries = listOf<GraphPixelPoint>()
    private var maxWidthInPoints = 0f
    private var maxHeightInPoints = 0f
    private var viewWidthInPixels = 0f
    private var viewHeightInPixels = 0f

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
    }

    private var currentScroll = 0f

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
//            if (canScroll()) {
//                if (distanceX >= 0 && canGetRightContent(distanceX)) {
//                    getRightContent(distanceX)
//                } else if (distanceX < 0) {
//                    getLeftContent(distanceX)
//                }
//            }
            moveView(distanceX)
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
            canvas.drawText("${it.calendar.get(Calendar.DAY_OF_MONTH)}, ${it.calendar.get(Calendar.HOUR_OF_DAY)}", it.x, it.y, paint)
            canvas.drawCircle(it.x, it.y, 10f, paint)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        scrollDetector.onTouchEvent(event)
        scaleDetector.onTouchEvent(event)
        return true
    }

//    fun canGetRightContent(distanceX: Float): Boolean {
//        val canGetRightContent = pixelSeries.size > 29
//        if (canGetRightContent) {
//            return true
//        } else {
//            val stopPoint = pixelSeries.last().x
//            val toScroll: Float
//            if (distanceX > stopPoint - viewWidthInPixels) {
//                toScroll = stopPoint - viewWidthInPixels
//            } else {
//                toScroll = distanceX
//            }
//            pixelSeries = pixelSeries.map {
//                PointD(it.x.translate(toScroll), it.y, it.text)
//            }
//            currentScroll += toScroll
//            invalidate()
//            return false
//        }
//    }
//
//    fun getRightContent(distanceX: Float) {
//        val pointToLoad = pixelSeries[pixelSeries.size - maxWidthInPoints].x
//        val toScroll: Float
//        if (distanceX > pointToLoad - viewWidthInPixels) {
//            toScroll = pointToLoad - viewWidthInPixels
//            val startItem = pixelSeries.last().text + 1
//            val shift = startItem - 20
//            pixelSeries = pixelSeries.takeLast(30).plus(
//                series.subList(startItem, startItem + 30)
//                    .map {
//                        PointD(
//                            getCalculatedX(it.x.toFloat() - shift, maxWidthInPoints.toFloat(), viewWidthInPixels) + toScroll,
//                            getCalculatedY(it.y.toFloat(), maxHeightInPoints.toFloat(), viewHeightInPixels),
//                            it.x
//                        )
//                    }
//            )
//        } else {
//            toScroll = distanceX
//        }
//        pixelSeries = pixelSeries.map {
//            PointD(it.x.translate(toScroll), it.y, it.text)
//        }
//        currentScroll += toScroll
//        invalidate()
//    }
//
//    fun getLeftContent(distanceX: Float) {
//        val pointToLoad = pixelSeries[maxWidthInPoints].x
//        val toScroll: Float
//        if (Math.abs(pointToLoad) < Math.abs(distanceX)) {
//            toScroll = pointToLoad
//            val endItem = pixelSeries.first().text
//            val shift = endItem + 10
//            pixelSeries = series.subList(endItem - 30, endItem)
//                .map {
//                    PointD(
//                        getCalculatedX(it.x.toFloat() - shift, maxWidthInPoints.toFloat(), viewWidthInPixels),
//                        getCalculatedY(it.y.toFloat(), maxHeightInPoints.toFloat(), viewHeightInPixels),
//                        it.x
//                    )
//                }.plus(pixelSeries.take(30))
//
//            Timber.d("$pixelSeries")
//        } else {
//            toScroll = distanceX
//        }
//
//        pixelSeries = pixelSeries.map {
//            PointD(it.x.translate(toScroll), it.y, it.text)
//        }
//        currentScroll += toScroll
//        invalidate()
//    }

    fun canScroll() = pixelSeries.size > maxWidthInPoints


    fun scaleView(factor: Float) {
//        pixelSeries = pixelSeries.map {
//            it.scale(factor, viewWidthInPixels / 2, viewHeightInPixels / 2)
//        }
//        invalidate()
    }

    fun moveView(distanceX: Float) {
        pixelSeries = pixelSeries.map {
                       GraphPixelPoint(it.x.translate(distanceX), it.y, it.substanceAmount, it.type, it.calendar)
        }
        invalidate()
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

    fun setData(data: List<DiabetPoint>) {
        if (series.isEmpty()) {
            series = data
        } else {
            TODO("not implemented when there is some data")
        }
        val seriesToTake = series.takeLast(100)
        val shift = seriesToTake.first().time.toFloat() + shiftToPage(18)
        pixelSeries = seriesToTake.map {
            GraphPixelPoint(
                getCalculatedX(it.time.toFloat().translate(shift), maxWidthInPoints, viewWidthInPixels),
                getCalculatedY(it.substanceAmount.toFloat(), maxHeightInPoints, viewHeightInPixels),
                it.substanceAmount,
                it.type,
                it.calendar
            )
        }
        invalidate()
    }

    private fun shiftToPage(page: Int): Float {
        return page * maxWidthInPoints
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