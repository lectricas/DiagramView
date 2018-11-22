package com.example.apolusov.kotlintest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.*
import com.example.apolusov.kotlintest.diagram.DayViewPort
import com.firstlinesoftware.diabetus.diagram.DayItem
import timber.log.Timber


class CustomView : View {

    private var newDataListener: NewDataListener
    private var scrollDetector: GestureDetector
    private var scaleDetector: ScaleGestureDetector

    companion object {
        const val INITIAL_SCROLL = 0f
    }

    private var daysData = listOf<DayItem>()
    private var maxWidthInPoints = 0f
    private var maxHeightInPoints = 0f
    private var viewWidthInPixels = 0f
    private var viewHeightInPixels = 0f

    private var rectList = mutableListOf<DayViewPort>()

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
    }

    val rectPaint = Paint().apply {
        color = Color.BLUE
    }


    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            scrollView(distanceX)
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
        val rectF = RectF(0f, 0f, viewWidthInPixels, viewHeightInPixels)
        val dayViewPort = DayViewPort.construct(rectF, daysData.first())
        rectList.add(dayViewPort)
    }

    override fun onDraw(canvas: Canvas) {
        rectList.map { it.rectF }.forEach { rect ->
            canvas.drawRect(rect, rectPaint)
            canvas.drawText("${rect.width()}", rect.centerX(), rect.centerY(), paint)
            canvas.drawRect(rect.left + 200, rect.centerY(), rect.right - 200, rect.centerY() + 50, paint)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        scaleDetector.onTouchEvent(event)
        scrollDetector.onTouchEvent(event)
        return true
    }

    fun scrollView(dx: Float) {
        var scrolled = 0f
        if (dx > 0) {
            Timber.d("movingLeft $dx")
            while (scrolled < dx) {
                val rightView = rectList.last().rectF
                val hangingRight = Math.max(rightView.right - viewWidthInPixels, 0f)
                val scrollBy = -Math.min(dx - scrolled, hangingRight)
                scrolled -=scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled < dx) {
                    val left = rightView.right
                    val right = left + rightView.width()
                    val rect = RectF(left, rightView.top, right, rightView.bottom)
                    rectList.add(rect)
                    if (rectList.size > 2) {
                        rectList.removeAt(0)
                    }
                } else {
                    break
                }
            }

        } else if (dx < 0) {
            Timber.d("movingRight $dx")
            while (scrolled > dx) {
                val leftView = rectList.first().rectF
                val hangingLeft = Math.max(-leftView.left, 0f)
                val scrollBy = Math.min(scrolled - dx, hangingLeft)
                scrolled -= scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled > dx) {
                    val right = leftView.left
                    val left = right - leftView.width()
                    val rect = RectF(left, leftView.top, right, leftView.bottom)
                    rectList.add(0, rect)
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
        Timber.d("size = ${rectList.size}")
        rectList.forEach {
            scaleRectHorizontally(it, factor)
        }
        invalidate()
    }

    //move from real world coordinates to the viewport and vice versa
    fun getCalculatedX(oldX: Float, oldWidth: Float, newWidth: Float): Float {
        return oldX / oldWidth * newWidth
    }

    //move from real world coordinates to the viewport and vice versa
    fun getCalculatedY(oldY: Float, oldHight: Float, newHight: Float): Float {
        return newHight - oldY / oldHight * newHight
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