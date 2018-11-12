package com.example.apolusov.kotlintest.somenew

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import timber.log.Timber

/**
 * Created by adityagohad on 06/06/17.
 */

class PickerLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {

    var scaleDownBy = 0.66f
    var scaleDownDistance = 0.9f
    var scaleFactor = 1f
    var someWidth = 2672
    var deltaX = 0f

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        scaleDownView(recycler, state)

    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        deltaX = deltaX + dx
        if (scaleFactor > 2f) {
            Timber.d("delta $deltaX, $scaleFactor")
        }
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    private fun scaleDownView(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) {
//        val mid = width / 2.0f
//        val unitScaleDownDist = scaleDownDistance * mid
        for (i in 0 until childCount) {
            val child = getChildAt(i)
//            val childMid = (getDecoratedLeft(child!!) + getDecoratedRight(child)) / 2.0f
//            val scale =
//                1.0f + -1 * scaleDownBy * Math.min(unitScaleDownDist, Math.abs(mid - childMid)) / unitScaleDownDist
            val params = child.layoutParams.apply { width = (scaleFactor * someWidth).toInt() }
            child.layoutParams = params
        }
    }
}