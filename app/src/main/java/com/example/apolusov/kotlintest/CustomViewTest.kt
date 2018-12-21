package com.example.apolusov.kotlintest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.example.apolusov.kotlintest.diagram.DataPoint
import timber.log.Timber
import java.util.*


class CustomViewTest(context: Context, private val listener: ViewEventListener) : View(context) {

    val maxWidth = 1080
    val maxHeight = 1536

    private var w: Float = 0f
    private var h: Float = 0f

    private var scrollDetector: GestureDetector

    var dataPoints = listOf<DataPoint>()
    var holders = mutableListOf<PointsViewHolder>()

    var totalScroll = 0f

    private val PAINT = Paint().apply {
        color = Color.WHITE
        textSize = 30f
        strokeWidth = 50f
        style = Paint.Style.STROKE;
    }

    private val LABELS = Paint().apply {
        color = Color.WHITE
        textSize = 30f
    }

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            totalScroll += distanceX
            scrollView(distanceX)
            return true
        }
    }

    init {
        scrollDetector = GestureDetector(context, scrollListener)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        this.w = width.toFloat()
        this.h = height.toFloat()
        for (i in 0 until 2) {
            val holder = PointsViewHolder(i * w, 0f, i * w + w, h, Calendar.getInstance())
            holders.add(holder)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scrollDetector.onTouchEvent(event)
        return true
    }

    fun setData(data: List<DataPoint>) {
        this.dataPoints = data
        requestLayout()
    }

    fun addData(data: List<DataPoint>) {
        this.dataPoints = dataPoints.plus(data)
    }

    private fun scrollView(dx: Float) {
        var scrolled = 0f
         if (dx > 0) {
            while (scrolled < dx) {
                val rightView = holders.last()
                val hangingRight = Math.max(rightView.right - w, 0f)
                val scrollBy = -Math.min(dx - scrolled, hangingRight)
                scrolled -= scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled < dx) {
                    val holder = PointsViewHolder(rightView.right, rightView.top, rightView.right + w, rightView.bottom, )
                    holders.add(holder)
                    if (holders.size > 2) {
                        holders.removeAt(0)
                    }
                } else {
                    break
                }
            }
        }
        else if (dx < 0) {
             while (scrolled > dx) {
                 val leftView = holders.first()
                 val hangingLeft = Math.max(-leftView.left, 0f)
                 val scrollBy = Math.min(scrolled - dx, hangingLeft)
                 scrolled -= scrollBy
                 offsetChildrenHorizontal(scrollBy)
                 if (scrolled > dx) {
                     val holder = PointsViewHolder(leftView.left - w, leftView.top, leftView.left, leftView.bottom)
                     holders.add(0, holder)
                     if (holders.size > 2) {
                         holders.removeAt(holders.lastIndex)
                     }
                 } else {
                     break
                 }
             }
         }
        invalidate()
    }

    private fun offsetChildrenHorizontal(scrollBy: Float) {
        holders.forEach {
            it.offsetX(scrollBy)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        holders.forEach {
            canvas.drawRect(RectF(it.left, it.top, it.right, it.bottom), PAINT)
        }
        dataPoints.forEach {
            canvas.drawText("${it.valueX}", it.valueX - totalScroll, it.valueY.toFloat(), LABELS)
        }
    }

    interface ViewEventListener {
        fun onNewData()
    }
}