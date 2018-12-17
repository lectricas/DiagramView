package com.example.apolusov.kotlintest

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.example.apolusov.kotlintest.diagram.DataPoint
import android.support.v7.widget.RecyclerView
import kotlin.math.roundToInt


class CustomViewTest(context: Context) : View(context) {

    val pointsWidth = 10
    val pointsHeight = 5

    private var w: Float = 0f
    private var h: Float = 0f

    val screensInCache = 2

    private var scrollDetector: GestureDetector

    var dataPoints = listOf<DataPoint>()
    var holders = mutableListOf<PointsViewHolder>()

    var firstPosition = 0

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            scrollView(distanceX)
            return true
        }
    }

    init {
        scrollDetector = GestureDetector(context, scrollListener)
    }

    @SuppressLint("DrawAllocation") //called only once, can allocate
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        this.w = width.toFloat()
        this.h = height.toFloat()
        setMeasuredDimension(width, height)

        for (i in 0 until 2) {
            val holder = PointsViewHolder(i * w, 0f, i * w + w, h)
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

    private fun scrollView(dx: Float) {
        var scrolled = 0f
        if (dx < 0) {
            while (scrolled > dx) {
                val leftView = holders.first()
                val hangingLeft = Math.max(-leftView.left, 0f)
                val scrollBy = Math.min(scrolled - dx, hangingLeft)
                scrolled -= scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled > dx) {
                    val holder = PointsViewHolder(leftView.left - w, 0f, leftView.left, h)
                    holders.add(0, holder)

                    if (holders.size > 2) {
                        holders.removeAt(holders.lastIndex)
                    }
                } else {
                    break
                }
            }
        } else if (dx > 0) {
            while (scrolled < dx) {
                val rightView = holders.last()
                val hangingRight = Math.max(rightView.right, 0f)
                val scrollBy = -Math.min(dx - scrolled, hangingRight)
                scrolled -= scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled < dx) {
                    val holder = PointsViewHolder(rightView.right, 0f, rightView.right + w, h)
                    holders.add(holder)
                    if (holders.size > 2) {
                        holders.removeAt(0)
                    }
                } else {
                    break
                }
            }
        }
    }

    private fun offsetChildrenHorizontal(scrollBy: Float) {

    }

}