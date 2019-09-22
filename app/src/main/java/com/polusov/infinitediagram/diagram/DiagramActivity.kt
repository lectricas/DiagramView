package com.polusov.infinitediagram.diagram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.polusov.infinitediagram.R.layout
import kotlinx.android.synthetic.main.activity_main.container
import java.util.concurrent.TimeUnit

class DiagramActivity : AppCompatActivity() {

    lateinit var diagramView: InfiniteDiagramView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        diagramView = InfiniteDiagramView(this)
        container.addView(diagramView)

        diagramView.setData(generateData(System.currentTimeMillis()))
    }

    fun generateData(time: Long): List<DiagramPoint> {
        val hour = TimeUnit.HOURS.toMillis(1)
        val startTime = time - (hour * 50)
        return (1..100).map {
            DiagramPoint(startTime + (it * hour), 500)
        }
    }
}