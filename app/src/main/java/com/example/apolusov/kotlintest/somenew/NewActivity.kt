package com.example.apolusov.kotlintest.somenew

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.LinearLayout
import com.example.apolusov.kotlintest.R
import com.example.apolusov.kotlintest.withadapter.GraphicView
import kotlinx.android.synthetic.main.activity_new.*
import timber.log.Timber
import java.util.*

class NewActivity : AppCompatActivity() {

    val r = Random()

    var scaleFactor = 1f
    lateinit var scaleDetector: ScaleGestureDetector

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

        diagramRecyclerView.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                scaleDetector.onTouchEvent(event)
                return false
            }

        })
    }

    private var scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor = scaleFactor * detector.scaleFactor
            manager.scaleFactor = scaleFactor
            Timber.d("$scaleFactor")
            return true
        }
    }
}