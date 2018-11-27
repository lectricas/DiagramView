package com.example.apolusov.kotlintest

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.view.*
import android.view.animation.DecelerateInterpolator
import com.example.apolusov.kotlintest.diagram.DayViewPort
import com.example.apolusov.kotlintest.diagram.DiagramBar
import com.firstlinesoftware.diabetus.diagram.DayItem
import com.firstlinesoftware.diabetus.diagram.DiagramPoint
import timber.log.Timber
import kotlin.math.roundToInt


class CustomView : View {

    private inner class ScrollWorker(val scrollBy: Float) : Runnable {
        override fun run() {
            scrollView(scrollBy)
        }
    }

    private val scrollHandler = Handler(Looper.getMainLooper());
    private var scrollWorker: Runnable? = null

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
        const val ITEMS_LEFT_WHEN_LOAD = 5
        const val BARS_WIDTH = 7
        const val BAR_RADIUS = 15f
        const val BAR_BOTTOM_MARGIN = 30f
        const val SCROLL_DEBOUNCE = 5L
        const val TIME_LABELS_SHIFT = 50
    }

    private var horizontalLinesNumber = 7f
    private var viewWidthInPixels = 0f
    private var viewHeightInPixels = 0f
    private var otherMedsLineHeight = 0f

    private var daysData = listOf<DayItem>()
    private var rectList = mutableListOf<DayViewPort>()
    private var bezierPoints = listOf<PointF>()

    private var firstPosition = 0
    private var completeScaleFactor = 1f

    private val LABELS = Paint().apply {
        color = Color.WHITE
        textSize = 40f
    }

    private val OTHER_MEDS_LINE = Paint().apply {
        style = Paint.Style.FILL
    }

    private val bezierLinePaint = Paint().apply {
        color = Color.CYAN
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    private val bezierPath = Path()

    private lateinit var bitmap: Bitmap
    private lateinit var cacheCanvas: Canvas

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
//            scrollView(distanceX)
            scrollHandler.removeCallbacks(scrollWorker)
            scrollWorker = ScrollWorker(distanceX)
            scrollHandler.postDelayed(scrollWorker, SCROLL_DEBOUNCE)
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            valueAnimator.setFloatValues(-velocityX / VELOCITY_REDUCER, 0f)
            valueAnimator.start()
            return false
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
        pointClickListener: PointClickListener
    ) : super(
        context
    ) {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        scrollDetector = GestureDetector(context, scrollListener)
        scaleDetector = ScaleGestureDetector(context, scaleListener)
        this.newDataListener = newDataListener
        this.pointClickListener = pointClickListener
        initPaintsAndSizes()
        valueAnimator.addUpdateListener {
            scrollHandler.removeCallbacks(scrollWorker)
            scrollWorker = ScrollWorker(it.animatedValue as Float)
            scrollHandler.postDelayed(scrollWorker, SCROLL_DEBOUNCE)
//            scrollView(it.animatedValue as Float)

        }
    }

    fun initPaintsAndSizes() {
        otherMedsLineHeight = resources.getDimension(R.dimen.other_meds_height)
        OTHER_MEDS_LINE.color = ContextCompat.getColor(context, R.color.other_meds_line)
        LABELS.textSize = resources.getDimension(R.dimen.time_labels_size)
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
        canvas.drawBitmap(bitmap, 0f, 0f, LABELS)
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
        if (daysData.size - firstPosition <= ITEMS_LEFT_WHEN_LOAD) {
            Timber.d("first position $firstPosition")
            newDataListener.onNewData(daysData.last())
        }
        drawOnCacheCanvas()
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
        cacheCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR) //clear
        cacheCanvas.drawPath(bezierPath, bezierLinePaint)
        rectList.forEach { day ->
            drawGridAndTime(cacheCanvas, day.rectF)
            drawPoints(day.points, cacheCanvas)
            drawBars(day.bars, cacheCanvas)
        }

        bitmap.prepareToDraw()
        invalidate()
        //todo glow
    }

    private fun drawBars(points: List<DiagramBar>, canvas: Canvas) {
        points.forEach {
            val rectF = RectF(it.x - BARS_WIDTH, it.height - 50, it.x + BARS_WIDTH, height - 50f )
            val currentPaint = when (it.type) {
                DiagramBar.TYPE_0 -> LABELS
                DiagramBar.TYPE_1 -> LABELS
                DiagramBar.TYPE_2 -> LABELS
                DiagramBar.TYPE_3 -> LABELS
                else -> LABELS

            }
            canvas.drawRoundRect(rectF, BAR_RADIUS, BAR_RADIUS, currentPaint)
        }

        canvas.drawRect(0f, height - otherMedsLineHeight - LABELS.textSize, width + 0f, height - LABELS.textSize, OTHER_MEDS_LINE)
    }

    private fun drawPoints(points: List<DiagramPoint>, canvas: Canvas) {
        points.forEach {
            canvas.drawCircle(it.x, it.y, 12f, LABELS)
            canvas.drawText(it.text, it.x, it.y, LABELS)
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
                rectF.height() - LABELS.textSize,
                LABELS
            )
            val timeLabel = String.format("%05.2f", i * 24f / verticalLinesNumber)
                .replace(",50", ":30")
                .replace(",00", ":00")
            canvas.drawText(timeLabel, i * distanceBetweenLines + rectF.left - TIME_LABELS_SHIFT, rectF.height(), LABELS)
        }

        val horizontalLinesNumber = horizontalLinesNumber.roundToInt()
        val verticalDistance = rectF.height() / horizontalLinesNumber
        for (i in 0 until horizontalLinesNumber) {
            canvas.drawLine(
                0f,
                i * verticalDistance,
                rectF.width(),
                i * verticalDistance,
                LABELS
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