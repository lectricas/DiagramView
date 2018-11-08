package com.example.apolusov.kotlintest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.apolusov.kotlintest.withadapter.DiagramAdapter
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity(), CustomView.NewDataListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        with(diagramRecyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, true)
            adapter = DiagramAdapter()
        }

//        val customView = CustomView(this, this, 10, 10)
//        container.addView(customView)
//        customView.post {
//            customView.setData((0..100).map { PointM(it, 5) })
//        }
    }


    override fun onNewData(point: PointM) {

    }
}
