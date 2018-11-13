package com.example.apolusov.kotlintest.somenew

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.LinearLayout
import com.example.apolusov.kotlintest.R
import com.example.apolusov.kotlintest.withadapter.GraphicView
import kotlinx.android.synthetic.main.activity_new.*
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

class NewActivity : AppCompatActivity() {

    companion object {
        val minScrollFactor = 1f
        val maxScrollFactor = 4f
    }

    val r = Random()
    lateinit var scaleDetector: ScaleGestureDetector
    var someWidth = 1794

    var scrollFactorMultipled = minScrollFactor

    lateinit var manager: PickerLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        scaleDetector = ScaleGestureDetector(this, scaleListener)
        manager = PickerLayoutManager(this@NewActivity, LinearLayout.HORIZONTAL, true)
        with(diagramRecyclerView) {
            adapter = NewAdapter()
            layoutManager = manager
        }

        diagramRecyclerView.scrollToPosition(5)

        diagramRecyclerView.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (scrollFactorMultipled in minScrollFactor..maxScrollFactor) {
                    scaleDetector.onTouchEvent(event).toString()
                }
                return true
            }
        })
    }

    private var scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            scrollFactorMultipled *= scaleFactor
            for (i in 0 until manager.childCount) {
                val child = manager.getChildAt(i)
                val prevWidth = child.width
                val params = child?.layoutParams?.apply { width = (scaleFactor * prevWidth).roundToInt() }
                child?.layoutParams = params
                manager.currentWidth = child.width
            }

            someWidth = (someWidth * scaleFactor).roundToInt()
//            Timber.d("${manager.getChildAt(0).width}, $someWidth")
            val scroll = (someWidth - manager.getChildAt(0).width) / 2
            diagramRecyclerView.scrollBy(scroll * -1, 0)
            return true
        }
    }
}