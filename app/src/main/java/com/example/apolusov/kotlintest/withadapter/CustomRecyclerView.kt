package com.example.apolusov.kotlintest.withadapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import timber.log.Timber
import java.util.*

class CustomRecyclerView : RecyclerView {

    private val scaleDetector: ScaleGestureDetector

    var viewWidth = 1000
    var color = Color.RED
    val rand = Random()

    var customDigit = 0

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        scaleDetector = ScaleGestureDetector(context, scaleListener)
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        scaleDetector.onTouchEvent(e)
        return super.onTouchEvent(e)
    }

    private var scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            customDigit = rand.nextInt(15) + 1
//            for (i in 0 until adapter.itemCount) {
//                Timber.d("${layoutManager.findViewByPosition(i)}, $i")
//            }

//            Timber.d("$childCount")
            for (i in 0 until childCount) {
                val view = getChildAt(i) as GraphicView
                view.digit = customDigit
                view.requestLayout();
            }
            return true
        }
    }
}