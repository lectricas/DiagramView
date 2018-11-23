package com.example.apolusov.kotlintest

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.*
import android.view.animation.DecelerateInterpolator
import com.example.apolusov.kotlintest.diagram.DayViewPort
import com.firstlinesoftware.diabetus.diagram.DayItem
import com.firstlinesoftware.diabetus.diagram.DiagramPoint


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

    private var maxWidthInPoints = 0f
    private var maxHeightInPoints = 0f
    private var viewWidthInPixels = 0f
    private var viewHeightInPixels = 0f

    private var daysData = listOf<DayItem>()
    private var rectList = mutableListOf<DayViewPort>()

    private var firstPosition = 0
    private var completeScaleFactor = 1f

    private val path = Path()

    private val paint = Paint().apply {
        color = Color.BLACK
        textSize = 50f
    }

    private val rectPaint = Paint().apply {
        color = Color.BLUE
    }

    val bezierLinePaint = Paint().apply {
        color = Color.YELLOW
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            scrollView(distanceX)
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            valueAnimator.setFloatValues(-velocityX / VELOCITY_REDUCER, 0f)
            valueAnimator.start()
            return true
        }
    }

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            completeScaleFactor *= detector.scaleFactor
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
            drawPoints(day.now, canvas)
            drawPoints(day.now, canvas)
            drawPoints(day.now, canvas)
            drawBezierCurve(day.now, canvas, 1)
            drawBezierCurve(day.before, canvas, 1)
            drawBezierCurve(day.after, canvas, 1)
//            drawGridAndTime(canvas, day.rectF)
        }
    }

    private fun drawPoints(points: List<DiagramPoint>, canvas: Canvas) {
        points.forEach {
            canvas.drawCircle(it.x, it.y, 10f, paint)
            canvas.drawText(it.text, it.x, it.y, paint)
        }
    }

    private fun drawBezierCurve(points: List<DiagramPoint>, canvas: Canvas, tension: Int) {
        path.reset()
        path.moveTo(points.first().x, points.first().y)
        for (i in 0 until points.size - 1) {
            val p0 = if (i > 0) points[i - 1] else points[0]
            val p1 = points[i]
            val p2 = points[i + 1]
            val p3 = if (i != points.size - 2) points[i + 2] else p2

            val cp1x = p1.x + (p2.x - p0.x) / 6 * tension
            val cp1y = p1.y + (p2.y - p0.y) / 6 * tension

            val cp2x = p2.x - (p3.x - p1.x) / 6 * tension
            val cp2y = p2.y - (p3.y - p1.y) / 6 * tension
            path.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
        }
        canvas.drawPath(path, bezierLinePaint)
    }

    private fun drawGridAndTime(canvas: Canvas, rectF: RectF) {
        val linesNumber =
            when (completeScaleFactor) {
                in 0..1 -> 48
                in 1..2 -> 24
                in 2..3 -> 12
                in 3..4 -> 6
                else -> 96
            }

        val distanceBetweenLines = rectF.width() / linesNumber
        for (i in 0 until linesNumber) {
            canvas.drawLine(i * distanceBetweenLines + rectF.left, 0f , i * distanceBetweenLines + rectF.left, rectF.height(), paint)
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
                scrolled -= scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled < dx && firstPosition - 1 > 0) {
                    val left = rightView.right
                    val right = left + rightView.width()
                    val rect = RectF(left, rightView.top, right, rightView.bottom)
                    firstPosition--

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

    private fun offsetChildrenHorizontal(scrollBy: Float) {
        rectList.forEach {
            it.offsetChildrenHorizontal(scrollBy)
        }
    }

    fun scaleView(factor: Float) {
        rectList.forEach {
            it.scaleHorizontally(factor, viewWidthInPixels)
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

    interface NewDataListener {
        fun onNewData(point: DayItem)
    }
}