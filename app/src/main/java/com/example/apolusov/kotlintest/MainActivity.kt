package com.example.apolusov.kotlintest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.apolusov.kotlintest.diagram.DataPoint
import com.firstlinesoftware.diabetus.diagram.DayItem
import com.firstlinesoftware.diabetus.diagram.DiagramPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*


class MainActivity : AppCompatActivity(), CustomView.NewDataListener, CustomView.PointClickListener, CustomViewTest.ViewEventListener {

    val r = Random()

//    lateinit var customView: CustomView
    lateinit var customView: CustomViewTest
    val maxWidth = 1080
    val maxHeight = 1536

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        customView = CustomView(this, this, this)
        customView = CustomViewTest(this, this)
        container.addView(customView)
//
//        customView.post {
//            customView.setData((0..9).map { dayNumber ->
//                val dayPoints = listOf(DiagramPoint(12f, r.nextInt(250) + 250f, 0, "12f"))
//                DayItem(dayPoints)
//            })
//        }

        val data = (1..10).map {
            DataPoint(maxWidth / 2 * it, r.nextInt(100) + 700)
        }

        customView.post {
            customView.setData(data)
        }
    }

    override fun onNewData() {
        val data = (11..20).map {
            DataPoint(maxWidth / 2 * it, r.nextInt(100) + 700)
        }

        customView.post {
            customView.addData(data)
        }
    }

    override fun onNewData(point: DayItem) {
//        customView.post {
//            customView.setData((0..9).map { dayNumber ->
//                val dayPoints = listOf(DiagramPoint(12f, r.nextInt(250) + 250f, 0, "12f"))
//                val dayBars = listOf(DiagramBar(10f, r.nextInt(250) + 0f, r.nextInt(3), "10f"))
//                DayItem(dayPoints, dayBars, dayNumber)
//            })
//        }
    }

    override fun onPointClick(point: DiagramPoint) {
        Timber.d(point.text)
    }
}
