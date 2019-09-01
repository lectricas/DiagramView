package com.firstlinesoftware.diabetus.diagram

class DiagramPoint(
    x: Float,
    y: Float,
    val type: Int,
    val text: String
) {

    var x: Float = x
        private set

    var y: Float = y
        private set

    fun convert(
        oldWidth: Float,
        newWidth: Float,
        oldHight: Float,
        newHight: Float,
        offset: Float
    ): DiagramPoint {
        return DiagramPoint(
            getCalculatedX(this.x, oldWidth, newWidth) + offset,
            getCalculatedY(this.y, oldHight, newHight),
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

    fun scale(factor: Float, viewWidthInPixels: Float) {
        val mX = (x - viewWidthInPixels / 2) * factor
        x = mX + viewWidthInPixels / 2
    }

    fun offset(scrollBy: Float) {
        x += scrollBy
    }
}