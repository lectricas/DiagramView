package com.example.apolusov.kotlintest

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import timber.log.Timber

class SimpleLayoutManager(c: Context) : RecyclerView.LayoutManager() {

    companion object {
        private val SCROLL_DISTANCE = 80 // dp
    }

    private var mFirstPosition: Int = 0
    private val mScrollDistance: Int

    private var scrolledOffset = 0

    init {
        val dm = c.resources.displayMetrics
        mScrollDistance = (SCROLL_DISTANCE * dm.density + 0.5f).toInt()
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
//        Timber.d("onLayoutChildren")
//        var oldRight = width - paddingRight
//        val oldRightView = if (childCount > 0) getChildAt(0) else null
//        if (oldRightView != null) {
//            oldRight = oldRightView.right
//        }
//
//        val count = state.itemCount
//
//        val parentLeft = paddingLeft
//        val top = paddingTop
//        val bottom = height - paddingBottom
//        var left: Int
//        var right = oldRight
//
//        detachAndScrapAttachedViews(recycler)
//        var i = 0
//        while (mFirstPosition + i < count && right > parentLeft) {
//            Timber.d("children ${mFirstPosition + i}")
//            val v = recycler.getViewForPosition(mFirstPosition + i)
//            addView(v, i)
//            measureChildWithMargins(v, 0, 0)
//            left = right - getDecoratedMeasuredWidth(v)
//            layoutDecorated(v, left, top, right, bottom)
//            i++
//            right = left
//        }
        val count = state.itemCount
        val parentLeft = 0
        val top = 0
        val bottom = height
        val rightTemp = width
        var leftTemp = 0
        val centerX = width / 2
        detachAndScrapAttachedViews(recycler)
        var i = 0

        val v = recycler.getViewForPosition(mFirstPosition + i)
        addView(v, i)
        measureChildWithMargins(v, 0, 0)
        leftTemp = rightTemp - getDecoratedMeasuredWidth(v)
        val viewCenter = rightTemp - getDecoratedMeasuredWidth(v) / 2
        val shift = centerX - viewCenter
        val left = leftTemp + shift
        val right = rightTemp + shift
        layoutDecorated(v, left, top, right, bottom)
    }

    fun shift(screenCenter: Int, viewCenter: Int): Int {
        return screenCenter - viewCenter
    }

    fun scaleX(xVal: Int, viewWidth: Int, scaleFactor: Float): Float {
        val tL = xVal - viewWidth / 2
        val sL = tL * scaleFactor
        return sL + viewWidth / 2
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

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State?): Int {
        val top = 0
        val bottom = height
        var scrolled = 0
        Timber.d("$childCount")
        if (dx < 0) {
            while (scrolled > dx) {
                val leftView = getChildAt(0)
                val hangingLeft = Math.max(-getDecoratedLeft(leftView), 0)
                val scrollBy = Math.min(scrolled - dx, hangingLeft)
                scrolled -= scrollBy
                scrolledOffset += scrollBy
                offsetChildrenHorizontal(scrollBy)
                if (scrolled > dx) {
                    val v = recycler.getViewForPosition(mFirstPosition + childCount)
                    val right = getDecoratedLeft(leftView)
                    addView(v, 0)
                    measureChildWithMargins(v, 0, 0)
                    val left = right -  getDecoratedMeasuredWidth(v)
                    layoutDecorated(v, left, top, right, bottom)
                }
            }
        }
        recycleViewsOutOfBounds(recycler)
        return scrolled
    }

    override fun scrollVerticallyBy(
        dy: Int, recycler: RecyclerView.Recycler,
        state: RecyclerView.State?
    ): Int {
        Timber.d("$childCount")
        if (childCount == 0) {
            return 0
        }
        var scrolled = 0
        val left = paddingLeft
        val right = width - paddingRight
        if (dy < 0) {
            while (scrolled > dy) {
                val topView = getChildAt(0)
                val hangingTop = Math.max(-getDecoratedTop(topView), 0)
                val scrollBy = Math.min(scrolled - dy, hangingTop)
                scrolled -= scrollBy
                offsetChildrenVertical(scrollBy)
                if (mFirstPosition > 0 && scrolled > dy) {
                    mFirstPosition--
                    val v = recycler.getViewForPosition(mFirstPosition)
                    addView(v, 0)
                    measureChildWithMargins(v, 0, 0)
                    val bottom = getDecoratedTop(topView)
                    val top = bottom - getDecoratedMeasuredHeight(v)
                    layoutDecorated(v, left, top, right, bottom)
                } else {
                    break
                }
            }
        } else if (dy > 0) {
            val parentHeight = height
            while (scrolled < dy) {
                val bottomView = getChildAt(childCount - 1)
                val hangingBottom = Math.max(getDecoratedBottom(bottomView) - parentHeight, 0)
                val scrollBy = -Math.min(dy - scrolled, hangingBottom)
                scrolled -= scrollBy
                offsetChildrenVertical(scrollBy)
                if (scrolled < dy && state!!.itemCount > mFirstPosition + childCount) {
                    val v = recycler.getViewForPosition(mFirstPosition + childCount)
                    val top = getDecoratedBottom(getChildAt(childCount - 1))
                    addView(v)
                    measureChildWithMargins(v, 0, 0)
                    val bottom = top + getDecoratedMeasuredHeight(v)
                    layoutDecorated(v, left, top, right, bottom)
                } else {
                    break
                }
            }
        }
        recycleViewsOutOfBounds(recycler)
        return scrolled
    }

//    fun recycleViewsOutOfBounds(recycler: RecyclerView.Recycler) {
//        val childCount = childCount
//        val parentWidth = width
//        val parentHeight = height
//        var foundFirst = false
//        var first = 0
//        var last = 0
//        for (i in 0 until childCount) {
//            val v = getChildAt(i)
//            if (v.hasFocus() ||
//                getDecoratedRight(v) >= 0 &&
//                getDecoratedLeft(v) <= parentWidth &&
//                getDecoratedBottom(v) >= 0 &&
//                getDecoratedTop(v) <= parentHeight
//            ) {
//                if (!foundFirst) {
//                    first = i
//                    foundFirst = true
//                }
//                last = i
//            }
//        }
//        for (i in childCount - 1 downTo last + 1) {
//            removeAndRecycleViewAt(i, recycler)
//        }
//        for (i in first - 1 downTo 0) {
//            removeAndRecycleViewAt(i, recycler)
//        }
//        if (getChildCount() == 0) {
//            mFirstPosition = 0
//        } else {
//            mFirstPosition += first
//        }
//        Timber.d("$mFirstPosition firstPostition")
//    }

    fun recycleViewsOutOfBounds(recycler: RecyclerView.Recycler) {
        val childCount = childCount
        val parentWidth = width
        val parentHeight = height

        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (
                getDecoratedRight(v) <= 0 ||
                getDecoratedLeft(v) >= parentWidth &&
                getDecoratedBottom(v) <= 0 &&
                getDecoratedTop(v) >= parentHeight
            ) {
                removeAndRecycleViewAt(i, recycler)
            }
        }
        Timber.d("$mFirstPosition firstPostition $itemCount")
    }
}