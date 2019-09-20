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
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.core.view.ScaleGestureDetectorCompat
import timber.log.Timber
import java.util.Random
import kotlin.Int.Companion

class InfiniteDiagramView(context: Context) : View(context) {

    companion object {
        private val SWIPE_THRESHOLD = 150
        private val SWIPE_MIN_DISTANCE = 120
        private val SWIPE_MAX_OFF_PATH = 250
        private val SWIPE_THRESHOLD_VELOCITY = 200
    }

    var rnd = Random()
    private val data = mutableListOf<RectView>()

    var weScrolled = 0f

    private var viewWidthInPixels = 0f
    private var viewHeightInPixels = 0f

    private val flinger = Flinger()

    private val detector = GestureDetector(context, object : SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            scrollView(distanceX)
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            Timber.d("OnFling $velocityX")
            flinger.start(velocityX.toInt())

            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            flinger.finish()
            return true
        }
    })

    private val scaler = ScaleGestureDetector(context, ScaleListener())

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            Timber.d("scaleFactor ${detector.scaleFactor}")
            return true
        }
    }

    inner class Flinger : Runnable {
        private val scroller = Scroller(context)
        private var lastX = 0

        fun start(velocity: Int) {
            Timber.d("startX = ${data.first().rectF.left} width = ${width}")
            scroller.fling(
                weScrolled.toInt(),
                0,
                velocity,
                0,
                Int.MIN_VALUE,
                Int.MAX_VALUE,
                0,
                0
            )
            post(this)
        }

        override fun run() {
            if (scroller.isFinished) {
                return
            }
            val more = scroller.computeScrollOffset()
            val x = scroller.currX
            val diff = lastX - x
            if (diff != 0) {
                scrollView(diff.toFloat())
                lastX = x
            }

            if (more) {
                post(this)
            }
        }

        fun finish() {
            if (!scroller.isFinished) {
                scroller.forceFinished(true);
            }
        }
    }

    private fun scrollView(dx: Float) {
        this.weScrolled -= dx
        Timber.d("scrollView: $dx")
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
                    data.add(RectView(rect, rightView.data.inc(), randomColor()))
                    if (data.size > 2) {
                        data.removeAt(0)
                    }
                } else {
                    break
                }
            }
        } else if (dx < 0) {
            while (scrolled > dx) {
                val leftView = data.first()
                val leftRect = leftView.rectF
                val hangingLeft = Math.max(-leftRect.left, 0f)
                val scrollBy = Math.min(scrolled - dx, hangingLeft)
                scrolled -= scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled > dx) {
                    val right = leftRect.left
                    val left = right - leftRect.width()
                    val rect = RectF(left, leftRect.top, right, leftRect.bottom)
                    data.add(0, RectView(rect, leftView.data.dec(), randomColor()))
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
        data.add(RectView(RectF(0f, 0f, w.toFloat(), h.toFloat()), 1, randomColor()))
    }

    override fun onDraw(canvas: Canvas) {
        data.forEach {
            p.color = it.color
            canvas.drawRect(it.rectF, p)
            canvas.drawText(it.data.toString(), it.rectF.centerX(), it.rectF.centerY(), textP)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (scaler.onTouchEvent(event)) {
            return true
        } else {
            detector.onTouchEvent(event)
        }
        return true
    }

    private fun randomColor(): Int {
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
}