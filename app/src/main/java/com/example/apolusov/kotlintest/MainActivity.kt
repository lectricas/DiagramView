package com.example.apolusov.kotlintest

import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), CustomView.NewDataListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val customView = CustomView(this, this)
        container.addView(customView)
        customView.setData((0..100).map { PointD(it.toFloat(), 5f) })
    }


    override fun onNewData(point: PointD) {

    }
}
