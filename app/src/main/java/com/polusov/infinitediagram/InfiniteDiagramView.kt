package com.polusov.infinitediagram

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import timber.log.Timber

class InfiniteDiagramView(context: Context): View(context) {

    private val data = mutableListOf<RectView>()

    private val scroller = Scroller(context, DecelerateInterpolator())

    private var viewWidthInPixels = 0f
    private var viewHeightInPixels = 0f

    private val detector = GestureDetector(context, object: SimpleOnGestureListener(){
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
//            scrollView(distanceX)
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            Timber.d("OnFling $velocityX")
            scroller.fling(
                0,
                0,
                velocityX.toInt(),
                0,
                -5000,
                5000,
                0,
                0
            )

            Timber.d("duration ${scroller.duration}")

            val a = ValueAnimator.ofFloat(scroller.duration.toFloat())
            var intA = 0
            a.addUpdateListener {
                if (scroller.computeScrollOffset()) {
                    intA += scroller.currX
                    Timber.d("scroller offset = ${scroller.currX} + ${it.animatedValue} + summ = $intA")
                }
            }
            a.start()


//            invalidate()
            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            if (!scroller.isFinished) {
                scroller.forceFinished(true)
            }
            return true
        }
    })

    private fun scrollView(dx: Float) {
        Timber.d(dx.toString())
        var scrolled = 0f
        if (dx > 0) {
            while (scrolled < dx) {
                val rightView = data.last()
                val rightRect = rightView.rectF
                val hangingRight = Math.max(rightRect.right - viewWidthInPixels, 0f)
                val scrollBy = -Math.min(dx - scrolled, hangingRight)
                scrolled -= scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled < dx) {
                    val left = rightRect.right
                    val right = left + rightRect.width()
                    val rect = RectF(left, rightRect.top, right, rightRect.bottom)
                    data.add(RectView(rect, rightView.data.inc()))

                    if (data.size > 2) {
                        data.removeAt(0)
                    }
                } else {
                    break
                }
            }
        }
        else if (dx < 0) {
            while (scrolled > dx) {
                val leftView = data.first()
                val leftRect= leftView.rectF
                val hangingLeft = Math.max(-leftRect.left, 0f)
                val scrollBy = Math.min(scrolled - dx, hangingLeft)
                scrolled -= scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled > dx) {
                    val right = leftRect.left
                    val left = right - leftRect.width()
                    val rect = RectF(left, leftRect.top, right, leftRect.bottom)
                    data.add(0, RectView(rect, leftView.data.dec()))
                    if (data.size > 2) {
                        data.removeAt(data.lastIndex)
                    }
                } else {
                    break
                }
            }
        }
    }

    private fun offsetChildrenHorizontal(delta: Float) {
        data.forEach {
            it.rectF.left += delta
            it.rectF.right += delta
        }
        invalidate()
    }

    val p = Paint().apply {
        color = Color.BLUE
    }

    val textP = Paint().apply {
        color = Color.WHITE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidthInPixels = w.toFloat()
        viewHeightInPixels = h.toFloat()
        data.add(RectView(RectF(0f, 0f, w.toFloat(), h.toFloat()), 1))
    }

    override fun onDraw(canvas: Canvas) {
        data.forEach {
            canvas.drawRect(it.rectF, p)
            canvas.drawText(it.data.toString(), it.rectF.centerX(), it.rectF.centerY(), textP)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        detector.onTouchEvent(event)
        return true
    }
}