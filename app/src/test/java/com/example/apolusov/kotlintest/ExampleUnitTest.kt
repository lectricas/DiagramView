package com.example.apolusov.kotlintest

import com.example.apolusov.kotlintest.daydata.DiabetPoint
import com.example.apolusov.kotlintest.daydata.DotColor
import com.example.apolusov.kotlintest.daydata.OneDayPoints
import org.junit.Test
import timber.log.Timber
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

//        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//        val days = (currentDay - 5..currentDay + 2).map { Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, it) } }
//
//        val points = mutableListOf<OneDayPoints>()
//
//        for (i in 0 until days.size) {
//            val currentDayIndex = i
//            val previousDayIndex = i - 1
//            val nextDayIndex = i + 1
//            val prevDay = if (previousDayIndex < 0) null else days[previousDayIndex]
//            val nextDay = if (nextDayIndex > days.lastIndex) null else days[nextDayIndex]
//            val nowDay = days[currentDayIndex]
//            points.add(OneDayPoints(prevDay, nowDay, nextDay))
//        }
//
//        print(points)


//        val dayToLoad = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        (0..5).forEach { print(it) }

//        print(days)
//        val now = Calendar.getInstance()
//        var positionToAdd = 0
//        diabetPoints.forEachIndexed { index, diabetPoint ->
//            if (now.timeInMillis > diabetPoint.time) {
//                positionToAdd = index
//            }
//        }
//        diabetPoints.add(positionToAdd + 1, DiabetPoint(now.timeInMillis, 600, DotColor.GREEN, now))
    }
}
