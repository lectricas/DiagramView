package com.example.apolusov.kotlintest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.apolusov.kotlintest.daydata.DiabetPoint
import com.example.apolusov.kotlintest.daydata.DotColor
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

        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val days = (currentDay - 5..currentDay + 1).map { Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, it) } }

        val diabetPoints = mutableListOf<DiabetPoint>()
        days.forEach { calendar ->
            (0..23).forEach { hour ->
                val newCal = Calendar.getInstance()
                newCal.timeInMillis = calendar.timeInMillis
                newCal.set(Calendar.HOUR_OF_DAY, hour)
                diabetPoints.add(DiabetPoint(newCal.timeInMillis, 500, DotColor.RED, newCal))
            }
        }

        val now = Calendar.getInstance()
        var positionToAdd = 0
        diabetPoints.forEachIndexed { index, diabetPoint ->
            if (now.timeInMillis > diabetPoint.time) {
                positionToAdd = index
            }
        }
        diabetPoints.add(positionToAdd + 1, DiabetPoint(now.timeInMillis, 600, DotColor.RED, now))

        val width = 1000 * 60 * 60 * 5 //5 hours
        val height = 1000
        val customView = CustomView(this, this, width, height)
        container.addView(customView)
        customView.post {
            customView.setData(diabetPoints)
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
