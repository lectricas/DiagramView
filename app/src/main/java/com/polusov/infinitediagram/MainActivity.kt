package com.polusov.infinitediagram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.container
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    lateinit var mCircle: ExpandingCircleAnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.d("AppStarts")
//        mCircle = ExpandingCircleAnimationDrawable(200f)
//        image.setImageDrawable(mCircle)
        container.addView(InfiniteDiagramView(this))
    }

    override fun onResume() {
        super.onResume()
//        mCircle.start()
    }
}
