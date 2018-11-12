package com.example.apolusov.kotlintest.linear

import android.content.Context
import android.graphics.Color
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.OverScroller
import com.example.apolusov.kotlintest.daydata.OneDayPoints
import timber.log.Timber
import java.util.*

class ScrolledLinear : LinearLayout {

    companion object {
        const val WIDTH = 2672
        const val HEIGHT = 1152
    }

    private var days = listOf<OneDayPoints>()

    //    private var views = listOf<InnerView>()
    val random = Random()

    private var positionX = 0
    private var positionY = 0

    private var gestureDetector: GestureDetectorCompat
    private lateinit var overScroller: OverScroller
    private var onNewDataListener: NewDataListener
//    private var scaleDetector: ScaleGestureDetector

    constructor(context: Context, newDataListener: NewDataListener) : super(context, null, 0) {
        gestureDetector = GestureDetectorCompat(context, gestureListener)
        overScroller = OverScroller(context)
        this.onNewDataListener = newDataListener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        // computeScrollOffset() returns true only when the scrolling isn't
        // already finished
        Timber.d("before curr ${overScroller.currX} final ${overScroller.finalX} ${overScroller.isFinished}" )
        if (overScroller.computeScrollOffset()) {
            Timber.d("after curr ${overScroller.currX} final ${overScroller.finalX} ${overScroller.isFinished}" )
            positionX = overScroller.currX
            positionY = overScroller.currY
            scrollTo(positionX, positionY)
        } else {
            if (positionX == 0) {
                onNewDataListener.onNewDataLeft(days.first().currentDay)
            }

            if (positionX == getMaxHorizontal()) {
//                onNewDataListener.onNewDataRight(days.last().currentDay)
            }

            // when scrolling is over, we will want to "spring back" if the
            // image is overscrolled
//            val back = overScroller.springBack(positionX, positionY, 0, getMaxHorizontal(), 0, getMaxVertical())
        }
    }

    private fun getMaxHorizontal(): Int {
        return (days.size - 1) * WIDTH
    }

    private fun getMaxVertical(): Int {
        return 0
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
            overScroller.startScroll(positionX, 0, dx, dy, 0)
            ViewCompat.postInvalidateOnAnimation(this@ScrolledLinear)
            return true
        }
    }

    fun setDataLeft(data: List<OneDayPoints>) {
        removeAllViewsInLayout()
        data.forEach {
            val item = InnerView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    2672,
                    1152
                )
                number = it.currentDay.get(Calendar.DAY_OF_MONTH)
                setBackgroundColor(Color.parseColor("#ff69b4"));

            }
            addView(item)
        }

        days = data
        positionX = getMaxHorizontal()

        scrollTo(positionX, positionY)
    }

    fun setDataRight(data: List<OneDayPoints>) {
        removeAllViewsInLayout()
        data.forEach {
            val item = InnerView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    2672,
                    1152
                )
                number = it.currentDay.get(Calendar.DAY_OF_MONTH)
                setBackgroundColor(Color.parseColor("#ff69b4"));

            }
            addView(item)
        }

        days = data
        positionX = 0

        scrollTo(positionX + 1, positionY)
    }

    interface NewDataListener {
        fun onNewDataLeft(cal: Calendar)
        fun onNewDataRight(cal: Calendar)
    }
}