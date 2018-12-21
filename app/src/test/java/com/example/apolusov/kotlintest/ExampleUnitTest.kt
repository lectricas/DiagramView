package com.example.apolusov.kotlintest

import com.example.apolusov.kotlintest.diagram.DataPoint
import org.junit.Test

import org.junit.Assert.*
import java.text.DecimalFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    val r = Random()

    @Test
    fun addition_isCorrect() {
        val maxWidth = 1080
        val maxHeight = 1536

        val data = (1..20).map {
            println(it)
            DataPoint(maxWidth / 2 * it, r.nextInt(maxHeight))
        }

        print(data)
    }
}
