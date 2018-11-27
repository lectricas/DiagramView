package com.example.apolusov.kotlintest.diagram

import com.firstlinesoftware.diabetus.diagram.DiagramPoint

class DiagramBar(
    x: Float,
    height: Float,
    val type: Int,
    val text: String
) {

    companion object {
        const val TYPE_0 = 0
        const val TYPE_1 = 1
        const val TYPE_2 = 2
        const val TYPE_3 = 3
    }

    var x: Float = x
        private set

    var height: Float = height
        private set

    fun convert(
        oldWidth: Float,
        newWidth: Float,
        oldHight: Float,
        newHight: Float,
        offset: Float
    ): DiagramBar {
        return DiagramBar(
            getCalculatedX(this.x, oldWidth, newWidth) + offset,
            getCalculatedY(this.height, oldHight, newHight),
            this.type,
            this.text
        )
    }

    //move from real world coordinates to the viewport and vice versa
    private fun getCalculatedX(oldX: Float, oldWidth: Float, newWidth: Float): Float {
        return oldX / oldWidth * newWidth
    }

    //move from real world coordinates to the viewport and vice versa
    private fun getCalculatedY(oldY: Float, oldHight: Float, newHight: Float): Float {
        return newHight - oldY / oldHight * newHight
    }

    fun offset(scrollBy: Float) {
        x += scrollBy
    }

    fun scale(factor: Float, viewWidthInPixels: Float) {
        val mX = (x - viewWidthInPixels / 2) * factor
        x = mX + viewWidthInPixels / 2
    }
}