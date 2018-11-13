package com.example.apolusov.kotlintest.somenew

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import timber.log.Timber
import android.support.v4.view.ViewCompat.setAlpha
import android.support.v4.view.ViewCompat.setScaleY
import android.support.v4.view.ViewCompat.setScaleX
import kotlin.math.roundToInt


/**
 * Created by adityagohad on 06/06/17.
 */

class PickerLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {

    var canScrollHorizontally = true
    var currentWidth = 1794

    override fun canScrollHorizontally(): Boolean {
        return canScrollHorizontally
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        val child = getChildAt(0)
        Timber.d("$0 , ${child.width}")
//        for (i in 0 until childCount) {
//            val child = getChildAt(i)
//            Timber.d("$i , ${child.width}")
////            val params = child?.layoutParams?.apply { width = currentWidth }
////            child?.layoutParams = params
//        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            Timber.d("$i , ${child.width}")
//            val params = child?.layoutParams?.apply { width = currentWidth }
//            child?.layoutParams = params
        } 
        return super.scrollHorizontallyBy(dx, recycler, state)
    }
}