package com.example.apolusov.kotlintest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.apolusov.kotlintest.withadapter.DiagramAdapter
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*


class MainActivity : AppCompatActivity(), CustomView.NewDataListener {

    companion object {
        public var ZOOM = 1f
        var VIEW_WIDTH = 1000f
    }

    private var scaleFactor = 1f
    val r = Random()

    val diagramAdapter = DiagramAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val scaleDetector = ScaleGestureDetector(this, listener)
//        with(diagramRecyclerView) {
//            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, true)
//            adapter = diagramAdapter
//            addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
//                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                    scaleDetector.onTouchEvent(e)
//                    return false
//                }
//
//                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
//
//                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//
//                }
//            })
//        }

        val customView = CustomView(this, this, 10, 10)
        container.addView(customView)
        customView.post {
            customView.setData((0..150).map { PointM(it, r.nextInt(2) + 4) })
        }
    }

//    private var listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//        override fun onScale(detector: ScaleGestureDetector): Boolean {
//            VIEW_WIDTH = VIEW_WIDTH * detector.scaleFactor
//            scaleFactor = scaleFactor * detector.scaleFactor
//            Timber.d(VIEW_WIDTH.toString())
//            diagramAdapter.notifyDataSetChanged()
//            var scrollBy = scaleFactor
//            diagramRecyclerView.scrollBy(scrollBy.toInt(), 0)
//            return true
//        }
//    }


    override fun onNewData(point: PointM) {

    }
}
