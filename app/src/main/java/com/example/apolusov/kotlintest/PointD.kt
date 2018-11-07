package com.example.apolusov.kotlintest

data class PointD(
    val x: Int,
    val y: Int,
    val text: Int
) {
    override fun toString() = "[$x, $y, $text]"
}