package com.example.apolusov.kotlintest

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING
import android.view.ViewGroup
import timber.log.Timber
import kotlin.math.roundToInt

class SimpleLayoutManager(c: Context, val scrollingState: () -> Int) : RecyclerView.LayoutManager() {

    private var mFirstPosition: Int = 0


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val top = paddingTop
        val bottom = height - paddingBottom
        val left = paddingLeft
        val right = width - paddingRight

        detachAndScrapAttachedViews(recycler)
        val v = recycler.getViewForPosition(0)
        addView(v)
        measureChildWithMargins(v, 0, 0)
        layoutDecorated(v, left, top, right, bottom)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun canScrollVertically(): Boolean {
        return false
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(
        delta: Int, recycler: RecyclerView.Recycler,
        state: RecyclerView.State?
    ): Int {
        if (childCount == 0) {
            return 0
        }
        var dx = delta
        var scrolled = 0
        val top = paddingTop
        val bottom = height - paddingBottom
        if (dx < 0) {
//            if (scrollingState.invoke() == SCROLL_STATE_DRAGGING) {
//                dx = Math.min((dx / 4), -1)
//            }
            while (scrolled > dx) {
                val leftView = getChildAt(0)
                val hangingLeft = Math.max(-getDecoratedLeft(leftView), 0)
                val scrollBy = Math.min(scrolled - dx, hangingLeft)
                scrolled -= scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled > dx && state!!.itemCount > mFirstPosition + childCount) {
                    val v = recycler.getViewForPosition(mFirstPosition + childCount)
                    addView(v, 0)
                    measureChildWithMargins(v, 0, 0)
                    val right = getDecoratedLeft(leftView)
                    val left = right - getDecoratedMeasuredWidth(v)
                    layoutDecorated(v, left, top, right, bottom)
                } else {
                    break
                }
            }
        } else if (dx > 0) {
//            if (scrollingState.invoke() == SCROLL_STATE_DRAGGING) {
//                dx = Math.max((dx / 4), 1)
//            }
            Timber.d("scrollLeft")
            val parentWidth = width
            while (scrolled < dx) {
                val rightView = getChildAt(childCount - 1)
                val shift = (width * App.scaleFactor - width) / 2
                val hangingRight = Math.max(getDecoratedRight(rightView) - parentWidth, 0)
                val scrollBy = -Math.min(dx - scrolled, hangingRight)
                scrolled -= scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled < dx && mFirstPosition > 0) {
                    mFirstPosition--
                    val v = recycler.getViewForPosition(mFirstPosition)
                    val left = getDecoratedRight(getChildAt(childCount - 1))
                    addView(v)
                    measureChildWithMargins(v, 0, 0)
                    val right = left + getDecoratedMeasuredWidth(v)
                    layoutDecorated(v, left, top, right, bottom)
                } else {
                    break
                }
            }
        }
        recycleViewsOutOfBounds(recycler)
        return delta
    }

    fun recycleViewsOutOfBounds(recycler: RecyclerView.Recycler) {
        val childCount = childCount
        val parentWidth = width
        val parentHeight = height
        var foundFirst = false
        var first = 0
        var last = 0
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (v.hasFocus() ||
                getDecoratedRight(v) >= 0 &&
                getDecoratedLeft(v) <= parentWidth &&
                getDecoratedBottom(v) >= 0 &&
                getDecoratedTop(v) <= parentHeight
            ) {
                if (!foundFirst) {
                    first = i
                    foundFirst = true
                }
                last = i
            }
        }
        for (i in childCount - 1 downTo last + 1) {
            removeAndRecycleViewAt(i, recycler)
            mFirstPosition++
        }
        for (i in first - 1 downTo 0) {
            removeAndRecycleViewAt(i, recycler)
        }
    }
}