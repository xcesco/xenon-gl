/**
 *
 */
package com.abubusoft.xenon.core.graphic

import android.graphics.Color

/**
 * @author Francesco Benincasa
 */
class ColorRange {
    var startColor = 0
    private val tempStart = FloatArray(3)
    private val tempEnd = FloatArray(3)
    private val tempRange = FloatArray(3)
    var endColor = 0

    /**
     * Dato un valore iniziale e finale, calcola la percentuale
     *
     * @param start
     * @param end
     * @param percentage
     * @return
     */
    fun ramp(start: Int, end: Int, percentage: Float): Int {
        return (start + (end - start) * percentage).toInt()
    }

    /**
     * Dato un valore iniziale e finale, calcola la percentuale
     *
     * @param start
     * @param end
     * @param percentage
     * @return
     */
    fun ramp(start: Float, end: Float, percentage: Float): Float {
        return start + (end - start) * percentage
    }

    /**
     * @param percentage
     * da 0 a 1
     * @return
     */
    fun interpolate(percentage: Float, type: ColorRangeType): Int {
        var color = 0
        if (type.isWorkingOnHSV) {
            Color.colorToHSV(startColor, tempStart)
            Color.colorToHSV(endColor, tempEnd)
            tempRange[0] = if (type.isEnable(ColorRangeMasks.MASK_HUE)) ramp(tempStart[0], tempEnd[0], percentage) else tempStart[0]
            tempRange[1] = if (type.isEnable(ColorRangeMasks.MASK_SAT)) ramp(tempStart[1], tempEnd[1], percentage) else tempStart[1]
            tempRange[2] = if (type.isEnable(ColorRangeMasks.MASK_VALUE)) ramp(tempStart[2], tempEnd[2], percentage) else tempStart[2]
            color = Color.HSVToColor(tempRange)
        } else if (type.isWorkingOnRGB) {
            tempStart[0] = Color.red(startColor).toFloat()
            tempStart[1] = Color.green(startColor).toFloat()
            tempStart[2] = Color.blue(startColor).toFloat()
            tempEnd[0] = Color.red(endColor).toFloat()
            tempEnd[1] = Color.green(endColor).toFloat()
            tempEnd[2] = Color.blue(endColor).toFloat()
            tempRange[0] = if (type.isEnable(ColorRangeMasks.MASK_RED)) ramp(tempStart[0], tempEnd[0], percentage) else tempStart[0]
            tempRange[1] = if (type.isEnable(ColorRangeMasks.MASK_GREEN)) ramp(tempStart[1], tempEnd[1], percentage) else tempStart[1]
            tempRange[2] = if (type.isEnable(ColorRangeMasks.MASK_BLUE)) ramp(tempStart[2], tempEnd[2], percentage) else tempStart[2]
            color = Color.rgb(tempRange[0].toInt(), tempRange[1].toInt(), tempRange[2].toInt())
        }
        if (type.isWorkingOnAlphaChannel) {
            color = Color.argb(ramp(Color.alpha(startColor), Color.alpha(endColor), percentage), Color.red(color), Color.green(color), Color.blue(color))
        }
        return color
    }

    /**
     * @param alpha
     * from 0 to 255
     * @param hue
     * from 0 to 359
     * @param saturation
     * from 0 to 1
     * @param value
     * from 0 to 1
     */
    protected fun setColorHSV(alpha: Int, hue: Float, saturation: Int, value: Int): Int {
        val c = floatArrayOf(hue, saturation.toFloat(), value.toFloat())
        return Color.HSVToColor(alpha, c)
    }

    /**
     * @param alpha
     * from 0 to 255
     * @param red
     * from 0 to 255
     * @param green
     * from 0 to 255
     * @param blue
     * from 0 to 255
     */
    protected fun setColorRGB(alpha: Int, red: Int, green: Int, blue: Int): Int {
        return Color.argb(alpha, red, green, blue)
    }
}