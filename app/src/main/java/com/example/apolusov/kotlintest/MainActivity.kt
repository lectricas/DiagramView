package com.example.apolusov.kotlintest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.apolusov.kotlintest.diagram.DataPoint
import com.firstlinesoftware.diabetus.diagram.DayItem
import com.firstlinesoftware.diabetus.diagram.DiagramPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*


class MainActivity : AppCompatActivity(), CustomView.NewDataListener, CustomView.PointClickListener {

    val r = Random()

    lateinit var customView: CustomView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customView = CustomView(this, this, this)
        container.addView(customView)

        customView.post {
            customView.setData((0..9).map { dayNumber ->
                val dayPoints = listOf(DiagramPoint(12f, r.nextInt(250) + 250f, 0, "12f"))
                DayItem(dayPoints)
            })
        }

        val test = CustomViewTest(this)
        val maxWidth = 10
        val maxHeight = 5


        val data = listOf(5,15,25,35,45,55,65,75,85,95)
            .map { DataPoint(it, r.nextInt(2) + 2) }
        test.setData(data)
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
