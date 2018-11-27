package com.example.apolusov.kotlintest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.apolusov.kotlintest.diagram.DiagramBar
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

        customView = CustomView(this, this, this, 10, 10)
        container.addView(customView)

        customView.post {
            customView.setData((0..10).map { dayNumber ->
                val dayPoints = (0..23).map { hourNumber -> DiagramPoint(hourNumber.toFloat(), r.nextFloat() * 3 + 2, 0, hourNumber.toString()) }
                val dayBars = (0..23).map { hourNumber -> DiagramBar(hourNumber.toFloat(), r.nextFloat(), r.nextInt(3), hourNumber.toString()) }
                DayItem(dayPoints, dayBars, dayNumber)
            })
        }

    }

    override fun onNewData(point: DayItem) {
        customView.post {
            customView.setData((0..10).map { dayNumber ->
                val dayPoints = (0..23).map { hourNumber -> DiagramPoint(hourNumber.toFloat(), r.nextFloat() * 3 + 2, 0, hourNumber.toString()) }
                val dayBars = (0..23).map { hourNumber -> DiagramBar(hourNumber.toFloat(), r.nextFloat(), r.nextInt(3), hourNumber.toString()) }
                DayItem(dayPoints, dayBars, dayNumber)
            })
        }
    }

    override fun onPointClick(point: DiagramPoint) {
        Timber.d(point.text)
    }
}
