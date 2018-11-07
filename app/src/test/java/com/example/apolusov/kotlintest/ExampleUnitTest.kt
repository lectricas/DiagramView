package com.example.apolusov.kotlintest

import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val r = Random()
        val a = (0..1000000).map { PointM(it, r.nextInt()) }
        var x = Int.MAX_VALUE
        val timeStart = System.nanoTime()
        a.forEach {
            if (it.y < x) {
                x = it.y
            }
        }
        val timeEnd = System.nanoTime()
        println(timeEnd - timeStart)
        println(x)
//        deltaX = deltaX + getCalculatedX(distanceX, viewWidthInPixels, maxWidthInPoints)
    }

    fun getCalculatedY(oldY: Int, oldHight: Int, newHight: Int): Float {
        return (newHight - oldY.toFloat() / oldHight * newHight)
    }
}
