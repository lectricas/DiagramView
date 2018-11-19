package com.example.apolusov.kotlintest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = SimpleLayoutManager(this@MainActivity)

        with(recyclerView) {
            adapter = SimpleAdapter()
            layoutManager = manager
        }

       seek.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
           override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
               Timber.d("progress = $progress")
               for (i in 0 until recyclerView.childCount) {
                   (recyclerView.getChildAt(i) as CustomView).scaleView(progress.toFloat())
               }
           }

           override fun onStartTrackingTouch(seekBar: SeekBar?) {

           }

           override fun onStopTrackingTouch(seekBar: SeekBar?) {

           }

       })
    }
}
