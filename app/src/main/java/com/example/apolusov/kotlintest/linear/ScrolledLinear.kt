package com.example.apolusov.kotlintest.linear

import android.content.Context
import android.graphics.Color
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.LinearLayout
import android.widget.OverScroller
import timber.log.Timber

class ScrolledLinear : LinearLayout {

    val views: List<View>

    private var positionX = 0
    private var positionY = 0

    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var overScroller: OverScroller
//    private var scaleDetector: ScaleGestureDetector

    constructor(context: Context) : this(context, null) {

    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        gestureDetector = GestureDetectorCompat(context, gestureListener)
        overScroller = OverScroller(context)

        views = (0..3).map {
            View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    700,
                    700
                ).apply { setMargins(10, 10, 10, 10) }
                setBackgroundColor(Color.parseColor("#ff66c1"))
            }
        }

        views.forEach {
            addView(it)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        // computeScrollOffset() returns true only when the scrolling isn't
        // already finished
        if (overScroller.computeScrollOffset()) {
            positionX = overScroller.currX
            positionY = overScroller.currY
            scrollTo(positionX, positionY)
        } else {
            // when scrolling is over, we will want to "spring back" if the
            // image is overscrolled

            val back = overScroller.springBack(positionX, positionY, 0, getMaxHorizontal(), 0, getMaxVertical())
            Timber.d("$back")
        }
    }

    private fun getMaxHorizontal(): Int {
        return (views.size - 1) * views[0].width
    }

    private fun getMaxVertical(): Int {
        return views[0].height
    }

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            overScroller.forceFinished(true)
            ViewCompat.postInvalidateOnAnimation(this@ScrolledLinear)
            return true
        }

        override fun onFling(
            e1: MotionEvent, e2: MotionEvent, velocityX: Float,
            velocityY: Float
        ): Boolean {
            overScroller.forceFinished(true)
            overScroller.fling(
                positionX, positionY, (-velocityX).toInt(), (-velocityY).toInt(), 0, getMaxHorizontal(), 0,
                getMaxVertical()
            )
            ViewCompat.postInvalidateOnAnimation(this@ScrolledLinear)
            return true
        }

        override fun onScroll(
            e1: MotionEvent, e2: MotionEvent,
            distanceX: Float, distanceY: Float
        ): Boolean {
            overScroller.forceFinished(true)
            // normalize scrolling distances to not overscroll the image
            var dx = distanceX.toInt()
            var dy = distanceY.toInt()
            val newPositionX = positionX + dx
            val newPositionY = positionY + dy
            if (newPositionX < 0) {
                dx -= newPositionX
            } else if (newPositionX > getMaxHorizontal()) {
                dx -= newPositionX - getMaxHorizontal()
            }
            if (newPositionY < 0) {
                dy -= newPositionY
            } else if (newPositionY > getMaxVertical()) {
                dy -= newPositionY - getMaxVertical()
            }
            overScroller.startScroll(positionX, positionY, dx, dy, 0)
            ViewCompat.postInvalidateOnAnimation(this@ScrolledLinear)
            return true
        }
    }

}