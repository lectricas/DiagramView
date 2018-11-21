package com.example.apolusov.kotlintest

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.DecelerateInterpolator
import timber.log.Timber
import kotlin.math.roundToInt

class ZoomRecyclerView : RecyclerView {

    // touch detector
    internal lateinit var mScaleDetector: ScaleGestureDetector
    internal lateinit var mGestureDetector: GestureDetectorCompat

    // draw param
    internal var mViewWidth: Float = 0.toFloat()       // Width
    internal var mViewHeight: Float = 0.toFloat()      // Height
    var mTranX: Float = 0.toFloat()           // x offset
    var mTranY: Float = 0.toFloat()           // y offset
    internal var mScaleFactor: Float = 0.toFloat()     // scale factor

    // touch param
    internal var mActivePointerId = INVALID_POINTER_ID  // Effective finger id
    internal var mLastTouchX: Float = 0.toFloat()      // lastTouchLocation X
    internal var mLastTouchY: Float = 0.toFloat()      // lastTouchLocation Y

    // control param
    internal var isScaling = false    // 是否正在缩放
//    internal var isEnableScale = false// 是否支持缩放

    // zoom param
    internal var mScaleAnimator: ValueAnimator? = null //zoom animation
    internal var mScaleCenterX: Float = 0.toFloat()    // zoomCenter X
    internal var mScaleCenterY: Float = 0.toFloat()    // zoomCenter Y
    internal var mMaxTranX: Float = 0.toFloat()        // The maximum X offset under the current zoom factor
    internal var mMaxTranY: Float = 0.toFloat()        // The maximum Y offset under the current zoom factor

