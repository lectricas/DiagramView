package com.example.apolusov.kotlintest.old

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import com.example.apolusov.kotlintest.R
import com.example.apolusov.kotlintest.daydata.OneDayPoints
import com.example.apolusov.kotlintest.linear.ScrolledLinear
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*


class MainActivity : AppCompatActivity(), ScrolledLinear.NewDataListener {

    val r = Random()


    lateinit var scrolledLinear: ScrolledLinear

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val days =
            (currentDay - 5..currentDay + 2).map { Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, it) } }

        val points = mutableListOf<OneDayPoints>()

        for (i in 0 until days.size) {
            val currentDayIndex = i
            val previousDayIndex = i - 1
            val nextDayIndex = i + 1
            val prevDay = if (previousDayIndex < 0) null else days[previousDayIndex]
            val nextDay = if (nextDayIndex > days.lastIndex) null else days[nextDayIndex]
            val nowDay = days[currentDayIndex]
            points.add(OneDayPoints(prevDay, nowDay, nextDay))
        }

        scrolledLinear = ScrolledLinear(this, this)
            .apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }

        container.addView(scrolledLinear)
        scrolledLinear.post {
            scrolledLinear.setDataLeft(points)
        }
    }

    override fun onNewDataLeft(cal: Calendar) {
        val calDay = cal.get(Calendar.DAY_OF_MONTH)
        Timber.d("firstDay $calDay")
        val days = (calDay - 5..calDay).map { Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, it) } }

        val points = mutableListOf<OneDayPoints>()

        for (i in 0 until days.size) {
            val currentDayIndex = i
            val previousDayIndex = i - 1
            val nextDayIndex = i + 1
            val prevDay = if (previousDayIndex < 0) null else days[previousDayIndex]
            val nextDay = if (nextDayIndex > days.lastIndex) null else days[nextDayIndex]
            val nowDay = days[currentDayIndex]
            points.add(OneDayPoints(prevDay, nowDay, nextDay))
        }

        scrolledLinear.setDataLeft(points)
    }

    override fun onNewDataRight(cal: Calendar) {
        val calDay = cal.get(Calendar.DAY_OF_MONTH)
        Timber.d("firstDay $calDay")
        val days = (calDay..calDay + 5).map { Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, it) } }

        val points = mutableListOf<OneDayPoints>()

        for (i in 0 until days.size) {
            val currentDayIndex = i
            val previousDayIndex = i - 1
            val nextDayIndex = i + 1
            val prevDay = if (previousDayIndex < 0) null else days[previousDayIndex]
            val nextDay = if (nextDayIndex > days.lastIndex) null else days[nextDayIndex]
            val nowDay = days[currentDayIndex]
            points.add(OneDayPoints(prevDay, nowDay, nextDay))
        }

        scrolledLinear.setDataRight(points)
    }

    //    private var listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//        override fun onScale(detector: ScaleGestureDetector): Boolean {
//            scaleFactor = scaleFactor * detector.scaleFactor
//
//            return true
//        }
//    }
//
//    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
//        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
//            Timber.d(distanceX.toString())
//            diagramRecyclerView.scrollBy(distanceX.toInt(), distanceY.toInt())
//            return true
//        }
//    }

    //        val imageView = ScrollableImageView(this)
//            .apply { ViewGroup.LayoutParams(50, 50) }
//        imageView.setImageResource(R.mipmap.ic_launcher)
//        container.addView(imageView)
//        val scaleDetector = ScaleGestureDetector(this, listener)
//        val scrollDetector = GestureDetector(this, scrollListener)
//        with(diagramRecyclerView) {
//            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, true)
//            adapter = diagramAdapter
//            addItemDecoration(DividerItemDecoration(this@MainActivity, RecyclerView.HORIZONTAL))
//        }

//        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//        val days = (currentDay - 5..currentDay + 1).map { Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, it) } }
//
//        val diabetPoints = mutableListOf<DiabetPoint>()
//        days.forEach { calendar ->
//            (0..23).forEach { hour ->
//                val newCal = Calendar.getInstance()
//                newCal.timeInMillis = calendar.timeInMillis
//                newCal.set(Calendar.HOUR_OF_DAY, hour)
//                diabetPoints.add(DiabetPoint(newCal.timeInMillis, 500, DotColor.RED, newCal))
//            }
//        }
//
//        val now = Calendar.getInstance()
//        var positionToAdd = 0
//        diabetPoints.forEachIndexed { index, diabetPoint ->
//            if (now.timeInMillis > diabetPoint.time) {
//                positionToAdd = index
//            }
//        }
//        diabetPoints.add(positionToAdd + 1, DiabetPoint(now.timeInMillis, 600, DotColor.RED, now))
////
//        val width = 1000 * 60 * 60 * 5 //5 hours
//        val height = 1000
}
