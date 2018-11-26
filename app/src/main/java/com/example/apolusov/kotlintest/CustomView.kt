package com.example.apolusov.kotlintest

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.*
import android.view.animation.DecelerateInterpolator
import com.example.apolusov.kotlintest.diagram.DayViewPort
import com.firstlinesoftware.diabetus.diagram.DayItem
import com.firstlinesoftware.diabetus.diagram.DiagramPoint
import timber.log.Timber
import kotlin.math.roundToInt


class CustomView : View {

    private var newDataListener: NewDataListener
    private var pointClickListener: PointClickListener
    private var scrollDetector: GestureDetector
    private var scaleDetector: ScaleGestureDetector
    private val valueAnimator = ValueAnimator().apply {
        duration = 2000
        interpolator = DecelerateInterpolator()
    }


    companion object {
        const val VELOCITY_REDUCER = 100f
        const val BEZIER_TENSION = 1f
        const val TOUCH_PRECISION = 20f
    }

    private var maxWidthInPoints = 0f
    private var maxHeightInPoints = 0f
    private var viewWidthInPixels = 0f
    private var viewHeightInPixels = 0f

    private var daysData = listOf<DayItem>()
    private var rectList = mutableListOf<DayViewPort>()
    private var bezierPoints = listOf<PointF>()

    private var firstPosition = 0
    private var completeScaleFactor = 1f

    private val paint = Paint().apply {
        color = Color.WHITE
        textSize = 50f
    }

    val bezierLinePaint = Paint().apply {
        color = Color.CYAN
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    val bezierPath = Path()

    lateinit var bitmap: Bitmap
    lateinit var cacheCanvas: Canvas

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

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            findClickedPoint(e.x, e.y)
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

    constructor(
        context: Context,
        newDataListener: NewDataListener,
        pointClickListener: PointClickListener,
        defaultWidth: Int,
        defaultHeight: Int
    ) : super(
        context
    ) {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        scrollDetector = GestureDetector(context, scrollListener)
        scaleDetector = ScaleGestureDetector(context, scaleListener)
        this.newDataListener = newDataListener
        this.pointClickListener = pointClickListener
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

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        cacheCanvas = Canvas(bitmap)
        if (daysData.isNotEmpty()) {
            val rectF = RectF(0f, 0f, viewWidthInPixels, viewHeightInPixels)
            val dayViewPort = DayViewPort.construct(rectF, daysData.first())
            rectList.add(dayViewPort)
            drawOnCacheCanvas()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
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
        drawOnCacheCanvas()
        invalidate()
    }

    private fun offsetChildrenHorizontal(scrollBy: Float) {
        rectList.forEach {
            it.offsetChildrenHorizontal(scrollBy)
        }
    }

    fun findClickedPoint(approxX: Float, approxY: Float) {
        rectList.forEach { day ->
            day.points.forEach { diagramPoint ->
                if (diagramPoint.x in approxX - TOUCH_PRECISION..approxX + TOUCH_PRECISION &&
                    diagramPoint.y in approxY - TOUCH_PRECISION..approxY + TOUCH_PRECISION
                ) {
                    pointClickListener.onPointClick(diagramPoint)
                }
            }
        }
    }

    fun scaleView(factor: Float) {
        rectList.forEach {
            it.scaleHorizontally(factor, viewWidthInPixels)
        }
        drawOnCacheCanvas()
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

    private fun drawOnCacheCanvas() {
        bezierPoints = rectList.flatMap { day -> day.points.map { point -> PointF(point.x, point.y) } }
        bezierPath.reset()
        bezierPath.moveTo(bezierPoints.first().x, bezierPoints.first().y)
        for (i in 0 until bezierPoints.size - 1) {
            val p0 = if (i > 0) bezierPoints[i - 1] else bezierPoints[0]
            val p1 = bezierPoints[i]
            val p2 = bezierPoints[i + 1]
            val p3 = if (i != bezierPoints.size - 2) bezierPoints[i + 2] else p2

            val cp1x = p1.x + (p2.x - p0.x) / 6 * BEZIER_TENSION
            val cp1y = p1.y + (p2.y - p0.y) / 6 * BEZIER_TENSION

            val cp2x = p2.x - (p3.x - p1.x) / 6 * BEZIER_TENSION
            val cp2y = p2.y - (p3.y - p1.y) / 6 * BEZIER_TENSION
            bezierPath.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
        }
        cacheCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        rectList.forEach { day ->
            drawPoints(day.points, cacheCanvas)
            drawGridAndTime(cacheCanvas, day.rectF)
        }
        cacheCanvas.drawPath(bezierPath, bezierLinePaint)
        //todo glow
    }

    private fun drawPoints(points: List<DiagramPoint>, canvas: Canvas) {
        points.forEach {
            canvas.drawCircle(it.x, it.y, 10f, paint)
            canvas.drawText(it.text, it.x, it.y, paint)
        }
    }

    private fun drawGridAndTime(canvas: Canvas, rectF: RectF) {
        val verticalLinesNumber =
            when (completeScaleFactor) {
                in 0..1 -> 8
                in 1..2 -> 12
                in 2..3 -> 24
                in 3..4 -> 48
                else -> 48
            }

        val distanceBetweenLines = rectF.width() / verticalLinesNumber
        for (i in 0 until verticalLinesNumber) {
            canvas.drawLine(
                i * distanceBetweenLines + rectF.left,
                0f,
                i * distanceBetweenLines + rectF.left,
                rectF.height(),
                paint
            )
        }

        val horizontalLinesNumber = maxHeightInPoints.roundToInt()
        val verticalDistance = rectF.height() / horizontalLinesNumber
        for (i in 0 until horizontalLinesNumber) {
            canvas.drawLine(
                0f,
                i * verticalDistance,
                rectF.width(),
                i * verticalDistance,
                paint
            )
        }
    }

    interface NewDataListener {
        fun onNewData(point: DayItem)
    }

    interface PointClickListener {
        fun onPointClick(point: DiagramPoint)
    }
}