package com.example.apolusov.kotlintest

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.support.v4.view.ViewCompat
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.OverScroller
import com.example.apolusov.kotlintest.diagram.DayViewPort
import com.firstlinesoftware.diabetus.diagram.DayItem
import timber.log.Timber
import kotlin.math.roundToInt


class CustomView : View {

    private var newDataListener: NewDataListener
    private var scrollDetector: GestureDetector
    private var scaleDetector: ScaleGestureDetector
    private val valueAnimator = ValueAnimator().apply {
        duration = 2000
        interpolator = DecelerateInterpolator()
    }


    companion object {
        const val VELOCITY_REDUCER = 100f
    }

    private var daysData = listOf<DayItem>()
    private var maxWidthInPoints = 0f
    private var maxHeightInPoints = 0f
    private var viewWidthInPixels = 0f
    private var viewHeightInPixels = 0f

    private var rectList = mutableListOf<DayViewPort>()

    private var firstPosition = 0

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 50f
    }

    val rectPaint = Paint().apply {
        color = Color.BLUE
    }


    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            scrollView(distanceX)
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            valueAnimator.setFloatValues(- velocityX / VELOCITY_REDUCER, 0f)
            valueAnimator.start()
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

        valueAnimator.addUpdateListener {
            scrollView(it.animatedValue as Float)
        }
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidthInPixels = width.toFloat()
        viewHeightInPixels = height.toFloat()

        if (daysData.isNotEmpty()) {
            val rectF = RectF(0f, 0f, viewWidthInPixels, viewHeightInPixels)
            val dayViewPort = DayViewPort.construct(rectF, daysData.first())
            rectList.add(dayViewPort)
        }
    }

    override fun onDraw(canvas: Canvas) {
        rectList.forEach { day ->
            canvas.drawText(day.text, day.rectF.centerX(), day.rectF.centerY(), paint)
            canvas.drawRect(day.rectF.left + 200, day.rectF.centerY(), day.rectF.right - 200, day.rectF.centerY() + 50, paint)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        valueAnimator.cancel()
        scaleDetector.onTouchEvent(event)
        scrollDetector.onTouchEvent(event)
        return true
    }

    fun scrollView(dx: Float) {
        var scrolled = 0f
        if (dx > 0) {
            while (scrolled < dx) {
                val rightView = rectList.last().rectF
                val hangingRight = Math.max(rightView.right - viewWidthInPixels, 0f)
                val scrollBy = -Math.min(dx - scrolled, hangingRight)
                scrolled -=scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled < dx && firstPosition - 1 > 0) {
                    val left = rightView.right
                    val right = left + rightView.width()
                    val rect = RectF(left, rightView.top, right, rightView.bottom)
                    firstPosition --

                    rectList.add(DayViewPort.construct(rect, daysData[firstPosition - 1]))
                    if (rectList.size > 2) {
                        rectList.removeAt(0)
                    }
                } else {
                    break
                }
            }

        } else if (dx < 0) {
            while (scrolled > dx) {
                val leftView = rectList.first().rectF
                val hangingLeft = Math.max(-leftView.left, 0f)
                val scrollBy = Math.min(scrolled - dx, hangingLeft)
                scrolled -= scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled > dx && daysData.lastIndex > firstPosition) {
                    val right = leftView.left
                    val left = right - leftView.width()
                    val rect = RectF(left, leftView.top, right, leftView.bottom)
                    firstPosition++
                    rectList.add(0, DayViewPort.construct(rect, daysData[firstPosition]))
                    if (rectList.size > 2) {
                        rectList.removeAt(rectList.lastIndex)
                    }
                } else {
                    break
                }
            }
        }

        invalidate()
    }

    fun offsetChildrenHorizontal(scrollBy: Float) {
        rectList.forEach {
            it.rectF.offset(scrollBy, 0f)
        }
    }

    fun scaleView(factor: Float) {
        rectList.forEach {
            //todo scale
//            scaleRectHorizontally(it, factor)
        }
        invalidate()
    }

    fun setData(data: List<DayItem>) {
        if (daysData.isEmpty()) {
            daysData = daysData.plus(data)
            requestLayout()
        } else {
            daysData = daysData.plus(data)
            invalidate()
        }
    }

    private fun scaleRectHorizontally(rectF: RectF, scaleFactor: Float) {
        val sLeft = (rectF.left - viewWidthInPixels / 2) * scaleFactor
        val sRight = (rectF.right - viewWidthInPixels / 2) * scaleFactor


        rectF.left = sLeft + viewWidthInPixels / 2
        rectF.right = sRight + viewWidthInPixels /2

        //todo scale vertically
//        val sTop = (rectF.top - viewHeightInPixels / 2)
//        val sBottom = (rectF.bottom - viewHeightInPixels / 2) * scaleFactor
//        rectF.top = sTop + viewHeightInPixels /2
//        rectF.bottom = sBottom + viewHeightInPixels /2
    }

    interface NewDataListener {
        fun onNewData(point: DayItem)
    }
}