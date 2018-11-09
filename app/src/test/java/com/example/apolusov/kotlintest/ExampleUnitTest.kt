package com.example.apolusov.kotlintest

import com.example.apolusov.kotlintest.daydata.DiabetPoint
import com.example.apolusov.kotlintest.daydata.DotColor
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

        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val days = (currentDay - 5..currentDay + 5).map { Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, it) } }

        val diabetPoints = mutableListOf<DiabetPoint>()
        days.forEach { calendar ->
            (0..23).forEach { hour ->
                val newCal = Calendar.getInstance()
                newCal.timeInMillis = calendar.timeInMillis
                newCal.set(Calendar.HOUR_OF_DAY, hour)
//                println(calendar)
                diabetPoints.add(DiabetPoint(newCal.timeInMillis, 500, DotColor.RED, newCal))
            }
        }

        val now = Calendar.getInstance()
        var positionToAdd = 0
        diabetPoints.forEachIndexed { index, diabetPoint ->
            if (now.timeInMillis > diabetPoint.time) {
                positionToAdd = index
            }
        }
        diabetPoints.add(positionToAdd + 1, DiabetPoint(now.timeInMillis, 500, DotColor.RED, now))
        diabetPoints.forEach {
            println(it.calendar)
        }
    }
}
