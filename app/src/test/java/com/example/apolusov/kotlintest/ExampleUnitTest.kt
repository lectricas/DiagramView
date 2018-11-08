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
        Math.abs(-5)
        print(Math.abs(5))
    }

    fun getCalculatedY(oldY: Int, oldHight: Int, newHight: Int): Float {
        return (newHight - oldY.toFloat() / oldHight * newHight)
    }
}
