package com.example.apolusov.kotlintest

import org.junit.Test

import org.junit.Assert.*
import java.text.DecimalFormat

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println(String.format("%05.2f", 11.5f).replace(",50", ":30"))

//        println(java.text.DecimalFormat("#.00").format(500.401))
    }
}
