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
        val f1 = 7532
        val f2 = 1024
        print(f1.toFloat()/f2)
    }

    fun getCalculatedY(oldY: Int, oldHight: Int, newHight: Int): Float {
        return (newHight - oldY.toFloat() / oldHight * newHight)
    }
}
