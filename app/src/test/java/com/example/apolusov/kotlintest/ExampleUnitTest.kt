package com.example.apolusov.kotlintest

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val s1 = (0..19).map { it.toString() }.toMutableList()
        var s2: String? = null
        if (s2 != null) {
            s1.add(s2)
        }
    }
}
