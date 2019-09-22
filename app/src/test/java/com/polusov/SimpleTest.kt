package com.polusov

import com.polusov.infinitediagram.diagram.DiagramPoint
import com.polusov.infinitediagram.diagram.RectView.RectPoint
import org.junit.*
import java.util.concurrent.TimeUnit

class SimpleTest {

    companion object {
        const val hour = 1000 * 60 * 60
        const val widthInMillis = hour * 24
        const val heightInValues = 1000
        const val widthInPixels = 1080
        const val heightInPixels = 2160
    }

    @Test
    fun isConversionValid() {
        val data = generateData(System.currentTimeMillis())
        data.map {
            convert(
                it.time,
                it.value,
                widthInMillis.toFloat(),
                widthInPixels.toFloat(),
                heightInValues.toFloat(),
                heightInPixels.toFloat()
            )
        }
    }

    fun generateData(time: Long): List<DiagramPoint> {
        val startTime = time - (hour * 50)
        return (1..100).map {
            DiagramPoint(startTime + (it * hour), 500)
        }
    }

    fun convert(
        x: Long,
        y: Int,
        oldWidth: Float,
        newWidth: Float,
        oldHeight: Float,
        newHeight: Float
    ): RectPoint {
        return RectPoint(
            getCalculatedX(x.toFloat(), oldWidth, newWidth),
            getCalculatedY(y.toFloat(), oldHeight, newHeight)
        )
    }

    private fun getCalculatedX(oldX: Float, oldWidth: Float, newWidth: Float): Float {
        return oldX / oldWidth * newWidth
    }

    private fun getCalculatedY(oldY: Float, oldHight: Float, newHight: Float): Float {
        return newHight - oldY / oldHight * newHight
    }

//    fun scale(factor: Float, viewWidthInPixels: Float) {
//        val mX = (xField - viewWidthInPixels / 2) * factor
//        xField = mX + viewWidthInPixels / 2
//    }
}