    // config param
    internal var mMaxScaleFactor: Float = 0.toFloat()      // Maximum scaling factor
    internal var mMinScaleFactor: Float = 0.toFloat()      // Minimum scaling factor
    internal var mDefaultScaleFactor: Float =
        0.toFloat()  // Default zoom factor Double-click on the zoom factor after zooming out 1
    internal var mScaleDuration: Int = 0         // zoom time ms

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attr: AttributeSet?) {
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mGestureDetector = GestureDetectorCompat(context, GestureListener())

        //init param with default
        mMaxScaleFactor = DEFAULT_MAX_SCALE_FACTOR
        mMinScaleFactor = DEFAULT_MIN_SCALE_FACTOR
        mDefaultScaleFactor = DEFAULT_SCALE_FACTOR
        mScaleFactor = mDefaultScaleFactor
        mScaleDuration = DEFAULT_SCALE_DURATION

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mViewWidth = View.MeasureSpec.getSize(widthMeasureSpec).toFloat()
        mViewHeight = View.MeasureSpec.getSize(heightMeasureSpec).toFloat()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {

//        if (!isEnableScale) {
//            return super.onTouchEvent(ev)
//        }

        var retVal = mScaleDetector.onTouchEvent(ev)
//        retVal = mGestureDetector.onTouchEvent(ev) || retVal
//
//        val action = ev.actionMasked
//
//        when (action) {
//            ACTION_DOWN -> {
//                val pointerIndex = ev.actionIndex
//                val x = ev.getX(pointerIndex)
//                val y = ev.getY(pointerIndex)
//                // Remember where we started (for dragging)
//                mLastTouchX = x
//                mLastTouchY = y
//                // Save the ID of this pointer (for dragging)
//                mActivePointerId = ev.getPointerId(0)
//                Timber.d("DOWN")
//
//            }
//            ACTION_MOVE -> {
////                try {
//                    // Find the index of the active pointer and fetch its position
//                    val pointerIndex = ev.findPointerIndex(mActivePointerId)
//
//                    val x = ev.getX(pointerIndex)
//                    val y = ev.getY(pointerIndex)
//
//                    if (!isScaling && mScaleFactor > 1) { // 缩放时不做处理
//                        // Calculate the distance moved
//                        val dx = (x - mLastTouchX)
//                        val dy = (y - mLastTouchY)
//
//                        setTranslateXY(mTranX + dx, mTranY + dy)
//                        correctTranslateXY()
//                    }
//
//                    prepareChildrenToDraw()
//                    // Remember this touch position for the next move event
//                    mLastTouchX = x
//                    mLastTouchY = y
////                } catch (e: Exception) {
////                    val x = ev.x
////                    val y = ev.y
////
////                    if (!isScaling && mScaleFactor > 1 && mLastTouchX != INVALID_TOUCH_POSITION) { // Do not process when zooming
////                        // Calculate the distance moved
////                        val dx = x - mLastTouchX
////                        val dy = y - mLastTouchY
////
////                        setTranslateXY(mTranX + dx, mTranY + dy)
////                        correctTranslateXY()
////                    }
////
////                    prepareChildrenToDraw()
////                    // Remember this touch position for the next move event
////                    mLastTouchX = x
////                    mLastTouchY = y
////                }
//                Timber.d("MOVE")
//
//            }
//            ACTION_UP, ACTION_CANCEL -> {
//                mActivePointerId = INVALID_POINTER_ID
//                mLastTouchX = INVALID_TOUCH_POSITION
//                mLastTouchY = INVALID_TOUCH_POSITION
//                Timber.d("CANCEL")
//            }
//            ACTION_POINTER_UP -> {
//                val pointerIndex = ev.actionIndex
//                val pointerId = ev.getPointerId(pointerIndex)
//                if (pointerId == mActivePointerId) {
//                    // This was our active pointer going up. Choose a new
//                    // active pointer and adjust accordingly.
//                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
//                    mLastTouchX = ev.getX(newPointerIndex)
//                    mLastTouchY = ev.getY(newPointerIndex)
//                    mActivePointerId = ev.getPointerId(newPointerIndex)
//                }
//                Timber.d("POINTER")
//            }
//        }

        return super.onTouchEvent(ev) || retVal
    }

    private fun prepareChildrenToDraw() {

//        for (i in 0 until childCount + 1) {
//            val child = getChildAt(i)
//            if (child != null) {
//                (child as DiagramView).setScale(this)
//            }
//        }
        invalidate()
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
//        if (!isScaling) {
//            canvas.translate(mTranX / 4, 0f)
//        } else {
//
//        }
//        canvas.translate(mTranX, 0f)
        canvas.scale(mScaleFactor, 1f, width/ 2f, height/2f)
        super.dispatchDraw(canvas)
        for (i in 0 until childCount + 1) {
            getChildAt(i)?.invalidate()
        }
        canvas.restore()
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val newVelocityX = velocityX / mScaleFactor
//        return super.fling(newVelocityX.roundToInt(), velocityY)
        return super.fling(velocityX, velocityY)
    }

    private fun setTranslateXY(tranX: Float, tranY: Float) {
        mTranX = tranX
        mTranY = tranY
    }

//     Correct the position of the action move when the scale is greater than 1
    private fun correctTranslateXY() {
        val correctXY = correctTranslateXY(mTranX, mTranY)
        mTranX = correctXY[0]
        mTranY = correctXY[1]
    }

    private fun correctTranslateXY(x: Float, y: Float): FloatArray {
        var x = x
        var y = y
        if (mScaleFactor <= 1) {
            return floatArrayOf(x, y)
        }

        if (x > 0.0f) {
            x = 0.0f
        } else if (x < mMaxTranX) {
            x = mMaxTranX
        }

        if (y > 0.0f) {
            y = 0.0f
        } else if (y < mMaxTranY) {
            y = mMaxTranY
        }
        return floatArrayOf(x, y)
    }

    private fun zoom(startVal: Float, endVal: Float) {
        if (mScaleAnimator == null) {
            newZoomAnimation()
        }

        if (mScaleAnimator!!.isRunning) {
            return
        }

        //set Value
        mMaxTranX = mViewWidth - mViewWidth * endVal
        mMaxTranY = mViewHeight - mViewHeight * endVal

        val startTranX = mTranX
        val startTranY = mTranY
        var endTranX = mTranX - (endVal - startVal) * mScaleCenterX
        var endTranY = mTranY - (endVal - startVal) * mScaleCenterY
        val correct = correctTranslateXY(endTranX, endTranY)
        endTranX = correct[0]
        endTranY = correct[1]

        val scaleHolder = PropertyValuesHolder
            .ofFloat(PROPERTY_SCALE, startVal, endVal)
        val tranXHolder = PropertyValuesHolder
            .ofFloat(PROPERTY_TRANX, startTranX, endTranX)
        val tranYHolder = PropertyValuesHolder
            .ofFloat(PROPERTY_TRANY, startTranY, endTranY)
        mScaleAnimator!!.setValues(scaleHolder, tranXHolder, tranYHolder)
        mScaleAnimator!!.duration = mScaleDuration.toLong()
        mScaleAnimator!!.start()
    }

    private fun newZoomAnimation() {
        mScaleAnimator = ValueAnimator()
        mScaleAnimator!!.interpolator = DecelerateInterpolator()
        mScaleAnimator!!.addUpdateListener { animation ->
            //update scaleFactor & tranX & tranY
            mScaleFactor = animation.getAnimatedValue(PROPERTY_SCALE) as Float
            setTranslateXY(
                animation.getAnimatedValue(PROPERTY_TRANX) as Float,
                animation.getAnimatedValue(PROPERTY_TRANY) as Float
            )
            prepareChildrenToDraw()
        }

        // set listener to update scale flag
        mScaleAnimator!!.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator) {
                isScaling = true
            }

            override fun onAnimationEnd(animation: Animator) {
                isScaling = false
            }

            override fun onAnimationCancel(animation: Animator) {
                isScaling = false
            }

        })

    }

    // handle scale event
    private inner class ScaleListener : ScaleGestureDetector.OnScaleGestureListener {

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val mLastScale = mScaleFactor
            mScaleFactor *= detector.scaleFactor
            // Fix scaleFactor
            mScaleFactor = Math.max(mMinScaleFactor, Math.min(mScaleFactor, mMaxScaleFactor))

            mMaxTranX = mViewWidth - mViewWidth * mScaleFactor
            mMaxTranY = mViewHeight - mViewHeight * mScaleFactor

            mScaleCenterX = detector.focusX
            mScaleCenterY = detector.focusY
//            mScaleCenterX = mViewWidth / 2
//            mScaleCenterY = mViewHeight / 2

            val offsetX = mScaleCenterX * (mLastScale - mScaleFactor)
            val offsetY = mScaleCenterY * (mLastScale - mScaleFactor)
//            Timber.d("offset=$offsetX, scaleC=$mScaleCenterX, mTrans$mTranX factor=$mScaleFactor")
            setTranslateXY(mTranX + offsetX, mTranY + offsetY)

            isScaling = true
            prepareChildrenToDraw()
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            if (mScaleFactor <= mDefaultScaleFactor) {
                mScaleCenterX = -mTranX / (mScaleFactor - 1)
                mScaleCenterY = -mTranY / (mScaleFactor - 1)
                mScaleCenterX = if (java.lang.Float.isNaN(mScaleCenterX)) 0f else mScaleCenterX
                mScaleCenterY = if (java.lang.Float.isNaN(mScaleCenterY)) 0f else mScaleCenterY
                zoom(mScaleFactor, mDefaultScaleFactor)
            }
            isScaling = false
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTap(e: MotionEvent): Boolean {
//            val startFactor = mScaleFactor
//            val endFactor: Float
//
//            if (mScaleFactor == mDefaultScaleFactor) {
//                mScaleCenterX = e.x
//                mScaleCenterY = e.y
//                endFactor = mMaxScaleFactor
//            } else {
//                mScaleCenterX = if (mScaleFactor == 1f) e.x else -mTranX / (mScaleFactor - 1)
//                mScaleCenterY = if (mScaleFactor == 1f) e.y else -mTranY / (mScaleFactor - 1)
//                endFactor = mDefaultScaleFactor
//            }
//            zoom(startFactor, endFactor)
            return super.onDoubleTap(e)
        }
    }

//    // public method
//    fun setEnableScale(enable: Boolean) {
//        if (isEnableScale == enable) {
//            return
//        }
//        this.isEnableScale = enable
//        // Disabled recovery ratio 1
//        if (!isEnableScale && mScaleFactor != 1f) {
////            zoom(mScaleFactor, 1f)
//        }
//    }
//
//    fun isEnableScale(): Boolean {
//        return isEnableScale
//    }

    companion object {

        private val TAG = "999"

        // constant
        private val DEFAULT_SCALE_DURATION = 300
        private val DEFAULT_SCALE_FACTOR = 1f
        private val DEFAULT_MAX_SCALE_FACTOR = 4.1f
        private val DEFAULT_MIN_SCALE_FACTOR = 0.5f
        private val PROPERTY_SCALE = "scale"
        private val PROPERTY_TRANX = "tranX"
        private val PROPERTY_TRANY = "tranY"
        private val INVALID_TOUCH_POSITION = -1f
    }

}