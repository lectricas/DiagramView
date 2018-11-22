package com.example.apolusov.kotlintest

import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.firstlinesoftware.diabetus.diagram.DayItem
import com.firstlinesoftware.diabetus.diagram.DiagramPoint
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), CustomView.NewDataListener {

    val r = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val customView = CustomView(this, this, 10, 10)
        container.addView(customView)
        customView.post {
            customView.setData((0..1000).map { dayNumber ->
                val day = (0..23).map { hourNumber -> DiagramPoint(hourNumber.toFloat(), r.nextFloat() * 3, 0, "a") }
                DayItem(day, day, day)
            })
        }

    }


    override fun onNewData(point: DayItem) {

    }
}
