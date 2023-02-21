package com.abubusoft.xenon.core.graphic

import android.graphics.Color
import java.util.*

object ColorHelper {
    private val temp = FloatArray(3)
    private fun fix(input: String): String {
        return if (input.length == 1) {
            "0$input"
        } else input
    }

    fun toARGBString(color: Int): String {
        var ret: String
        ret = "#"
        ret += fix(Integer.toHexString(Color.alpha(color)))
        ret += fix(Integer.toHexString(Color.red(color)))
        ret += fix(Integer.toHexString(Color.green(color)))
        ret += fix(Integer.toHexString(Color.blue(color)))
        return ret.uppercase(Locale.getDefault())
    }

    /**
     * @param hue
     * @param saturation
     * @param value
     * @return
     */
    fun hsv(hue: Int, saturation: Int, value: Int): Int {
        return hsvNormalized(hue.toFloat(), saturation / 255f, value / 255f)
    }

    /**
     * I parametri
     * @param hue
     * [0 .. 360)
     * @param saturation
     * [0 .. 1]
     * @param value
     * [0 .. 1]
     * @return
     */
    fun hsvNormalized(hue: Float, saturation: Float, value: Float): Int {
        var hue = hue
        var saturation = saturation
        var value = value
        val color: Int
        hue = Math.max(0f, Math.min(360f, hue))
        saturation = Math.max(0f, Math.min(1f, saturation))
        value = Math.max(0f, Math.min(1f, value))
        temp[0] = hue
        temp[1] = saturation
        temp[2] = value
        color = Color.HSVToColor(temp)
        return color
    }

    /**
     * I parametri vanno da [0 .. 1]
     * @param red
     * @param green
     * @param blue
     * @return
     */
    fun rgbNormalized(red: Float, green: Float, blue: Float): Int {
        return rgb((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())
    }

    /**
     * I parametri vanno da [0 .. 255]
     *
     * @param red
     * @param green
     * @param blue
     * @return
     */
    fun rgb(red: Int, green: Int, blue: Int): Int {
        var red = red
        var green = green
        var blue = blue
        val color: Int
        red = Math.max(0, Math.min(255, red))
        green = Math.max(0, Math.min(255, green))
        blue = Math.max(0, Math.min(255, blue))
        temp[0] = red.toFloat()
        temp[1] = green.toFloat()
        temp[2] = blue.toFloat()
        color = Color.rgb(red, green, blue)
        return color
    }

    fun toHSV(color: Int): FloatArray {
        val ret = FloatArray(3)
        toHSV(color, ret)
        return ret
    }

    fun toHSV(color: Int, ret: FloatArray) {
        val r: Float
        val g: Float
        val b: Float
        r = Color.red(color) / 255f
        g = Color.green(color) / 255f
        b = Color.blue(color) / 255f
        val minRGB = Math.min(r, Math.min(g, b))
        val maxRGB = Math.max(r, Math.max(g, b))
        val computedH: Float
        val computedS: Float
        val computedV: Float

        // Black-gray-white
        if (minRGB == maxRGB) {
            computedV = minRGB
            ret[0] = 0f
            ret[1] = 0f
            ret[2] = computedV
            return
        }

        // Colors other than black-gray-white:
        val d = if (r == minRGB) g - b else if (b == minRGB) r - g else b - r
        val h: Float = if (r == minRGB) 3F else (if (b == minRGB) 1 else 5).toFloat()
        computedH = 60 * (h - d / (maxRGB - minRGB))
        computedS = (maxRGB - minRGB) / maxRGB
        computedV = maxRGB
        ret[0] = computedH
        ret[1] = computedS
        ret[2] = computedV
    }

    /**
     * Given H,S,L in range of 0-360, 0-1, 0-1 Returns a Color.
     *
     * @param hue
     * @param saturation
     * @param value
     * @return
     */
    fun fromHSV(hue: Float, sat: Float, lum: Float): Int {
        var color = 0
        val a = floatArrayOf(hue, sat, lum)
        color = Color.HSVToColor(a)
        return color
    }

    fun toRGBString(color: Int): String {
        var ret = "#"
        ret += fix(Integer.toHexString(Color.red(color)))
        ret += fix(Integer.toHexString(Color.green(color)))
        ret += fix(Integer.toHexString(Color.blue(color)))
        return ret.uppercase(Locale.getDefault())
    }

    fun toHSVString(color: Int): String {
        val ret = toHSV(color)
        return "{hue=" + ret[0] + ", sat=" + ret[1] + ",val=" + ret[2] + "}"
    }

    fun toAHSVString(color: Int): String {
        val ret = toHSV(color)
        return "{alpha=" + Color.alpha(color) + ", hue=" + ret[0] + ", sat=" + ret[1] + ",val=" + ret[2] + "}"
    }
}